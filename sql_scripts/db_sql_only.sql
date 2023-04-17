-- Adatbázis alapú rendszerek: Könyvesbolt
-- Bakk Ábel, Ocztos Károly Levente

CREATE SEQUENCE customer_seq
    START WITH 1
    INCREMENT BY 1
    CACHE 50;

CREATE TABLE customer (
                          customer_id         NUMBER(19)          DEFAULT customer_seq.NEXTVAL PRIMARY KEY,
                          email               VARCHAR2(255)       UNIQUE NOT NULL,
                          password            VARCHAR2(255)       NOT NULL,
                          first_name          VARCHAR2(30)        NOT NULL,
                          last_name           VARCHAR2(30)        NOT NULL,
                          created_at          DATE                NOT NULL,
                          last_login          DATE,
                          admin               NUMBER(1),
                          street              VARCHAR2(50),
                          city                VARCHAR2(50),
                          state_or_region     VARCHAR2(50),
                          postcode            VARCHAR2(10),
                          country             VARCHAR2(56),
                          regular_since       DATE
);

CREATE SEQUENCE wishlist_seq
    START WITH 1
    INCREMENT BY 1
    CACHE 5;

CREATE TABLE wishlist (
                          wishlist_id         NUMBER(19)          DEFAULT wishlist_seq.NEXTVAL PRIMARY KEY,
                          name                VARCHAR2(256)       NOT NULL,
                          created_at          DATE                NOT NULL,
                          customer_id         NUMBER(19)          REFERENCES customer(customer_id) ON DELETE CASCADE
);

CREATE SEQUENCE order_seq
    START WITH 1
    INCREMENT BY 1
    CACHE 20;

CREATE TABLE orders (
                        order_id            NUMBER(19)          DEFAULT order_seq.NEXTVAL PRIMARY KEY,
                        created_at          DATE                NOT NULL,
                        shipped             NUMBER(1),
                        pickup              NUMBER(1),
                        customer_id         NUMBER(19)          REFERENCES customer(customer_id) ON DELETE CASCADE
);

CREATE SEQUENCE invoice_seq
    START WITH 1
    INCREMENT BY 1
    CACHE 20;

CREATE TABLE invoice (
                         invoice_id          NUMBER(19)          DEFAULT invoice_seq.NEXTVAL PRIMARY KEY,
                         value               NUMBER(12),
                         payment_mode        VARCHAR2(30),
                         paid                NUMBER(1),
                         order_id            NUMBER(19)          REFERENCES orders(order_id) ON DELETE SET NULL
);

CREATE SEQUENCE book_seq
    START WITH 1
    INCREMENT BY 1
    CACHE 100;

CREATE TABLE book (
                      book_id             NUMBER(19)          DEFAULT book_seq.NEXTVAL PRIMARY KEY,
                      title               VARCHAR2(128)       NOT NULL,
                      description         VARCHAR2(2048),
                      cover               VARCHAR2(50),
                      weight              FLOAT,
                      price               NUMBER(10),
                      number_of_pages     NUMBER(5),
                      published_at        DATE,
                      publisher           VARCHAR2(50),
                      isbn                VARCHAR2(13)        UNIQUE NOT NULL,
                      language            VARCHAR2(16),
                      discounted_price    NUMBER(10)
);

CREATE TABLE author (
                        book_id             NUMBER(19)          REFERENCES book(book_id) ON DELETE CASCADE,
                        first_name          VARCHAR2(30),
                        last_name           VARCHAR2(30),
                        PRIMARY KEY (book_id, first_name, last_name)
);

CREATE TABLE genre (
                       book_id             NUMBER(19)          REFERENCES book(book_id) ON DELETE CASCADE,
                       genre_name          VARCHAR2(30),
                       PRIMARY KEY (book_id, genre_name)
);

CREATE SEQUENCE store_seq
    START WITH 1
    INCREMENT BY 1
    CACHE 10;

CREATE TABLE store (
                       store_id            NUMBER(19)          DEFAULT store_seq.NEXTVAL PRIMARY KEY,
                       name                VARCHAR2(50)        NOT NULL,
                       street              VARCHAR2(50),
                       city                VARCHAR2(50),
                       state_or_region     VARCHAR2(50),
                       postcode            VARCHAR2(10),
                       country             VARCHAR2(56)
);

CREATE TABLE stock (
                       book_id             NUMBER(19)          REFERENCES book(book_id) ON DELETE CASCADE,
                       store_id            NUMBER(19)          REFERENCES store(store_id) ON DELETE CASCADE,
                       count               NUMBER(6),
                       PRIMARY KEY (book_id, store_id)
);

CREATE TABLE partof (
                        book_id             NUMBER(19)          REFERENCES book(book_id) ON DELETE CASCADE,
                        wishlist_id         NUMBER(19)          REFERENCES wishlist(wishlist_id) ON DELETE CASCADE,
                        added_at             DATE,
                        PRIMARY KEY (book_id, wishlist_id)
);

CREATE TABLE contains (
                          order_id            NUMBER(19)          REFERENCES orders(order_id) ON DELETE CASCADE,
                          book_id             NUMBER(19)          REFERENCES book(book_id) ON DELETE CASCADE,
                          count               NUMBER(6),
                          PRIMARY KEY (order_id, book_id)
);

CREATE SEQUENCE business_hours_seq
    START WITH 1
    INCREMENT BY 1
    CACHE 20;

CREATE TABLE business_hours (
                                hours_id            NUMBER(19)          DEFAULT business_hours_seq.NEXTVAL PRIMARY KEY,
                                day_of_week         NUMBER(1, 0),
                                opening_time        VARCHAR2(30),
                                closing_time        VARCHAR2(30),
                                store_id            NUMBER(19)          REFERENCES store(store_id) ON DELETE CASCADE
);

CREATE SEQUENCE notification_seq
    START WITH 1
    INCREMENT BY 1
    CACHE 10;

CREATE TABLE notification (
                              notification_id     NUMBER(19)          DEFAULT notification_seq.NEXTVAL PRIMARY KEY,
                              message             VARCHAR2(256),
                              customer_id         NUMBER(19)          REFERENCES customer(customer_id) ON DELETE CASCADE
);

CREATE OR REPLACE PROCEDURE invoice_belongs_to_customer
(in_invoice_id IN NUMBER, in_customer_id IN NUMBER, out_result OUT NUMBER)
    IS
    curr_order orders%ROWTYPE;
BEGIN

    SELECT o.order_id, o.created_at, o.shipped, o.pickup, o.customer_id
    INTO curr_order
    FROM orders o
    JOIN invoice i ON o.order_id = i.order_id
    WHERE i.invoice_id = in_invoice_id;

    IF curr_order.customer_id = in_customer_id THEN
        out_result := 1;
    ELSE
        out_result := 0;
    END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        out_result := 0;
END invoice_belongs_to_customer;

CREATE OR REPLACE PROCEDURE stock_status_per_book
(in_book_id IN NUMBER, out_status OUT VARCHAR2)
    IS
    number_of_books NUMBER;
BEGIN

    SELECT SUM(COALESCE(st.count, 0))
    INTO number_of_books
    FROM stock st
    WHERE st.book_id = in_book_id
    GROUP BY st.book_id;

    IF number_of_books > 5 THEN
        out_status := 'ON_STOCK';
    ELSIF number_of_books > 0 THEN
        out_status := 'FEW_REMAINING';
    ELSE
        out_status := 'NONE';
    END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        out_status := 'NONE';
END stock_status_per_book;

CREATE OR REPLACE PROCEDURE store_size
(in_store_id IN NUMBER, out_status OUT VARCHAR2)
    IS
    number_of_books NUMBER;
BEGIN

    SELECT SUM(COALESCE(st.count, 0))
    INTO number_of_books
    FROM stock st
    WHERE st.store_id = in_store_id
    GROUP BY st.store_id;

    IF number_of_books > 100 THEN
        out_status := 'LARGE_STORE';
    ELSIF number_of_books > 30 THEN
        out_status := 'MEDIUM_STORE';
    ELSE
        out_status := 'SMALL_STORE';
    END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        out_status := 'SMALL_STORE';
END store_size;

CREATE OR REPLACE PROCEDURE unsold_books
(number_of_books OUT NUMBER)
    IS
BEGIN

    SELECT COUNT(b.book_id)
    INTO number_of_books
    FROM book b
    WHERE b.book_id NOT IN (
        SELECT c.book_id
        FROM contains c
    );

END unsold_books;

CREATE OR REPLACE PROCEDURE revenue_per_month
(in_start_date IN DATE, in_end_date IN DATE, out_result OUT NUMBER)
    IS
BEGIN

    SELECT SUM(i.value)
    INTO out_result
    FROM invoice i
             JOIN orders o ON i.order_id = o.order_id
    WHERE o.created_at >= in_start_date
      AND o.created_at < in_end_date;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        out_result := 0;
END revenue_per_month;

--INSERT INTO VALUES ();

--BOOK--

INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Metró 2033', 'Az egész világ romokban hever. Az emberiség majdnem teljesen elpusztult. Moszkva szellemvárossá változott, megmérgezte a radioaktív sugárzás, és szörnyek népesítik be. A kevés életben maradt ember a moszkvai metróban bújik meg - a Föld legnagyobb atombombabiztos óvóhelyén. A metró állomásai most városállamok, az alagutakban sötétség honol, és borzalom fészkel. Artyomnak az egész metróhálózaton át kell jutnia, hogy megmentse a szörnyű veszedelemtől az állomását, sőt talán az egész emberiséget.', 'füles kartonált', 540, 3990, 440, '27-JUN-16', 'Európa Könyvkiadó', 9630791397, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Vének háborúja', 'Az ember már űrbéli gyarmatokon él, és folytat kegyetlen harcokat újabbakért a Gyarmati Véderő vezetésével. Ez mindenképp szükséges, ugyanis az erőforrások szűkösek, lakható bolygókból pedig kevés van, és emiatt a kivándorlást erősen korlátozzák a Földről. Sokaknak csak az a lehetőség marad, hogy a hetvenöt éves kort elérve jelentkezzenek katonának, majd két év frontszolgálat után letelepedjenek valamelyik kolónián, busásan megjutalmazva. Nem csak emiatt hangzik csábítónak ez a lehetőség: a közvélekedés szerint a Gyarmati Véderő valamilyen módszerrel meg is fiatalítja az embereket, hiszen öregekkel nem lehet harcba indulni. Úgyhogy John Perry két dolgot tett a hetvenötödik születésnapján. Meglátogatta a felesége sírját, majd belépett a hadseregbe. Csak két évet kell túlélnie. Azt viszont még ő sem sejti, hogy milyen megpróbáltatások várnak rá - a brutális háborúk és az otthon fényévekre nyúló távolsága örökké megváltoztatja az embert. Valami sokkal különösebbé és veszélyesebbé.', 'puhatáblás', 294, 3280, 252, '16-AUG-19', 'Agave Könyvek', 9789634196297, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Szellemhadtest', 'Az emberiséget soha nem látott veszély fenyegeti, űrbéli gyarmatai ellen három intelligens faj szövetkezik. Három faj és egyetlen ember. Az emberiség egyetlen reménye az, hogy az áruló tudós a dezertálása során hátrahagyta az elméjéről készült biztonsági másolatot. De senki nem tudja, hogyan lehetne azt felhasználni. A Gyarmati Véderő Szellemhadteste soha nem az erkölcsi aggályairól volt híres, így megpróbálja egy klónkatonájába ültetni a lemásolt lelket... elvégre a háborút nekik kell megakadályozni, és ezért a célért mindenre hajlandóak.', 'puhatáblás', 350, 3880, 291, '01-NOV-19', 'Agave Könyvek', 9789634196303, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Az utolsó gyarmat', 'Katonai szolgálata végeztével John Perry leszerelt, és eseménytelen veteránéveit Huckleberry távoli bolygóján tengeti, ahol a Gyarmati Szövetség falusi békebírójaként a telepesek tyúk- és kecskepereiben szolgáltat igazságot. A korábban a Különleges Erőknél szolgáló feleségével, Jane Sagannel él együtt, több hektáros tanyájukon földet művelnek, és büszkeségtől dagadó kebellel figyelik, hogyan cseperedik örökbefogadott lányuk, Zoë. A falusi idill nyolc évét azonban egy csapásra fenekestül felforgatja, amikor a Gyarmati Szövetség fejese kopogtat a portájukon. Az emberek gyarmatbirodalma ugyanis politikai válság szélére sodródott: a maroknyi telepes segítségével létesített egykori kolóniák az évek során annyira megerősödtek, hogy egy ideje maguk is gyarmatosítási jogot követelnek saját bolygóiknak. Ki más lehetne rátermettebb kormányzója a baljós nevű Roanoke-ra készülő, tíz külön világról érkezett, önfejű telepesnek, mint Perry, a békebíró és az ellentmondást nem tűrő Sagan?', 'puhatáblás', 350, 3880, 294, '03-JAN-23', 'Agave Könyvek', 9789634196310, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('2084', 'Mit tartogat számunkra a 2084-es év? A mesterséges intelligencia fejlesztése messze nem csupán az informatikusokat érinti, hanem kihat az egész emberiségre. John C. Lennox, az Oxfordi Egyetem nyugalmazott matematikaprofesszora 2084 című könyvében bemutatja, mit tud ma és mire lehet képes a technológia, majd bibliai nézőpontból értékeli az olyan népszerű jövőkutatók és sci-fi írók elképzeléseit, mint Harari, Dan Brown, Tegmark vagy C. S. Lewis.', 'kartonált', 350, 3315, 240, '04-NOV-21', 'Harmat Kiadó', 9789632886719, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('2084', 'What will the year 2084 hold for you--for your friends, for your family, and for our society? Are we doomed to the grim dystopia imagined in George Orwell`s 1984? In 2084, scientist and philosopher John Lennox will introduce you to a kaleidoscope of ideas: the key developments in technological enhancement, bioengineering, and, in particular, artificial intelligence. You will discover the current capacity of AI, its advantages and disadvantages, the facts and the fiction, as well as potential future implications. The questions posed by AI are open to all of us. And they demand answers. A book that is written to challenge all readers, no matter your worldview, 2084 shows how the Christian worldview, properly understood, can provide evidence-based, credible answers that will bring you real hope for the future of humanity.', 'kartonált', 350, 3200, 240, '04-NOV-21', 'Harmat Kiadó', 9789632886718, 'angol', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Szamurájmesék', 'A könyv nyolc jellemző történetet tartalmaz, melyek mindegyike a szamurájerkölcsök, elvek, viselkedés és erkölcs, azaz a busidó elvei köré csoportosulnak, és a mai modern japán társadalmat is át- meg átszövik. Ezt hangsúlyozza Mijamori könyvének rövid bevezetőjében. A történetek nemcsak egzotikusak és borzongatók, de a japán néplélek megértésében, sőt, a modern japán írók, akár Rjúnoszuke Akutagava, Abe Kobo és az irodalmi Nobel-díjas Oe Kenzaburo vagy a hazánkban is méltán népszerű Murakami Haruki motivációinak, társadalmi-történeti-kulturális hátterének megértésében is segítenek. Arról nem is beszélve, hogy remek olvasmány valamennyi.', 'keménytáblás', 320, 3350, 176, '1-DEC-22', 'Digi-Book Kiadó', 9789635596997, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Az első ember - Neil Armstrong élete', '1969. JÚLIUS 21-ÉN AZ EGÉSZ VILÁG LÉLEGZETVISSZAFOJTVA FIGYELTE, amint egy 38 éves amerikai űrhajós, Neil A. Armstrong első emberként a Holdra lép. Talán nincsenek az emberi történelemben ismertebb szavak azoknál, amelyeket abban a történelmi pillanatban kimondott. Az első ember egyaránt bemutatja Armstrongot, mint kiváló űrhajóst és mint egyedülálló egyéniséget.', 'keménytáblás', 550, 4740, 560, '12-OCT-18', 'Gabo Kiadó', 9789632521121, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Birmingham Bandája', 'A Birmingham Bandája tv-sorozat 2013-as megjelenése óta egy szűk réteg kultuszsorozatából világjelenséggé nőtte ki magát: hat évadot tudhat már maga mögött, és Tommy Shelby karaktere világszerte ismertté vált. A sorozatról szóló első hivatalos kiadvány elénk tárja a Shelby család bámulatos felemelkedésének történetét, egyszersmind betekintést enged a sorozat alkotóinak és szereplőinek világába. A könyvben exkluzív interjút olvashatnak Steven Knighttal a várva várt hatodik és egyben utolsó évaddal kapcsolatban. Fedezzük fel együtt, mi vár a bandára a drámai fináléban!', 'keménytáblás', 410, 5941, 330, '15-MAR-22', 'Unio Mystica Kiadó', 9786155546549, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('A tizedik lány', 'Üdvözlünk mindenkit a Vaccaro Iskolában! Dél-Amerika legdélebbi csücskében egy mindentől elzárt leányiskolát találunk. Különös erők sújtják ezt a szentségtelen szikladarabot, a legenda szerint mindenki el lesz átkozva, aki itt merészel letelepedni. Mavi számára viszont ez a hely az egyetlen kiút, hiszen édesanyját elragadta a kormányzó hatalom, így a neki el kell hagynia otthonát, Buenos Airest. Fiatal tanárként itt kezdhet új életet, Argentína leggazdagabb leánynövendékei között. Megpróbálja elfogadni a nagy múltú intézmény idegenszerűségét. De amikor tíz diákja közül az egyik eltűnik, és mindenki úgy kezd viselkedni, mintha megszállták volna, nem tud többé nem tudomást venni a szentségtelen sziklán kísértő erőkről. Meg akarja fejteni a hiányzó lány rejtélyét a figyelmeztetések ellenére is, hogy veszélyes éjszaka kószálnia. Ráadásul különös Másvilágiakról szóló pletykák is keringenek. Az egyik ilyen lélek, egy rejtélyes fiatalember olyan titkokat őriz, amelyek Mavinak puszta létezésére is hatással vannak.', 'kartonált', 550, 4250, 464, '06-OCT-22', 'Maxim Könyvkiadó', 9789634992905, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Agykontroll', 'Bill Hodges, a nyugdíjas zsaru és társa, Holly Gibney egy öngyilkosság ügyében nyomoznak, amelyhez köze lehet Brady Hartsfieldnek, az ördögi Mercedeses Gyilkosnak. Igaz, Brady öt évvel azelőtt, amikor többször is fejbe vágták a zsúfolt koncertcsarnokban, amelyet fel akart robbantani, olyan súlyos agysérülést szenvedett, hogy azóta is kórházban van, csak üresen bambul, és az orvosok szerint reménye sincs a gyógyulásra. Ám a 217-es szobában, ahol az ártalmatlannak tartott gyilkos vegetál, egy titokzatos, gonosz erő ébred, amely hozzásegítheti Bradyt, hogy bosszút álljon ellenségein - sőt az egész városon... A Mr. Mercedes és az Aki kapja, marja után itt a Bill Hodges-trilógia csattanós fináléja, amelyben a hősöknek immár egy természetfölötti képességgel rendelkező, sátáni alakkal kell megküzdeniük életre-halálra.', 'füles kartonált', 570, 5750, 512, '18-SEP-19', 'Európa Kiadó', 9789635041046, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('End of Watch', 'The trilogy began with Mr. Mercedes and Finders Keepers-In End of Watch, the diabolical "Mercedes Killer" drives his enemies to suicide, and if Bill Hodges and Holly Gibney don`t figure out a way to stop him, they`ll be victims themselves.For nearly six years, in Room 217 of the Lakes Region Traumatic Brain Injury Clinic, Brady Hartsfield has been in a persistent vegetative state. A complete recovery seems unlikely for the insane perpetrator of the "Mercedes Massacre," in which eight people were killed and many more maimed for life. But behind the vacant stare, Brady is very much awake and aware, having been pumped full of experimental drugs...scheming, biding his time as he trains himself to take full advantage of the deadly new powers that allow him to wreak unimaginable havoc without ever leaving his hospital room. Brady Hartsfield is about to embark on a new reign of terror against thousands of innocents, hell-bent on taking revenge against anyone who crossed his path-with…', 'puhakötésű', 570, 5750, 512, '12-AUG-17', 'Európa Kiadó', 9789635041045, 'angol', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Az ember utolsó története', 'Kis Norbert evolúciófilozófiai kisregénye utazás az emberi tudás, a képzelet és a valóság határvidékén. A könyvben a mesterséges intelligencia mögötti Tudat és az utolsó Tudós nyolc napon át beszélget, amiből kirajzolódik a Mindenség evolúciója. Hogyan kezdődött a létezés és az élet? Mi ad életet az anyagnak? Mi az értelme és a célja az élet fejlődésének? Mi lesz az emberi élettel, ha száz éven belül Földünk lakhatatlanná válik? Eszmecseréjük meglepően kézenfekvő választ ad a természettudományok kétségeire, az élet múltjának és jövőjének talányaira. A regény végén elénk tárul az a nap, amikor eldől az emberi élet földi sorsa.', 'puhatáblás', 400, 4299, 346, '29-JUL-22', 'Pallas Athéné Könyvkiadó', 9789635731268, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('A Vezércsel', 'Beth Harmon csendes, morózus és első pillantásra jelentéktelen nyolcéves kislány, akit édesanyja halála után árvaházba küldenek. Legfőbb társasága egy másik árva, és a gyermekotthon gondnoka, aki sakkozni tanítja. A sakk szép lassan Beth életének értelmévé válik. Idővel a fiatal tehetség a nyugtatókhoz és az alkoholhoz nyúl, hogy elmenekülhessen a valóság elől. De a sakkpartik során kiélesednek az érzékei, tisztán gondolkodik, és úgy érzi, hogy talán visszanyerheti az irányítást. Tizenhat éves lányként a hidegháborús években már az USA nyílt bajnokságán küzd a győzelemért. Azonban, miközben szakmai képességei egyre fejlődnek, a tét is növekszik, egyre félelmetesebbnek tűnik számára az elszigeteltsége, és egyre csábítóbbá válik a menekülés gondolata. És eljön az a pillanat, amikor a világ legjobb játékosával kell megküzdenie a Szovjetunióban. Képes lesz-e nyerni, vagy végül függőségei áldozatává válik?', 'ragasztott', 381, 3990, 381, '06-FEB-19', 'Metropolis Media Group Kft.', 9786155859281, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Kardok kora', 'Az istenekről kiderült, hogy ők is csak halandók, az elnyomott rhunák pedig fegyvert ragadtak uraik ellen. Ám a küzdelem még csak most kezdődik... Raithe, az Istenölő végzett egy fhreyjel, és ezzel kirobbantotta a lázadást, csakhogy a szolgaként tartott embereket ősidők óta megosztó viszályok meggátolják, hogy összefogjanak közös ellenségük ellen. De még ha a klánok egyesítenék is hadaikat, hogyan remélhetnék legyőzni az isteni varázserővel bíró fhreyjeket? A válasz a tengerentúlon rejlik egy messzi-messzi vidéken, amelyet egy magának való, konok faj népesít be, akik megvetik a fhreyeket és az embereket egyaránt. Mielőtt kifutnának az időből, Perszephoné kétségbeesett utazásra indul, hogy segítséget szerezzen kis csapatával, melynek tagja Suri, az ifjú látó, és Arion is. Útjuk egyenesen Elan sötét szívébe vezet, ahol egy ősi és félelmetes ellenfél várja őket...', 'kartonált', 620, 4245, 560, '04-DEC-21', 'Fumax Kiadó', 9789634702207, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('A Földre esett férfi', 'A férfi fontos küldetéssel érkezett Anthea távoli világából: a földi erőforrások segítségével kell kimenekítenie pusztuló bolygójáról fajának túlélőit. A háborúkban, természeti katasztrófákban megfogyatkozott antheaiak fejlett technológiáját pénzzé téve, identitását titokban tartva Newton nekifog egy űrhajó építésének. Ám miközben a küldetése teljesítésén dolgozik, szembesül vele, hogy a két világ közt több a hasonlóság, mint elsőre gondolta, és az ő sikerétől nemcsak a saját népe fennmaradása függ, hanem az emberiség sorsa is. Kérdés azonban, vajon egyetlen férfi - még ha egy fejlettebb világ örököse is - képes-e megbirkózni egy ilyen óriási teherrel, távol a szeretteitől, egy számára idegen bolygón.', 'kartonált', 340, 2800, 280, '17-JUN-22', 'Gabo Kiadó', 9789635662951, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('S.T.A.L.K.E.R. - Ház a Mocsárban', 'A 21. század elején a csernobili atomerőmű romjai körül létrejött a titokzatos, természetellenes Zóna, és a könnyű pénzkereset megszámlálhatatlan rajongója ideözönlött abban a reményben, hogy ritka, hatalmas pénzeket érő relikviákat talál. Nagyon hamar világossá vált azonban számukra, hogy bentről nem mindenki képes visszatérni. A Tüske névre hallgató stalkernek is megvoltak a saját elképzelései a meggazdagodásra. Nem akart áttörni a radioaktív területeken és a rejtélyes, kívánságokat teljesítő Monolitot övező halálos csapdákon, nem akart fosztogatókkal vagy kegyetlen mutánsokkal harcolni, egyszerűen csak ki akarta rabolni és meg akarta ölni a Mocsárban élő Doktort, aki önzetlenül kezelte a sebesült stalkereket. Tüske nem is sejtette, hogy emiatt nemcsak a stalker testvériségekkel, de az egész Zónával fog szembekerülni. Márpedig a Zóna ellen seki nem nyerhet...', 'ragasztott', 300, 2990, 323, '27-AUG-18', 'Metropolis Media Group Kft.', 9786155628832, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Hálóban', 'Aznap, amikor Finnországban két népszerű influencer rejtélyes körülmények között eltűnik, egy fiatal mangaruhás nő holttestét sodorja partra a tenger. Jessica Niemi nyomozó versenyt fut az idővel, hogy megtalálja a bűnügyek közti összefüggést a Boszorkányvadász szerzőjének hátborzongató új regényében. Hat hónap telt el azóta, hogy Jessica találkozott a titokzatos boszorkányszektával, és meghalt a főnöke. A gyilkossági csoport új vezetővel az élen egy különös eltűnéssorozat felderítésén dolgozik: úgy tűnik, egy olyan bűnügyi hálózat mozgatja a szálakat, amelynek tagjai az Instagramon szerveződnek. Ráadásul valaki szórakozik a rendőrséggel. Valaki, akinek esze ágában sincs abbahagyni a halálos játékot. Miközben Jessica elszántan igyekszik az ügyre összpontosítani, éjjelente rémálmok gyötrik, melyek hirtelen túlságosan is valóságosnak tűnnek. Mintha halott édesanyja mondani akarna neki valamit, és a szekta, amely őt sok évvel ezelőtt elemésztette, most Jessica életére törne...', 'kartonált', 340, 3999, 408, '28-OCT-21', 'Animus Könyvek', 9789633248973, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Óvakodj a múlttól', 'A végtelen, kihalt mezőkkel teli angol lápvidék baljóslatú, sötét hely, ahol bármikor eltévedhetünk a köd leszállta után. Ezen a kietlen vidéken szembesül a fiatal Matt Ballard nyomozó az emberi természet sötét oldalával, miután a mocsárból három meggyilkolt gyermek holtteste bukkan elő. 25 évvel később Ballard főfelügyelő már a visszavonuláson gondolkodik sikerekben gazdag karrierje után, ám a múlt közbeszól: egy ismeretlen feladó az azóta is megoldatlan gyilkosságokról küld autentikus fotókat, majd újabb gyermek holtteste kerül elő. Az igazi gyilkos tért vissza vajon, vagy beteg elméjű követőre talált? Akárki is áll az új esetek hátterében, ezúttal Matt Ballard is a célpontok egyike találja magát - elvégre egy embert többféle módon is meg lehet semmisíteni... A bűnügyi thriller mesterei közé tartozó Joy Ellis sodró lendületű regénye arra figyelmeztet, hogy múltunk a legváratlanabb pillanatokban kérhet számon minket.', 'kartonált', 290, 3699, 336, '23-NOV-19', 'Alexandra Kiadó', 9789634475965, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Fullasztó tél', 'A dermesztő nyár fullasztó télbe fordul. Dale Stewart azt hitte, gyerekkorában végleg megszabadult a sötétségtől, ám nagyobbat nem is tévedhetett volna. Az egykor köztiszteletben álló egyetemi oktató és regényíró élete kisiklott: felesége elhagyta, karrierje romokban, sőt egy katasztrofális viszonyt követően még öngyilkosságot is megkísérel. Úgy dönt, hogy mindent maga mögött hagy, és visszatér gyermekkora színhelyére, az egykor virágzó kisvárosnak számító Elm Haven összeaszott maradványai közé, ahol reményei szerint nyugalomra lel. Régi barátja házát bérli ki, hogy megírja élete első komoly regényét, ami történetesen a négy évtizeddel azelőtti nyár eseményeit dolgozza fel. Ám azzal, hogy a városka szélén álló, régóta elhagyatott házba vackolja be magát - ahol egykor legjobb barátja, az 1960 dermesztő nyarán szörnyű halált halt Duane McBride lakott -, csupán az eddig elkövetett hibáit tetézi. Mert nincs egyedül a házban: ide is követték saját bejáratú démonjai, akik valami szörnyű dologra készülnek. És a hó lassan mindent maga alá temet...', 'kartonált', 350, 4250, 400, '11-SEP-20', 'Alexandra Kiadó', 9789634477266, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('A Winter Haunting', 'A once-respected college professor and novelist, Dale Stewart has sabotaged his career and his marriage -- and now darkness is closing in on him. In the last hours of Halloween he has returned to the dying town of Elm Haven, his boyhood home, where he hopes to find peace in isolation. But moving into a long-deserted farmhouse on the far outskirts of town -- the one-time residence of a strange and brilliant friend who lost his young life in a grisly "accident" back in the terrible summer of 1960 -- is only the latest in his long succession of recent mistakes. Because Dale is not alone here. He has been followed to this house of shadows by private demons who are now twisting his reality into horrifying new forms. And a thick, blanketing early snow is starting to fall...', 'kartonált', 340, 3999, 400, '21-SEP-17', 'Alexandra Kiadó', 9789634477265, 'angol', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Gyilkos társas', 'Jody JJ Johnson, fiatal PR-szakértő hölgy éppen Los Angeles legfelkapottabb éttermében ebédel, amikor álarcos fegyveres ront be robbanómellényben, és túszul ejti a vendégeket - Hollywood leggazdagabb sztárjait. JJ arról híres, hogy minden problémát képes elsimítani, a közönséges emberekből pedig szupersztárt faragni. Ebben a helyzetben azonban ő is tanácstalan. Miközben híre megy a túszejtésnek, az áldozatok az életükért alkudoznak egy zavart elméjű férfival, akit semmi sem tud megakadályozni abban, hogy megszerezze, amit akar. A bomba a fegyveres szívritmusának változásától felrobban, ezért a megölése szóba sem jöhet. Mivel csak négy órája van, hogy megállítsa, JJ-nek gyorsan megoldást kell találnia, hogy a fenyegető rémálom valósággá ne váljon.', 'kartonált', 360, 4999, 448, '29-NOV-17', 'Könyvmolyképző Kiadó', 9789634571117, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Halállista', 'A Halállista az USA szigorúan titkos listája azokról, akik a világ biztonságát veszélyeztetik. A lista élén a Prédikátor áll, aki az interneten közvetített uszító beszédeivel arra buzdítja az iszlám híveit, hogy kövessenek el öngyilkos merényleteket a nyugati világ fontos vezetői ellen. Ennek nyomán szerte a világon szörnyű gyilkosságok történnek Isten nevében. Mintha csak valami rettenetes járvány szedné áldozatait. Ha valaki nem állítja meg ezt az őrületet, a világ káoszba zuhan. A szinte lehetetlen küldetéssel egy volt tengerészgyalogost bíznak meg, akit legközelebbi kollégái is csak Nyomkövetőként ismernek. A Nyomkövető egy zseniális, tinédzser hacker segítségével próbálja meg azonosítani a maszk mögött rejtőzködő Prédikátort. Az üldözés egyszerre zajlik a virtuális és a valós világban, de kétséges, hogy milyen eredménnyel zárul. Forsyth legújabb regénye bepillantást nyújt a terrorizmus lélektani motivációinak legsötétebb bugyraiba és a globális terrorizmus elleni harc titkos boszorkánykonyhájába.', 'kartonált', 208, 3999, 360, '12-JAN-23', 'General Press Kft.', 9789634527060, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('The Kill List', 'The Kill List: a top secret catalogue of names held at the highest level of the US government. On it, those men and women who would threaten the world`s security. And at the top of it, The Preacher, a radical Islamic cleric whose sermons inspire his followers to kill high profile Western targets in the name of God. As the bodies begin to pile up in America, Great Britain and across Europe, the message goes out: discover this man`s identity, locate him and take him out. Tasked with what seems like an impossible job is an ex-US marine who has risen through the ranks to become one of America`s most effective intelligence chiefs. Now known only as The Tracker, he must gather what scant evidence there is, collate it and unmask The Preacher if he is to prevent the next spate of violent deaths. Aided only by a brilliant teenaged hacker, he must throw out the bait and see whether his deadly target can be drawn from his lair...', 'kartonált', 208, 3799, 360, '7-FEB-19', 'General Press Kft.', 9789634527671, 'angol', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Hidegvér', 'Megcsonkított fiatalember testét rejtő, viharvert bőröndöt sodor a víz a Temze partjára. Erika Foster nyomozót sokkolja a látvány. Dolgozott már borzalmas ügyeken, de ilyet még sosem látott pályafutása során. Erika és csapata munkához lát, és sikerül összekötniük az ügyet egy másik áldozatéval - két héttel korábban egy hasonló bőröndben fiatal nő hullája került elő. A nyomozó hamar rájön, hogy olyan sorozatgyilkossal van dolga, aki máris a következő dobására készül. Ám alig talál használható nyomot, s eközben őt is brutális támadás éri. Persze Erikát semmi sem állíthatja meg. A holttestek száma egyre nő, továbbá kollégája, Marsh parancsnok ikerlányait elrabolják, így a tét minden eddiginél nagyobb. Meg tudja-e menteni a két ártatlan gyermeket, mielőtt még késő lesz? Kezd kifutni az időből, és ekkor újabb felkavaró felfedezést tesz... Lehet, hogy nem csak egy gyilkos van?', 'kartonált', 340, 3399, 434, '22-SEP-20', 'Insomnia', 9789634335726, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('A szerencsések', 'Amikor egy nő holttestére bukkannak az apátság romjainál, Imogen Evans nyomozó rögtön tudja, hogy a sorozatgyilkos ismét lecsapott. Fiona, akárcsak a korábbi áldozatok, békésen, nyitott szemmel, örökre az arcára fagyott mosollyal hever a hátán. Néhány mérföldnyire innen Ben Hofland, a gyermekét egyedül nevelő apa alig hiszi, hogy végre jóra fordul a sorsa. Válása után fiával együtt a szülővárosába menekült, ahol nehezen boldogulnak. Ám váratlanul minden megoldódni látszik: Bennek állást ajánlanak, a fiát zaklató kölykök pedig eltűnnek. A hónapokon át tartó mélyrepülés után a férfi úgy érzi, végre mellé szegődött a szerencse. Nem is sejti, hogy valaki figyeli őt és Ollie-t. Valaki, aki nem akar mást, csak boldogságot Bennek. Boldogságot... és halált.', 'kartonált', 340, 5220, 416, '28-JUN-21', 'Alexandra Kiadó', 9789634479192, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('The Lucky Ones', 'It was the happiest day of her life. Little did she know it was also the last. When a woman`s body is found in the grounds of a ruined priory, Detective Imogen Evans realises she is dealing with a serial killer—a killer whose victims appear to die in a state of bliss, eyes open, smiles forever frozen on their faces. A few miles away, single dad Ben Hofland believes his fortunes are changing at last. Forced to move back to the sleepy village where he grew up following the breakdown of his marriage, Ben finally finds work. What`s more, the bullies who have been terrorising his son, Ollie, disappear. For the first time in months, Ben feels lucky. But he is unaware that someone is watching him and Ollie. Someone who wants nothing but happiness for Ben. Happiness…and death.', 'kartonált', 320, 5250, 416, '21-JUL-18', 'Alexandra Kiadó', 9789634479387, 'angol', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Hat varjú', 'Ketterdam: nyüzsgő nemzetközi kereskedelmi csomópont, ahol jó pénzért minden megkapható Ezt pedig senki nem tudja jobban, mint Kaz Brekker, a kivételes tehetségű bűnöző. Kaz veszélyes küldetésre kap megbízást, és cserébe olyan gazdagságot kínálnak neki, ami legvadabb álmait is felülmúlja. Egyedül viszont képtelen sikerre vinni a vállalkozást...', 'kartonált', 470, 5225, 568, '04-DEC-18', 'Könyvmolyképző Kiadó', 9789634573951, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Six of Crows', 'Criminal prodigy Kaz Brekker is offered a chance at a deadly heist that could make him rich beyond his wildest dreams - but he can`t pull it off alone.', 'kartonált', 470, 5225, 568, '06-OCT-16', 'Könyvmolyképző Kiadó', 9789634579127, 'angol', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Az Órakulcs', 'Vasilisa egyszerű kamasz lány, átlagos élettel. Ám egy nap megtudja, hogy létezik egy titkos, elrejtett világ, melyben óramágusok, tündérek és más varázslények élnek. Mire felocsúdik, már egy életre-halálra menő játszma kellős közepén találja magát, és szerepet kell vállalnia a Nagy Idővarázslatban. Veszélyesnél veszélyesebb akadályokat kell legyőznie, miközben szép lassan rájön, ki áll teljes szívből mellette, és ki az, akitől óvakodnia kell. Barátság, bizalom, a könnyű és a helyes út közti választás nehézsége, az elhivatottság megtalálása, az akaraterő megedzése, az első szerelem bonyodalmai - ilyen és hasonló kérdések foglalkoztatják az Óramágusok sorozat kamasz szereplőit, ahogyan a való élet, a valódi világ kamasz lakóit is, éljenek bárhol.', 'puhatáblás', 370, 3399, 464, '03-NOV-20', 'Európa Kiadó', 9789635043279, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('A víz mélyén', 'A várost átszelő folyóból holtan húznak ki egy fiatal nőt. Néhány hónappal korábban egy sérülékeny tinédzser lány végezte ugyanott, ugyanígy. Előttük évszázadokon át asszonyok és lányok hosszú sora lelte halálát a sötét vízben, így a két friss tragédia régen eltemetett titkokat bolygat meg - és hoz felszínre. Az utolsó áldozat árván maradt, tizenöt éves lányának szembe kell néznie azzal, hogy félelmetes nagynénje lett a gondviselője, aki most kényszeredetten tér vissza oda, ahonnan annak idején elmenekült, és ahová szíve szerint soha nem tette volna be újra a lábát. A folyóparti ház eresztékei éjjelente hangosabban nyikorognak, a fal tövében susogó víz kísérteties neszekkel tölti meg az egyébként zavartalan csendet.', 'cérnafűzött, keménytáblás', 524, 3816, 367, '02-MAY-17', '21. Század Kiadó', 9786155638589, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Into The Water', 'A young woman is pulled dead from the river that runs through the city. A few months earlier, a vulnerable teenage girl had done it in the same place, in the same way. For centuries before them, a long line of women and girls died in the dark water, so the two recent tragedies stir up - and bring to the surface - long-buried secrets. The orphaned fifteen-year-old daughter of the last victim has to face the fact that her terrifying aunt has become her guardian, who is now forced to return to the place from which she fled at the time, and where she would never have set foot in again. The eaves of the house by the river creak louder at night, the rustling water at the base of the wall fills the otherwise undisturbed silence with eerie noises.', 'cérnafűzött, keménytáblás', 248, 4500, 448, '27-APR-16', '21. Század Kiadó', 9786155638556, 'angol', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('A jóslat - kötött', 'Ha valaki halottakkal álmodik, az bárki másnak egy nyugtalan éjszakát jelent, de nem Ninának, hisz számára ez figyelmeztetés. Épphogy túlélte az első csatát a Pokollal, újból keményen küzdenie kell, egyetemi hallgatóként is, és úgy is, mint gyakornok apja vállalatánál. Visszatérő rémálmai apja erőszakos haláláról mindennapossá válnak. Mivel Jared aggódik Nináért, hogy választ kapjon kérdéseire, visszalopja a Könyvet. Megkezdődik a harc az új ellenséggel szemben a régi barátok oldalán, és Jared legrosszabb rémálma látszik beigazolódni. Választás elé kerül: egyedül szálljon szembe a Pokollal vagy kezdjen háborút a Mennyel. A döntést egy váratlan esemény még inkább megnehezíti...', 'keménytáblás', 265, 2549, 324, '10-JUL-14', 'Maxim Könyvkiadó', 9789632614717, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('A néma csillag dala', 'Háború árnyéka vetül a világra. A világra, hol az elme veszélyesebb a kardnál, a víz egyet jelent a halállal, és aki a városfalon túlra lép, az maga válik prédává. A fűbőrűek az egyetlenek, akik még útját állhatnák a keletről gyűrűző fenyegetésnek, de sokak szerint már késő... Felmerülhet a kérdés: jobban tennék, ha feladnák? Ha porba dobnák a kristálykardot, hogy elejét vegyék a hasztalan vérontásnak? Noha a kivégzéstől megmenekített Kitert és kényszerű útitársait csak magukkal sodorták az események, meglehet, hogy ők jelentik a kiutat a kilátástalanságból. De célt érhet-e a csapat, miben ellenlábasok feszülnek egymásnak, ha egy halni vágyó mutatja az útjukat, a vérükre ácsingózó vadak sűrűjén át? Ők lennének az utolsók, kik az isteni hatalommal bíró uralkodó útjába állhatnak? A néma csillag dala műfaji határokat feszegető alkotás, amely szándéka szerint tudományos alapokon nyugvó világépítés.', 'kartonált', 390, 4666, 480, '08-FEB-23', 'Ad Librum Kiadó', 9786156440228, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Ne engedd el!', 'Lehet a vakáció maga a paradicsom, máskor meg gyilkos kalanddá fajul. Martial és Liane idilli környezetben töltik szabadságukat Réunion szigetén. A házaspár és hatéves kislányuk számára ezek a tökéletes boldogság napjai, percei. Türkizkék ég, kristálytiszta víz, pálmafák, langyos szellő... Aztán hirtelen eltűnik Liane Bellion. Délután három és négy között felmegy a szállodai szobába, és egyszerűen nem tér vissza. A hotel egyik alkalmazottja azt állítja, látta Martialt a szállodai folyosón a kérdéses időpontban. Nemsokára Martialnak is nyoma vész, a kislányukat sem találják. Az egész szigetet tűvé teszik értük. Vajon Martial gyilkolta meg a feleségét? Ha nem, akkor mi okozza a bűntudatát?', 'kartonált', 190, 4800, 270, '02-JUN-20', '21. Század Kiadó', 9786155915444, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('NE LACHE PAS MA MAIN', 'Martial Bellion verbringt mit Frau und sechsjähriger Tochter die Ferien auf La Réunion. Nach einem Streit zwischen den Eheleuten, verlässt seine Frau das Hotel und verschwindet spurlos. Auf Grund von Zeugenaussagen wird Martial zum Hauptverdächtigen. Er beschließt, zusammen mit seiner Tochter zu fliehen, um seine Frau wieder zu finden und die Geister aus seiner Vergangenheit endlich zu vertreiben.', 'kartonált', 190, 4830, 270, '04-JUN-17', '21. Század Kiadó', 9786155915633, 'francia', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('A testolvasó', 'A halottak arcán ott a történetük. Jude Fontaine nyomozót három évig tartották fogva egy földalatti cellában, az egyetlen kapcsolata a külvilággal a fogvatartója volt, az ő arcvonásainak olvasása adott értelmet az életének. Az tartotta életben, hogy megtanulta mit jelent a férfi minden egyes ránca, mozdulata, a tekintetén átfutó gondolata. A magány és a kínzás után Jude-ot az igazság utáni vágy hajtja - valamint az a fantasztikus képessége, amellyel képes nem csak az élők, de a holtak testéről is olvasni. A kollégák hezitálása ellenére Jude visszatér a gyilkosságiakhoz, és miközben új társa, Uriah Ashby nyomozó is kételkedik az alkalmasságában, sőt a férfinek is megvannak a maga titkai. Ám amikor egy sorozatgyilkos fiatal lányokra kezd vadászni, a nyomozók kénytelenek együttműködni, hogy megállítsák a veszélyes őrültet, mielőtt újból lecsap. Az őrülteket pedig senki nem ismeri annyira, mint Jude.', 'kartonált', 416, 2750, 360, '10-NOV-17', 'Könyvmolyképző Kiadó', 9789634571575, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('A búra alatt', 'Egy teljesen átlagos őszi napon a békés maine-i Chester`s Mill kisvárosát váratlanul, a derült égből egy láthatatlan erőtér zárja el a világ többi részétől. A hirtelen alászálló "búrába" belecsapódik egy repülőgép és lángoló roncsokban zuhan le az égből, egy kertészkedő asszony keze leszakad, a szomszédos városban dolgozó emberek nem tudnak hazajutni. Senki sem érti, mi ez a megmagyarázhatatlan jelenség, honnan jött, és mikor tűnik el. Aztán ahogy az emberek lassan felfogják, hogy a láthatatlan, de nagyon is valóságos akadály ott van és ott marad, az is kiderül, számukra, hogy Chester`s Mill nem is olyan békés hely. Dale Barbara, a városban rekedt iraki veterán és alkalmi szakács az események közepén találja magát. Néhány bátor ottani lakóval - a helyi újság tulajdonosával, Julia Shumwayjel, két lelkésszel, egy kórházi asszisztenssel, egy anyával és három bátor gyerekkel - kénytelen szembeszállni a város vezetését átvevő Big Jim Rennie-vel, aki még a gyilkosságtól sem riad vissza a vezetés megtartásához.', 'keménytáblás', 1210, 8999, 976, '30-NOV-22', 'Európa Kiadó', 9789635047215, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Tűz és vér', 'Több évszázaddal a Trónok harca eseményei előtt a Targaryen-ház - a Valyria végzetét egyedül túlélő sárkányúr család - Sárkánykőről hódította meg Westeros marakodó királyságait. A Tűz és Vér a Vastrón megalkotójától, Hódító Aegontól veszi fel a történet fonalát, és egészen a dinasztiát majdnem elpusztító polgárháborúig regéli el Westeros sorsfordító eseményeit. Mi történt valójában a Sárkányok tánca alatt? Miért vált Valyria oly halálos hellyé a Végzet után? Honnan származik Daenerys három sárkánytojása? Ezekre és még rengeteg más fontos kérdésre ad választ a Fellegvár egy tanult mestere által írt kihagyhatatlan krónika, melyet több mint nyolcvan illusztráció gazdagít. Az olvasók korábban már bepillantást nyerhettek a történet néhány részletébe, ám most tárul fel teljes egészében a Targaryenek cselszövéstől, rokongyilkosságoktól, pusztító csatáktól és természetesen sárkányoktól hemzsegő históriája a Tűz és Vér lapjain.', 'kartonált', 894, 5550, 696, '15-AUG-22', 'Alexandra Kiadó', 9789635823642, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Védtelen halandók', 'Eddig arra számítottunk, hogy csakis kívülről érkezhet az emberiséget fenyegető legnagyobb veszedelem. Tévedtünk. Rose Franklint, a zseniális tudóst egész felnőtt életében az hajtotta, hogy megtalálja a magyarázatot a rejtélyre - egy gigantikus, eltemetett fém kézre -, amelybe gyerekként botlott, pontosabban zuhant bele véletlenül a dél-dakotai Deadwood közelében. A felfedezés az egész bolygón tapasztalható, mindent feldúló változások láncreakcióját indította be. Rose és a Földvédelmi csapatok mindent megmozgattak, hogy mielőbbb kiismerjék a misztikus technológiát, csakhogy váratlanul óriásrobotok szállták meg a Föld legnépesebb városait, s gyilkolták milliójával a lakosságot. Bár Rose és az emberei végül gátat vetettek a mészárlásnak, elhárították a támadást, diadaluknak sokáig nem örülhettek. Ismeretlen támadóink visszavonultak, elhagyták a megrokkant bolygót... ám a tudóst és segítő kis csapatát magukkal vitték.', 'kartonált', 360, 3300, 368, '08-OCT-18', 'Agave Könyvek', 9789634195771, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('A 2117. áldozat', 'Assad már több mint tíz éve a Q-ügyosztály tagja, de társai alig tudnak róla valamit. Ám egy váratlan esemény - egy spanyol újságíró cikke egy tragikus körülmények között elhunyt asszonyról - kibillenti az egyensúlyából, és Rose segítségére van szüksége ahhoz, hogy ne omoljon teljesen össze. Mint kiderül, az elhunyt nő sorsa összefonódik Assad családjának sorsával, és az eseményeket az iraki Ghaalib, a férfi régi ellensége irányítja. Versenyfutás kezdődik az idővel, hogy Ghaalibot és társait megakadályozzák abban, hogy Európa szívében terrortámadásokat hajtsanak végre. Ezzel egy időben egy fiú, aki szobájába zárkózva a számítógépes játékok világában él, azt tervezi, hogy bosszút áll a mások szenvedései iránt közömbös dán társadalmon, és úgy dönt, hogy a szüleivel kezdi a sort... Carl Morcknek és munkatársainak minden ügyességüket és ravaszságukat be kell vetniük, hogy elhárítsák az ártatlan áldozatokra leselkedő veszélyt, és megmentsék Assad családját.', 'kartonált', 440, 4290, 437, '16-JUN-20', 'Animus Könyvek', 9789633247716, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('A hasadék', 'Ez a regény különös körülmények között született. A szerzőt egy korábbi misztikus thrillerének megjelenése után megkereste egy férfi, aki elmesélt neki egy hátborzongató történetet, ami egy elhagyatott elmegyógyintézet széfjében talált dokumentumokkal veszi kezdetét. Jozef Karika lejegyezte az elbeszélést, annak számos adatát ellenőrizte, majd saját jegyzeteivel ellátva kötetbe rendezte - ezt tartja most kezében az olvasó. A könyv lapjain kibontakozó tragikus és hajmeresztő eseménysor a világ elé tárja az egyik legnagyobb szlovákiai rejtélyt: az emberek megmagyarázhatatlan eltűnését a Tribecs-hegységben. Legenda lenne, szándékos hamisítás, vagy maga a kegyetlen valóság? Ezt a kérdést a lebilincselő rémtörténetet közreadó szerző is felteszi, az olvasóra bízva annak megválaszolását.', 'puhatáblás', 390, 3650, 384, '23-APR-21', 'Animus Könyvek', 9789633248270, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Solaris', '"Ti akartok az óceánnal szót érteni, mikor egymást sem értitek?" Stanisław Lem születésének századik évfordulójára jelentetjük meg minden idők egyik legsikeresebb, legvarázslatosabb, legnagyobb hatású sci-fijét, amelyből már három film is készült. Kris Kelvin pszichológus megérkezik egy távoli égitest, a Solaris felszíne fölött lebegő űrállomásra. A bolygót plazmaóceán borítja, amely különös intelligencia jeleit mutatja, de az emberek mindeddig sikertelenül próbáltak kapcsolatot teremteni vele. A pszichológus lassan felismeri a bolygó működésének titkát - vagy legalábbis megsejti, hogy milyen veszély leselkedik az emberekre: a plazmaóceán az őrületbe hajszolja a kutatókat: személyiségük legbelső, eltitkolt démonait szabadítja rájuk.', 'kartonált', 310, 3999, 265, '06-SEP-21', 'Helikon Kiadó', 9789634797722, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('A Föld szilánkjai', 'Az emberiség génmanipuláció segítségével megalkotta a tökéletes harcos elitet, a páncélos angyalokat, ám a Földet megtámadó holdnyi méretű ellenséggel szemben ők is tehetetlenek. A Föld pusztulása után az agysebészettel átalakított Közvetítőkre vár, hogy megmentsék az összes olyan fajt, amelyek csatlakoztak hozzájuk a harcban. Az űr csendjében a Közvetítők az elméjükkel képesek kommunikálni az ellenséggel. De miután az idegenek továbbállnak, az emberiség gyorsan megfeledkezik róluk. Idris, a maroknyi életben maradt Közvetítő egyike, egyetlen másodpercet sem öregedett vagy aludt, mióta a háború miatt megváltoztatták. Az egykori hősből most roncsvadász lett és igyekszik elkerülni a nagyhatalmak figyelmét, mivel számukra a képességei továbbra is kiemelt jelentőségűek. 50 évvel a háború után Idris és társai egy rutinfeladat során valami különlegeset fedeznek fel az űr egy elhagyatott részén. Egyértelmű, hogy ez az Építészek műve, de vajon azt is jelenti egyben, hogy az idegenek visszatérnek? És ha igen, miért?', 'puhatáblás', 686, 6995, 555, '06-OCT-22', 'Fumax Kiadó', 9789634702535, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Shards of Earth', 'With the help of genetic manipulation, humanity created the perfect warrior elite, the armored angels, but they are powerless against the moon-sized enemy attacking the Earth. After Earth`s destruction, it awaits the brain-engineered Mediators to rescue all the species that have joined them in the fight. In the silence of space, Mediators can communicate with the enemy with their minds. But after the aliens leave, humanity quickly forgets about them. Idris, one of the handful of Mediators left alive, hasn`t aged or slept a single second since being altered by the war. The former hero has now become a wrecker and tries to avoid the attention of the great powers, as his abilities are still of prime importance to them. 50 years after the war, Idris and his companions discover something special in a desolate part of space during a routine mission. It`s clearly the work of the Architects, but does it also mean the aliens are coming back? And if so, why?', 'puhatáblás', 404, 4980, 576, '20-NOV-20', 'Fumax Kiadó', 9789634702524, 'angol', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Tíz nap a világvége előtt', 'Miért nem tud hinni abban, hogy túlélik? Két robbanásfal halad egymással szemben, kilométerről kilométerre emésztik fel a Földet. Senki nem tudja, honnan erednek, de megállíthatatlanul közelednek egymás felé, hogy tíz nap múlva egyesüljenek. Menekültek áradata indul meg az Atlanti-óceán partja felé, ahol a legtovább lehet életben maradni. A véletlen egymás mellé sodor öt embert, három férfit és két nőt. Együtt vágnak neki a bedugult utaknak - és életük utolsó tíz napjának. Kezdetét veszi a könyörtelen visszaszámlálás. Egy letehetetlen road movie - térben és lélekben. És te? Te mit tennél, ha csak tíz napod maradna az életből?', 'kartonált', 414, 3999, 413, '25-MAR-22', 'Könyvmolyképző Kiadó', 9789634578185, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Sötét idők - Sötét elmék 1,5.', 'Néha sötétség is lakozik az emberben, és van, hogy az győz." Már több év telt el, mióta a járvány végigsöpört a világon, és gyermekek millióit megfertőzte. Sokuknak, akik életben maradtak, különleges képességei lettek. Mivel veszélyesnek ítélték őket, a többségüket rehabilitáció céljából bezárták. Ám akik még szabadon vannak, fejvadászok célpontjaivá váltak... Gabe élete az utóbbi időben maga a pusztulás. Egy olyan valaki számára, mint ő, egyetlen lehetőség van a tragikus múlt elfelejtésére, így elhagyja kisvárosát, és fejvadásznak áll. Azonban első célpontja egy fiatal lány, Zu, aki nem hajlandó megszólalni, így még nehezebbé teszi az amúgy sem könnyű munkát. Ahogy telik az idő, Gabe egyre inkább megszereti Zút, aki lassacskán olyan módon változtatja meg a fiú életét, amire egyikőjük sem számított.', 'kartonált', 120, 2199, 112, '28-NOV-19', 'PUBLISHER', 9789634990390, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Sömmi', 'élelem és reszketés járja át az alföldi tanyavilágot, a hatóságok tehetetlenek - ahol csak felbukkan Rózsa Sándor és bandája, ott rögvest egekbe szökik az eltulajdonított jószágok és lepuffantott pandúrok száma. Ám a legendás haramiavezér és önjelölt pusztai igazságosztó kezd belefásulni a betyáréletbe, és titkon már arról ábrándozik, hogy mundért öltve a becsület újtára lép. Ennek eléréséhez szó szerint még a lelkét is hajlandó eladni, és az ördögi paktumnak hála a Kegyelmes Paraszt végül bebocsátást nyer Kossuth Lajos táborába. Hamar kiderül azonban, hogy nem rablóból lesz a legjobb szabadságharcos...', 'kartonált', 210, 2499, 184, '05-JUN-23', 'Halikon Kiadó', 9789636200824, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Az amerikai farkas - Igaz történet túlélésről és megszállottságról', 'Mielőtt az ember vált a Föld urává, a farkasok uralkodtak. Ezek a fenséges teremtmények, melyek egykor Észak-Amerika minden részét meghódították, a kegyetlen vadászat következtében az 1920-as években szinte teljesen kipusztultak az Egyesült Államok területén. A természetvédők azonban az elmúlt évtizedekben visszatelepítették a farkast a Sziklás-hegységbe, szenvedélyes vitát robbantva ki, melynek tétje a Nyugat lelke.', 'kartonált', 386, 3299, 328, '11-JUN-18', 'Könyvmolyképző Kiadó', 9789634573449, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('American Wolf', 'In this vibrant work of nonfiction, a Texas Monthly writer goes into the mind—and heart—of a wolf. He tells the remarkable true story of O-Six, a wolf brought back to the Rockies by conservationists, as she fights hunters, cattle ranchers, and her own species for survival.', 'kartonált', 248, 7159, 320, '16-FEB-17', 'Könyvmolyképző Kiadó', 978634563666, 'angol', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Az utolsó vonat Londonba', 'A sötétségben megcsillan egy reménysugár: Truus Wijsmuller, a holland ellenállás egyik vezéralakja zsidó gyerekeket csempész ki a náci Németországból. Miután Hitler elfoglalta Ausztriát, ez a vállalkozás egyre veszélyesebbé válik. Európa-szerte lezárják az országhatárokat, és nem engedik be a menekültek tömegeit. Wijsmuller igyekszik minél több gyereket megmenteni. Versenyre kel az idővel: vajon ki lehet hozni Bécsből Zofie-Helene-t, Stephant és Stephan kisöccsét, Waltert, meg a hozzájuk hasonló ifjakat? Elérhető még London? Veszélyes és bizonytalan élet vár rájuk.', 'kartonált', 428, 4455, 462, '02-SEP-20', '21. Század Kiadó', 9786156122025, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('The last train to London', 'Based on true events, The Last Train to London tells the story of a Dutchwoman who, working with British and Austrian Jews, faces down Adolf Eichmann to rescue thousands of children from Nazi-occupied Vienna.', 'kartonált', 428, 4455, 462, '30-MAR-18', '21. Század Kiadó', 9786156122002, 'angol', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Öt nap', 'Már négy év óta pipálgatja a halála előtti teendőinek hosszú listáján a tételeket, és most, mindössze öt nappal előtte még mindig eszébe jutnak dolgok, amiket meg kellett volna tennie. Mara Nichols sikeres ügyvéd, szerető feleség és egy örökbe fogadott kislány boldog édesanyja. Élete maga a megtestesült amerikai álom, egészen addig, amíg rejtélyes rosszulléteivel orvoshoz nem fordul. A diagnózis hihetetlen. Scott Coffman középiskolai tanár. Úgy érzi, végre minden kívánsága teljesült. Felesége sokévnyi hiábavaló próbálkozás után gyermeket vár. És egy éven át nevelhetik Curtist, a nagyszájú nyolcéves kisfiút, akinek az anyja börtönbe került. Igazi család ők hárman, nemsokára négyen. Scottnak és Marának is öt napja maradt, hogy búcsút vegyen a szeretteitől. Hogyan lehet elviselni az elviselhetetlent? Hogyan lehet lemondani arról, ami a legfontosabb az életünkben? És mennyi áldozatot tudunk hozni a szeretteinkért?', 'puhatáblás', 360, 3315, 432, '13-APR-15', 'Európa Kiadó', 9789634051015, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('A barlang', 'Natalie, a fiatal amerikai barlangászlány 2015-ben egy ukrajnai expedíción megdöbbentő leletekre bukkan a föld alatt: cipőket, edényeket és egy gabonamalmot talál, meg egy héber feliratú arany nyakláncot. A nyomok egy zsidó nagycsaládhoz vezetnek, melynek tagjai 1942-ben ott találtak menedéket Hitler katonái elől. Az egyik túlélő, az ekkor már nyolcvankilenc éves Joscha Burker megeleveníti Natalie előtt a tizenévesen átélt üldöztetés történetét, hogyan tartotta benne a reményt családja bátorsága, összetartása és hite, valamint a gettóba zárt kedvese iránt érzett szerelme a legsötétebb időkben is. Azt is elárulja Natalie-nak, mit jelent a barlangban talált aranylánc szív alakú medáljára vésett, héber 91-es szám...', 'keménytáblás', 450, 4241, 288, '09-NOV-22', 'Magánkiadás', 9786150126234, 'magyar', null);
INSERT INTO BOOK (TITLE, DESCRIPTION, COVER, WEIGHT, PRICE, NUMBER_OF_PAGES, PUBLISHED_AT, PUBLISHER, ISBN, LANGUAGE, DISCOUNTED_PRICE) VALUES ('Érkezés a sötétségbe', 'Egy megmagyarázhatatlan csillagközi esemény alig pár óra alatt elpusztít minden szerves életet a Földön. Néhány ezer embernek azonban sikerül az utolsó pillanatban digitálisan lemásolnia a tudatát, így megmenekülnek a haláltól. Az apokalipszis után létezésük célját keresve munkagépekbe, katonai drónokba, szexrobotokba és más géptestekbe töltik le magukat, hogy új civilizációt építsenek a régi helyén. Ahogy telnek az évek, szövetségeket alapítanak, megvívják saját háborúikat, különféle célokat és ideológiákat dolgoznak ki, hol nosztalgiával tekintve a múltra, hol radikálisan szakítva vele. Grześ, a lengyel programozó kétségbeesetten próbálja feldolgozni a múlt elvesztését, közben végigkíséri az új emberiség formálódását és útkeresését. De vajon az apokalipszis túlélői még emberek, vagy már csak egy kipusztult faj árnyékai? A hús tesz bennünket emberré, vagy valami egészen más?', 'fűzött', 304, 3990, 299, '30-MAR-21', 'Gabo Könyvkiadó', 9789635660315, 'magyar', null);

--AUTHOR--
--INSERT INTO AUTHOR VALUES (ID, 'KNEV', 'VNEV');
INSERT INTO AUTHOR VALUES (1, 'Dimitry', 'Glukhovsky');
INSERT INTO AUTHOR VALUES (2, 'John', 'Scalzi');
INSERT INTO AUTHOR VALUES (3, 'John', 'Scalzi');
INSERT INTO AUTHOR VALUES (4, 'John', 'Scalzi');
INSERT INTO AUTHOR VALUES (5, 'John C.', 'Lennox');
INSERT INTO AUTHOR VALUES (6, 'John C.', 'Lennox');
INSERT INTO AUTHOR VALUES (7, 'Astro', 'Mijamori');
INSERT INTO AUTHOR VALUES (8, 'James R.', 'Hansen');
INSERT INTO AUTHOR VALUES (9, 'Matt', 'Allen');
INSERT INTO AUTHOR VALUES (9, 'Steven', 'Knight');
INSERT INTO AUTHOR VALUES (10, 'Sara', 'Faring');
INSERT INTO AUTHOR VALUES (11, 'Stephen', 'King');
INSERT INTO AUTHOR VALUES (12, 'Stephen', 'King');
INSERT INTO AUTHOR VALUES (13, 'Norbert', 'Kis');
INSERT INTO AUTHOR VALUES (14, 'Walter', 'Tevis');
INSERT INTO AUTHOR VALUES (15, 'Michael J.', 'Sullivan');
INSERT INTO AUTHOR VALUES (16, 'Walter', 'Tevis');
INSERT INTO AUTHOR VALUES (17, 'Alekszej', 'Kalugin');
INSERT INTO AUTHOR VALUES (18, 'Max', 'Seeck');
INSERT INTO AUTHOR VALUES (19, 'Ellis', 'Joy');
INSERT INTO AUTHOR VALUES (20, 'Dan', 'Simmons');
INSERT INTO AUTHOR VALUES (21, 'Dan', 'Simmons');
INSERT INTO AUTHOR VALUES (22, 'J. S.', 'Carol');
INSERT INTO AUTHOR VALUES (23, 'Frederick', 'Forsyth');
INSERT INTO AUTHOR VALUES (24, 'Frederick', 'Forsyth');
INSERT INTO AUTHOR VALUES (25, 'Robert', 'Bryndza');
INSERT INTO AUTHOR VALUES (26, 'Mark', 'Edwards');
INSERT INTO AUTHOR VALUES (27, 'Mark', 'Edwards');
INSERT INTO AUTHOR VALUES (28, 'Leigh', 'Bardugo');
INSERT INTO AUTHOR VALUES (29, 'Leigh', 'Bardugo');
INSERT INTO AUTHOR VALUES (30, 'Sherba', 'Natalia');
INSERT INTO AUTHOR VALUES (31, 'Paula', 'Hawkins');
INSERT INTO AUTHOR VALUES (32, 'Paula', 'Hawkins');
INSERT INTO AUTHOR VALUES (33, 'Jamie', 'McGurie');
INSERT INTO AUTHOR VALUES (34, 'T. B.', 'Byrt');
INSERT INTO AUTHOR VALUES (35, 'Michel', 'Bussi');
INSERT INTO AUTHOR VALUES (36, 'Michel', 'Bussi');
INSERT INTO AUTHOR VALUES (37, 'Anne', 'Frasier');
INSERT INTO AUTHOR VALUES (38, 'Stephen', 'King');
INSERT INTO AUTHOR VALUES (39, 'George R. R.', 'Martin');
INSERT INTO AUTHOR VALUES (40, 'Sylvain', 'Neuvel');
INSERT INTO AUTHOR VALUES (41, 'Jussi', 'Adler-Olsen');
INSERT INTO AUTHOR VALUES (42, 'Jozef', 'Karika');
INSERT INTO AUTHOR VALUES (43, 'Stanislaw', 'Lem');
INSERT INTO AUTHOR VALUES (44, 'Adrian', 'Tchaikovsky');
INSERT INTO AUTHOR VALUES (45, 'Adrian', 'Tchaikovsky');
INSERT INTO AUTHOR VALUES (46, 'Manon', 'Fargetton');
INSERT INTO AUTHOR VALUES (47, 'Alexandra', 'Bracken');
INSERT INTO AUTHOR VALUES (48, 'András', 'Cserna-Szabó');
INSERT INTO AUTHOR VALUES (49, 'Nate', 'Blakeslee');
INSERT INTO AUTHOR VALUES (50, 'Nate', 'Blakeslee');
INSERT INTO AUTHOR VALUES (51, 'Meg Waite', 'Clayton');
INSERT INTO AUTHOR VALUES (52, 'Meg Waite', 'Clayton');
INSERT INTO AUTHOR VALUES (53, 'Julie Lawson', 'Timmer');
INSERT INTO AUTHOR VALUES (54, 'Damaris', 'Kofmehl');
INSERT INTO AUTHOR VALUES (55, 'Jacek', 'Dukaj');

--GENRE--
--INSERT INTO GENRE VALUES (ID, 'GENRE_NAME');
INSERT INTO GENRE VALUES (1, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (1, 'sci-fi');
INSERT INTO GENRE VALUES (2, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (2, 'sci-fi');
INSERT INTO GENRE VALUES (3, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (3, 'sci-fi');
INSERT INTO GENRE VALUES (4, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (4, 'sci-fi');
INSERT INTO GENRE VALUES (5, 'sci-fi');
INSERT INTO GENRE VALUES (5, 'társadalomtudomány');
INSERT INTO GENRE VALUES (6, 'sci-fi');
INSERT INTO GENRE VALUES (6, 'társadalomtudomány');
INSERT INTO GENRE VALUES (7, 'szépirodalom');
INSERT INTO GENRE VALUES (7, 'történelmi');
INSERT INTO GENRE VALUES (8, 'életrajz');
INSERT INTO GENRE VALUES (9, 'életrajz');
INSERT INTO GENRE VALUES (10, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (10, 'krimi');
INSERT INTO GENRE VALUES (10, 'thriller');
INSERT INTO GENRE VALUES (11, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (11, 'krimi');
INSERT INTO GENRE VALUES (11, 'thriller');
INSERT INTO GENRE VALUES (12, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (12, 'kimi');
INSERT INTO GENRE VALUES (12, 'thiller');
INSERT INTO GENRE VALUES (13, 'természettudomány');
INSERT INTO GENRE VALUES (13, 'biológia');
INSERT INTO GENRE VALUES (14, 'szépirodalom');
INSERT INTO GENRE VALUES (14, 'regény');
INSERT INTO GENRE VALUES (15, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (15, 'fantasy');
INSERT INTO GENRE VALUES (16, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (16, 'sci-fi');
INSERT INTO GENRE VALUES (17, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (17, 'sci-fi');
INSERT INTO GENRE VALUES (18, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (18, 'krimi');
INSERT INTO GENRE VALUES (18, 'thiller');
INSERT INTO GENRE VALUES (19, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (19, 'krimi');
INSERT INTO GENRE VALUES (19, 'thriller');
INSERT INTO GENRE VALUES (20, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (20, 'krimi');
INSERT INTO GENRE VALUES (20, 'thriller');
INSERT INTO GENRE VALUES (21, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (21, 'krimi');
INSERT INTO GENRE VALUES (21, 'thriller');
INSERT INTO GENRE VALUES (22, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (22, 'krimi');
INSERT INTO GENRE VALUES (22, 'thriller');
INSERT INTO GENRE VALUES (23, 'történelem');
INSERT INTO GENRE VALUES (23, 'egytemes történelem');
INSERT INTO GENRE VALUES (23, 'hadtörténet');
INSERT INTO GENRE VALUES (24, 'történelem');
INSERT INTO GENRE VALUES (24, 'egytemes történelem');
INSERT INTO GENRE VALUES (24, 'hadtörténet');
INSERT INTO GENRE VALUES (25, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (25, 'krimi');
INSERT INTO GENRE VALUES (25, 'thriller');
INSERT INTO GENRE VALUES (26, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (26, 'krimi');
INSERT INTO GENRE VALUES (26, 'thriller');
INSERT INTO GENRE VALUES (27, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (27, 'krimi');
INSERT INTO GENRE VALUES (27, 'thriller');
INSERT INTO GENRE VALUES (28, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (28, 'fantasy');
INSERT INTO GENRE VALUES (29, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (29, 'fantasy');
INSERT INTO GENRE VALUES (30, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (30, 'fantasy');
INSERT INTO GENRE VALUES (31, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (31, 'krimi');
INSERT INTO GENRE VALUES (31, 'thriller');
INSERT INTO GENRE VALUES (32, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (32, 'krimi');
INSERT INTO GENRE VALUES (32, 'thriller');
INSERT INTO GENRE VALUES (33, 'szépirodalom');
INSERT INTO GENRE VALUES (33, 'ifjúsági irodalom');
INSERT INTO GENRE VALUES (33, 'regény');
INSERT INTO GENRE VALUES (34, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (34, 'fantasy');
INSERT INTO GENRE VALUES (35, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (35, 'krimi');
INSERT INTO GENRE VALUES (35, 'thriller');
INSERT INTO GENRE VALUES (36, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (36, 'krimi');
INSERT INTO GENRE VALUES (36, 'thriller');
INSERT INTO GENRE VALUES (37, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (37, 'krimi');
INSERT INTO GENRE VALUES (37, 'thriller');
INSERT INTO GENRE VALUES (38, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (38, 'krimi');
INSERT INTO GENRE VALUES (38, 'thriller');
INSERT INTO GENRE VALUES (39, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (39, 'fantasy');
INSERT INTO GENRE VALUES (40, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (40, 'sci-fi');
INSERT INTO GENRE VALUES (41, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (41, 'krimi');
INSERT INTO GENRE VALUES (41, 'thriller');
INSERT INTO GENRE VALUES (42, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (42, 'krimi');
INSERT INTO GENRE VALUES (42, 'thriller');
INSERT INTO GENRE VALUES (43, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (43, 'sci-fi');
INSERT INTO GENRE VALUES (44, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (44, 'sci-fi');
INSERT INTO GENRE VALUES (45, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (45, 'sci-fi');
INSERT INTO GENRE VALUES (46, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (46, 'akció');
INSERT INTO GENRE VALUES (46, 'kaland');
INSERT INTO GENRE VALUES (47, 'szépirodalom');
INSERT INTO GENRE VALUES (47, 'ifjúsági irodalom');
INSERT INTO GENRE VALUES (47, 'regény');
INSERT INTO GENRE VALUES (48, 'szépirodalom');
INSERT INTO GENRE VALUES (48, 'romantikus');
INSERT INTO GENRE VALUES (48, 'kaland');
INSERT INTO GENRE VALUES (49, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (49, 'akció');
INSERT INTO GENRE VALUES (49, 'kaland');
INSERT INTO GENRE VALUES (50, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (50, 'akció');
INSERT INTO GENRE VALUES (50, 'kaland');
INSERT INTO GENRE VALUES (51, 'szépirodalom');
INSERT INTO GENRE VALUES (51, 'regény');
INSERT INTO GENRE VALUES (52, 'szépirodalom');
INSERT INTO GENRE VALUES (52, 'regény');
INSERT INTO GENRE VALUES (53, 'szépirodalom');
INSERT INTO GENRE VALUES (53, 'regény');
INSERT INTO GENRE VALUES (54, 'szépirodalom');
INSERT INTO GENRE VALUES (54, 'regény');
INSERT INTO GENRE VALUES (55, 'szórakoztató irodalom');
INSERT INTO GENRE VALUES (55, 'sci-fi');

--STORE--
--INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES (ID, 'STORE_NAME', 'STREET', 'CITY', 'REGION', 'POSTCODE', 'COUNTRY');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('BookAround Szeged Pláza Könyvesbolt', 'Kossuth Lajos sgrt. 119', 'Szeged', 'Csongrád-Csanád vármegye', '6724', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Bookaround Westend Könyvesbolt', 'Váci út 1-3', 'Budapest', 'Budapest', '1062', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Győri BookAround', 'Árpád út 60', 'Győr', 'Győr-Moson-Sopron vármegye', '9022', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Debreceni Kis-Bookaround', 'Aulich utca 7', 'Debrecen', 'Hajdú-Bihar-Bereg vármegye', '4002', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Bookaround Debreceni Nagykönyvesbolt', 'Kerekerdő utca 11', 'Debrecen', 'Hajdú-Bihar-Bereg vármegye', '4020', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Dózsa György Bookaround', 'Dózsa György út 63', 'Miskolc', 'Borsod-Abaúj-Zemplén vármegye', '3528', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Pécsi Bookaround Könyvesbolt', 'Kossuth Lajos u. 5', 'Pécs', 'Baranya vármegye', '7634', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Acél Bookaround', 'Acél utca 40', 'Nyíregyháza', 'Szabolcs-Szatmár-Bereg vármegye', '4400', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Kecskeméti Tesco Bookaround Könyvesboltja', 'Levél utca 18', 'Kecskemét', 'Bács-Kiskun vármegye', '6044', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Gyimesi Könyvesbolt', 'Gyimesi utca 6', 'Székesfehérvár', 'Fejér vármegye', '8017', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Szombathelyi Bookaround', 'Hét vezér utca 28', 'Szombathely', 'Vas vármegye', '9700', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Bookaround Érdi Könyvesboltja', 'Tétényi út 11', 'Érd', 'Pest vármegye', '2030', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Szolnok Bookaround', 'Zerge utca 23', 'Szolnok', 'Jász-Nagykun-Szolnok vármegye', '5000', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Tatabányai Bookaround Könyvesbolt', 'Márna utca 55', 'Tatabánya', 'Komárom-Esztergom vármegye', '2800', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Sopron Bookaround', 'Selmeci utca 31', 'Sopron', 'Győr-Moson-Sopron vármegye', '9400', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Kaposvári Tesco Bookaround Könyvesboltja', 'Gróf Apponyi Albert utca 1-3', 'Kaposvár', 'Somogy vármegye', '7451', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Békéscsaba Bookaround Könyvesbolt', 'Fiumei utca 14', 'Békéscsaba', '	Békés vármegye', '5623', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Veszprémi Bookaround Könyvesbolt', 'József Attila utca 7', 'Veszprém', 'Veszprém vármegye', '8200', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Bookaround Zalaegerszeg', 'Csavargyár u. 47', 'Zalaegerszeg', 'Zala vármegye', '8900', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Egri Bookaround Könyvesbolt', 'Hősök utca 7', 'Eger', 'Heves vármegye', '3304', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Bookaround Hódmezővásárhelyi Kisüzlet', 'Őszirózsa utca 1', 'Hódmezővásárhely', 'Csongrád-Csanád vármegye', 'Lepke utca', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Lepke Bookaround', 'Lepke utca 30', 'Szeged', 'Csongrád-Csanád vármegye', '6753', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Ceglédi Bookaround Könyvesbolt', 'Borona utca 15', 'Cegléd', 'Pest vármegye', '2700', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Budapesti Istenhegyi Tesco Bookaround', 'Istenhegyi út 29', 'Budapest', 'Budapest', '1112', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Vác Bookaround Könyvesbolt', 'Csillag utca 32', 'Vác', 'Pest vármegye', '2600', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Ügető Bookaround', 'Ügető utca 71', 'Budapest', 'Budapest', '1124', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Bookaround Siófok', 'Dózsa György utca 8', 'Siófok', 'Somogy vármegye', '8600', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Budapest Ápád fejedelem Bookaround', ' Árpád fejedelem utca 10', 'Budapest', 'Budapest', '1162', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Szegedi Kis-Bookaround', 'Nagyrét út 7', 'Szeged', 'Csongrád-Csanád vármegye', '6721', 'Magyarország');
INSERT INTO STORE (NAME, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY) VALUES ('Budapesti Bookaround Csabai kapu Könyvesboltja', 'Csabai kapu 23', 'Budapest', 'Budapest', '1026', 'Magyarország');

--STOCK--
--INSERT INTO STOCK VALUES (book_id, store_id, count);
INSERT INTO STOCK VALUES (1, 1, 30);
INSERT INTO STOCK VALUES (2, 1, 20);
INSERT INTO STOCK VALUES (3, 1, 20);
INSERT INTO STOCK VALUES (4, 1, 30);
INSERT INTO STOCK VALUES (14, 1, 24);
INSERT INTO STOCK VALUES (28, 1, 25);
INSERT INTO STOCK VALUES (29, 1, 20);
INSERT INTO STOCK VALUES (31, 1, 25);
INSERT INTO STOCK VALUES (32, 1, 20);
INSERT INTO STOCK VALUES (38, 1, 35);
INSERT INTO STOCK VALUES (40, 1, 40);
INSERT INTO STOCK VALUES (41, 1, 35);
INSERT INTO STOCK VALUES (43, 1, 30);
INSERT INTO STOCK VALUES (52, 1, 25);
INSERT INTO STOCK VALUES (53, 1, 10);
INSERT INTO STOCK VALUES (55, 1, 30);
INSERT INTO STOCK VALUES (5, 2, 30);
INSERT INTO STOCK VALUES (7, 2, 25);
INSERT INTO STOCK VALUES (11, 2, 50);
INSERT INTO STOCK VALUES (20, 2, 45);
INSERT INTO STOCK VALUES (22, 2, 60);
INSERT INTO STOCK VALUES (23, 2, 20);
INSERT INTO STOCK VALUES (24, 2, 25);
INSERT INTO STOCK VALUES (40, 2, 32);
INSERT INTO STOCK VALUES (41, 2, 40);
INSERT INTO STOCK VALUES (9, 3, 20);
INSERT INTO STOCK VALUES (10, 3, 28);
INSERT INTO STOCK VALUES (15, 3, 31);
INSERT INTO STOCK VALUES (16, 3, 30);
INSERT INTO STOCK VALUES (18, 3, 34);
INSERT INTO STOCK VALUES (27, 3, 50);
INSERT INTO STOCK VALUES (28, 3, 48);
INSERT INTO STOCK VALUES (29, 4, 53);
INSERT INTO STOCK VALUES (30, 4, 36);
INSERT INTO STOCK VALUES (49, 4, 43);
INSERT INTO STOCK VALUES (50, 4, 47);
INSERT INTO STOCK VALUES (51, 4, 20);
INSERT INTO STOCK VALUES (52, 4, 25);
INSERT INTO STOCK VALUES (54, 4, 24);
INSERT INTO STOCK VALUES (6, 5, 26);
INSERT INTO STOCK VALUES (16, 5, 20);
INSERT INTO STOCK VALUES (17, 5, 17);
INSERT INTO STOCK VALUES (22, 5, 20);
INSERT INTO STOCK VALUES (24, 5, 30);
INSERT INTO STOCK VALUES (25, 5, 34);
INSERT INTO STOCK VALUES (33, 5, 23);
INSERT INTO STOCK VALUES (37, 5, 50);
INSERT INTO STOCK VALUES (47, 5, 47);
INSERT INTO STOCK VALUES (48, 5, 35);
INSERT INTO STOCK VALUES (8, 6, 25);
INSERT INTO STOCK VALUES (14, 6, 28);
INSERT INTO STOCK VALUES (18, 6, 31);
INSERT INTO STOCK VALUES (20, 6, 36);
INSERT INTO STOCK VALUES (22, 6, 61);
INSERT INTO STOCK VALUES (23, 6, 46);
INSERT INTO STOCK VALUES (32, 6, 28);
INSERT INTO STOCK VALUES (36, 6, 36);
INSERT INTO STOCK VALUES (37, 6, 20);
INSERT INTO STOCK VALUES (49, 6, 19);
INSERT INTO STOCK VALUES (50, 6, 10);
INSERT INTO STOCK VALUES (53, 6, 16);
INSERT INTO STOCK VALUES (10, 7, 36);
INSERT INTO STOCK VALUES (20, 7, 24);
INSERT INTO STOCK VALUES (30, 7, 64);
INSERT INTO STOCK VALUES (40, 7, 25);
INSERT INTO STOCK VALUES (20, 8, 80);
INSERT INTO STOCK VALUES (25, 8, 11);
INSERT INTO STOCK VALUES (30, 8, 36);
INSERT INTO STOCK VALUES (40, 8, 42);
INSERT INTO STOCK VALUES (7, 9, 13);
INSERT INTO STOCK VALUES (14, 9, 43);
INSERT INTO STOCK VALUES (20, 9, 35);
INSERT INTO STOCK VALUES (30, 10, 36);
INSERT INTO STOCK VALUES (47, 10, 36);
INSERT INTO STOCK VALUES (48, 10, 26);
INSERT INTO STOCK VALUES (55, 10, 10);
INSERT INTO STOCK VALUES (1, 11, 15);
INSERT INTO STOCK VALUES (11, 11, 70);
INSERT INTO STOCK VALUES (23, 15, 45);
INSERT INTO STOCK VALUES (26, 15, 50);
INSERT INTO STOCK VALUES (28, 15, 50);
INSERT INTO STOCK VALUES (40, 15, 50);
INSERT INTO STOCK VALUES (42, 15, 56);
INSERT INTO STOCK VALUES (10, 16, 45);
INSERT INTO STOCK VALUES (13, 16, 54);
INSERT INTO STOCK VALUES (26, 16, 37);
INSERT INTO STOCK VALUES (5, 17, 65);
INSERT INTO STOCK VALUES (41, 17, 25);
INSERT INTO STOCK VALUES (47, 17, 53);
INSERT INTO STOCK VALUES (30, 24, 27);
INSERT INTO STOCK VALUES (38, 24, 35);
INSERT INTO STOCK VALUES (39, 24, 45);
INSERT INTO STOCK VALUES (7, 27, 45);
INSERT INTO STOCK VALUES (19, 27, 45);
INSERT INTO STOCK VALUES (31, 27, 14);
INSERT INTO STOCK VALUES (41, 30, 16);
INSERT INTO STOCK VALUES (42, 30, 20);

--BUSINESS_HOURS--
INSERT INTO BUSINESS_HOURS (DAY_OF_WEEK, OPENING_TIME, CLOSING_TIME, STORE_ID) VALUES (1, '08:00 AM', '08:00 PM', 1);
INSERT INTO BUSINESS_HOURS (DAY_OF_WEEK, OPENING_TIME, CLOSING_TIME, STORE_ID) VALUES (2, '08:00 AM', '06:00 PM', 1);
INSERT INTO BUSINESS_HOURS (DAY_OF_WEEK, OPENING_TIME, CLOSING_TIME, STORE_ID) VALUES (3, '08:00 AM', '08:00 PM', 1);
INSERT INTO BUSINESS_HOURS (DAY_OF_WEEK, OPENING_TIME, CLOSING_TIME, STORE_ID) VALUES (4, '08:00 AM', '06:00 PM', 1);
INSERT INTO BUSINESS_HOURS (DAY_OF_WEEK, OPENING_TIME, CLOSING_TIME, STORE_ID) VALUES (5, '08:00 AM', '06:00 PM', 1);
INSERT INTO BUSINESS_HOURS (DAY_OF_WEEK, OPENING_TIME, CLOSING_TIME, STORE_ID) VALUES (1, '10:00 AM', '10:00 PM', 2);
INSERT INTO BUSINESS_HOURS (DAY_OF_WEEK, OPENING_TIME, CLOSING_TIME, STORE_ID) VALUES (2, '10:00 AM', '08:00 PM', 2);
INSERT INTO BUSINESS_HOURS (DAY_OF_WEEK, OPENING_TIME, CLOSING_TIME, STORE_ID) VALUES (3, '10:00 AM', '08:00 PM', 2);
INSERT INTO BUSINESS_HOURS (DAY_OF_WEEK, OPENING_TIME, CLOSING_TIME, STORE_ID) VALUES (4, '10:00 AM', '08:00 PM', 2);
INSERT INTO BUSINESS_HOURS (DAY_OF_WEEK, OPENING_TIME, CLOSING_TIME, STORE_ID) VALUES (5, '10:00 AM', '08:00 PM', 2);
INSERT INTO BUSINESS_HOURS (DAY_OF_WEEK, OPENING_TIME, CLOSING_TIME, STORE_ID) VALUES (6, '12:00 PM', '08:00 PM', 2);
INSERT INTO BUSINESS_HOURS (DAY_OF_WEEK, OPENING_TIME, CLOSING_TIME, STORE_ID) VALUES (7, '12:00 PM', '04:00 PM', 2);
INSERT INTO BUSINESS_HOURS (DAY_OF_WEEK, OPENING_TIME, CLOSING_TIME, STORE_ID) VALUES (1, '08:00 AM', '06:00 PM', 3);
INSERT INTO BUSINESS_HOURS (DAY_OF_WEEK, OPENING_TIME, CLOSING_TIME, STORE_ID) VALUES (2, '08:00 AM', '06:00 PM', 3);
INSERT INTO BUSINESS_HOURS (DAY_OF_WEEK, OPENING_TIME, CLOSING_TIME, STORE_ID) VALUES (4, '08:00 AM', '06:00 PM', 3);
INSERT INTO BUSINESS_HOURS (DAY_OF_WEEK, OPENING_TIME, CLOSING_TIME, STORE_ID) VALUES (5, '08:00 AM', '06:00 PM', 3);
INSERT INTO BUSINESS_HOURS (DAY_OF_WEEK, OPENING_TIME, CLOSING_TIME, STORE_ID) VALUES (6, '10:00 AM', '06:00 PM', 3);
INSERT INTO BUSINESS_HOURS (DAY_OF_WEEK, OPENING_TIME, CLOSING_TIME, STORE_ID) VALUES (7, '12:00 PM', '04:00 PM', 3);

--CUSTOMER--

--INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES (ID, 'EMAIL', 'PASSWORD', 'KNEV', 'VNEV', 'CREATED_AT', 'LAST_LOGIN', IS_ADMIN, 'STREET', 'CITY', 'STATE_OR_REGION', 'POSTCODE', 'COUNTRY', 'REGULAR_SINCE');
--Passwords from 3 to 30 are 'password' encoded form

INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('admin@bookaround.com', '$2a$12$phBRcK.bDG.6jLthGprAw.zGKLjErxfnkYiuKgBouMIfbGnd42ezG', 'John', 'Doe', '11-MAR-23', '11-MAR-23', 1, '905 John Calvin Drive', 'Oak Lawn', 'Illinois', '60453', 'United States of America', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('sallayambrus@rhyta.com', '$2a$12$phBRcK.bDG.6jLthGprAw.zGKLjErxfnkYiuKgBouMIfbGnd42ezG', 'Ambrus', 'Sallay', '11-MAR-23', '11-MAR-23', 0, '59 Belgrád rkp.', 'Vasvár', 'Vas vármegye', '9800', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('smidpanna@dayrep.com', '$2a$12$phBRcK.bDG.6jLthGprAw.zGKLjErxfnkYiuKgBouMIfbGnd42ezG', 'Panna', 'Smid', '11-MAR-23', '11-MAR-23', 0, '80 Kárpát utca', 'Lovászpatona', 'Veszprém vármegye', '8553', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('PallVanessza@jourrapide.com', '$2a$12$yriTdE6SPy1R7USsrj/mFObvFOPlPftmc7p2HXzJhNqRv4ZklOEwW', 'Vanessza', 'Páll ', '17-MAR-23', '17-MAR-23', 0, 'Kis Diófa u. 17.', 'Magyarszombatfa', 'Vas vármegye', '9946', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('FejesMarton@teleworm.us', '$2a$12$HUDd/d6lOwHxjENySxZituqY5SbsT17D1GZqJ6Sg9CVkl//lqHf5i', 'Márton', 'Fejes', '17-MAR-23', '17-MAR-23', 0, 'Erzsébet tér 52.', 'Parád', 'Heves vármegye', '3240', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('BerkyAtilla@armyspy.com', '$2a$12$fh2DE./Z7k0Mvy6lIuuvX.gtE3mJuoEcGsHjD3sYwrQMSAxG8qMAG', 'Attila', 'Berky', '17-MAR-23', '17-MAR-23', 0, 'Szent Gellért tér 76.', 'Kiscsécs', 'Borsod-Abaúj-Zemplén vármegye', '3578', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('SzolosiKata@rhyta.com', '$2a$12$kyEoNEzbu..YRkjEj2G78OfV/oPjPQ0hmj4dZNwYMwAwSOiC1ysxi', 'Kata', 'Szőlősi', '17-MAR-23', '17-MAR-23', 0, 'Tas vezér u. 7.', 'Szakály', 'Tolna vármegye', '7192', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('SimkoRikard@rhyta.com', '$2a$12$4Sk.cWTBpu/u5Ino5kvr5O2x14RsOq63KDwNUgxyNV8vUV36Zs4im', 'Richárd', 'Simkó', '17-MAR-23', '17-MAR-23', 0, 'Erzsébet tér 31.', 'Mátraderecske', 'Heves vármegye', '3246', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('MesterPatrik@dayrep.com', '$2a$12$Gi3uAxOEBVtmZfWl/hQlTOjzXFen/X.9M6j2u2uEI8tNBqxT3GdRu', 'Patrik', 'Mester', '17-MAR-23', '17-MAR-23', 0, 'Nánási út 84.', 'Vése', 'Somogy vármegye', '8721', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('HofmanEszter@rhyta.com', '$2a$12$W1zf6vLTr9cIqMO6oUy77O4fU7toaC3k33nnb.tDjwd2lAFBbCH/m', 'Eszter', 'Hofman', '17-MAR-23', '17-MAR-23', 0, 'Munkácsy Mihály út 98.', 'Nagykőrös', 'Pest vármegye', '2750', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('MarkusNatalia@armyspy.com', '$2a$12$Bh5mFsNSbMH/VzOvPLwoUuTfg05nmtQkWQHB/k7X7/Tp4sCkOXcpS', 'Natália', 'Márkus', '17-MAR-23', '17-MAR-23', 0, 'Hegyalja út 75.', 'Végardó', 'Borsod-Abaúj-Zemplén vármegye', '3952', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('FazekasMoric@rhyta.com', '$2a$12$3qIWVf4p2sP.P.HXVOJDoOJNc9mCeVYF5F7ctI3za4P8rJDKNwZ/q', 'Móric', 'Fazekas', '17-MAR-23', '17-MAR-23', 0, 'Erzsébet krt. 30.', 'Szigetszentmárton', 'Pest vármegye', '2318', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('RadicsDorottya@teleworm.us', '$2a$12$PLS0QRZE1gWwJ93TjmOq1eBEfzlgMDkJE9BpTqz0ZYF607F0LFmZK', 'Dorottya', 'Radics', '17-MAR-23', '17-MAR-23', 0, 'Csabai kapu 90.', 'Budapest', 'Budapest', '1123', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('SantaVince@armyspy.com', '$2a$12$3oKYWAfSifQhrvKYbhEPI.wO1bfa2Ce4./njkJfWOFmwO9n.5ZEsy', 'Vince', 'Santa', '17-MAR-23', '17-MAR-23', 0, 'Munkácsy Mihály út 49.', 'Ceglédbercel', 'Pest vármegye', '2737', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('NemethKemenes@rhyta.com', '$2a$12$X4uIv9uSKSvT7rgW/W3z/eaXJfC6Ze3It2hz98rWeS3nPrFUl0hCC', 'Kemenes', 'Németh', '17-MAR-23', '17-MAR-23', 0, 'Szent Gellért tér 52.', 'Mályinka', 'Borsod-Abaúj-Zemplén vármegye', '3645', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('BodeArnold@rhyta.com', '$2a$12$jdx63fYlNOMlauXFaIQ6wOrlrCWcHd.mMK9MZIpFyZMqziz106KD2', 'Arnold', 'Bode', '17-MAR-23', '17-MAR-23', 0, 'Apor Péter u. 53.', 'Portelek', 'Jász-Nagykun-Szolnok vármegye', '5152', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('FerencziBenedek@dayrep.com', '$2a$12$IzPBGyvQ6gN8Aj0M/1joBe793jl293rlgSUlG2Gs2/Dmu7RvX/Wkm', 'Benedek', 'Ferenzi', '17-MAR-23', '17-MAR-23', 0, 'Kolodvorska 4', 'Kresnice', 'Central Sava', '1281', 'Slovenija', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('CsorbaAtilla@dayrep.com', '$2a$12$kjWyC.f1GJlLqgebC/YqW.U/BHnGfdX1k1c8nE0DTVBVPAOGOzm/C', 'Attila', 'Csorba', '17-MAR-23', '17-MAR-23', 0, 'Árpád fejedelem útja 46.', 'Budapest', 'Budapest', '1143', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('PolachFlora@dayrep.com', '$2a$12$POAd0Zlm1Cr8Ft/RqLomlu.nVm0nDuUz0KFvst/9On6wjaCCdePdW', 'Flóra', 'Polach', '17-MAR-23', '17-MAR-23', 0, 'Kálmán Imre u. 29.', 'Zalaszabar', 'Zala vármegye', '8743', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('RozsaKund@rhyta.com', '$2a$12$MpAQAdOaVq4ECy3bQuLO..flRa.8Uug.j/Hdk1Z/wcYgbMLhxp2pW', 'Kund', 'Rózsa', '17-MAR-23', '17-MAR-23', 0, 'Erzsébet tér 10.', 'Tarnabod', 'Heves vármegye', '3369', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('SagiGizi@teleworm.us', '$2a$12$Y/YlPNZ4eni6rs3ez3rwGuN/CNDi4z7ox0bTGS.WGm0DsD8qqP21O', 'Gizi', 'Sági', '17-MAR-23', '17-MAR-23', 0, 'Belgrád rkp. 4.', 'Sorokpolány', 'Vas vármegye', '9773', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('RigoZsombor@jourrapide.com', '$2a$12$YTgtIpI/3yIU9nD6xfIDh.uThlHmHo7oCco2.1o1kEXaMT5XvnndC', 'Zsombor', 'Rigó', '17-MAR-23', '17-MAR-23', 0, 'Kálmán Imre u. 15.', 'Újudvar', 'Zala vármegye', '8778', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('CsaszarGyorgy@jourrapide.com', '$2a$12$kSlXnQP6Ah01bgRf5tSKjueu5U1eRIKytMt9080vQ8h4mQcq8cXTG', 'György', 'Császár', '17-MAR-23', '17-MAR-23', 0, 'Tavcarjeva 99', 'Šmarješke Toplice', 'Lower Carniola', '8220', 'Szlovénia', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('PappVivien@jourrapide.com', '$2a$12$omePYV2VXqwCNS.8n84gwOk0zSnkDA2kZfVa20AmMbhpPP4ZB.d4.', 'Vivien', 'Papp', '17-MAR-23', '17-MAR-23', 0, 'Veres Pálné u. 20.', 'Telekgerendás', 'Békés vármegye', '5675', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('KontzTibor@dayrep.com', '$2a$12$OLdip7TAjNr1pMz1jddiJO/Ee96P8lI6CuwaR3OKLsol/.TaSCCK2', 'Tibor', 'Kontz', '17-MAR-23', '17-MAR-23', 0, 'Nyár utca 14.', 'Zalalövő', 'Zala vármegye', '8999', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('DankoHanna@jourrapide.com', '$2a$12$w.5tW5nEc5LGkMAQlr2wHOQ3NLFuiOVu7pSkMiPxx4A/zZhRZssYS', 'Hanna', 'Danko', '17-MAR-23', '17-MAR-23', 0, 'Teréz krt. 81.', 'Pusztavám', 'Fejér vármegye', '8066', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('TolnaiBenedek@armyspy.com', '$2a$12$WaCzMaLC5ApOeCowx0adeeVMSRiPeUXOTCcnqtGBdfD5tUYo6bvfq', 'Benedek', 'Tolnai', '17-MAR-23', '17-MAR-23', 0, 'Dózsa György út 15.', 'Miskolc', 'Borsod-Abaúj-Zemplén vármegye', '3510', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('BaloghGreta@rhyta.com', '$2a$12$AvBMWP6nfUMgWDk9T1930exjXuYDDOfq0L83aHM8Ubo/jmZi8W1kq', 'Gréta', 'Balogh', '17-MAR-23', '17-MAR-23', 0, 'Bem rkp. 70.', 'Gönc', 'Borsod-Abaúj-Zemplén vármegye', '3895', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('PusztaiCsongor@teleworm.us', '$2a$12$Yp2SlXCJw0QAgzve.BBdmO6fqS6WfWEYQud6Hn4.TVOx/IPviYvH6', 'Csongor', 'Pusztai', '17-MAR-23', '17-MAR-23', 0, 'Agip u. 88.', 'Matty', 'Baranya vármegye', '7854', 'Magyarország', null);
INSERT INTO CUSTOMER (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, CREATED_AT, LAST_LOGIN, ADMIN, STREET, CITY, STATE_OR_REGION, POSTCODE, COUNTRY, REGULAR_SINCE) VALUES ('NemesIrenke@armyspy.com', '$2a$12$GhIWJf49sJjopfkq/RIEJ.e3X0JJKN04ITHB8RRJNYsrAVgTyODee', 'Irénke', 'Nemes', '17-MAR-23', '17-MAR-23', 0, 'Erzsébet krt. 67.', 'Gyón', 'Pest vármegye', '2373', 'Magyarország', null);

--NOTIFICATION--
INSERT INTO NOTIFICATION (MESSAGE, CUSTOMER_ID) VALUES ('Kívánságlistán lévő könyvek újra kaphatóak!', 1);

--ORDERS--
INSERT INTO ORDERS (CREATED_AT, SHIPPED, PICKUP, CUSTOMER_ID) VALUES ('11-MAR-23', 0, 0, 1);
INSERT INTO ORDERS (CREATED_AT, SHIPPED, PICKUP, CUSTOMER_ID) VALUES ('11-MAR-23', 1, 1, 3);
INSERT INTO ORDERS (CREATED_AT, SHIPPED, PICKUP, CUSTOMER_ID) VALUES ('11-MAR-23', 0, 1, 2);

INSERT INTO INVOICE (VALUE, PAID, ORDER_ID) VALUES (1234, 0, 1);
INSERT INTO INVOICE (VALUE, PAID, ORDER_ID) VALUES (123, 1, 2);
INSERT INTO INVOICE (VALUE, PAID, ORDER_ID) VALUES (12345, 0, 3);

--CONTAINS--
INSERT INTO CONTAINS VALUES (1, 2, 2);
INSERT INTO CONTAINS VALUES (1, 3, 1);
INSERT INTO CONTAINS VALUES (1, 4, 1);
INSERT INTO CONTAINS VALUES (2, 1, 1);
INSERT INTO CONTAINS VALUES (3, 1, 3);
INSERT INTO CONTAINS VALUES (3, 2, 1);

--INVOICE--
INSERt INTO INVOICE (VALUE, PAID, ORDER_ID) VALUES (11040, 1, 1);
INSERt INTO INVOICE (VALUE, PAID, ORDER_ID) VALUES (3990, 1, 2);
INSERt INTO INVOICE (VALUE, PAID, ORDER_ID) VALUES (7270, 0, 3);

--WHISLIST--
INSERT INTO WISHLIST (NAME, CREATED_AT, CUSTOMER_ID) VALUES ('Szülinaposak', '11-MAR-23', 1);

--PARTOF--
INSERT INTO PARTOF VALUES (1, 1, '11-MAR-23');

COMMIT;