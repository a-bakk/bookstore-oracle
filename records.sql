--INSERT INTO VALUES ();

--BOOK--

INSERT INTO BOOK VALUES (1, 'Metró 2033', 'Az egész világ romokban hever. Az emberiség majdnem teljesen elpusztult. Moszkva szellemvárossá változott, megmérgezte a radioaktív sugárzás, és szörnyek népesítik be. A kevés életben maradt ember a moszkvai metróban bújik meg – a Föld legnagyobb atombombabiztos óvóhelyén. A metró állomásai most városállamok, az alagutakban sötétség honol, és borzalom fészkel. Artyomnak az egész metróhálózaton át kell jutnia, hogy megmentse a szörnyű veszedelemtől az állomását, sőt talán az egész emberiséget.', 'füles, kartonált', 540, 'EFO nyomda', 3990, 440, '27-JUN-16', 'Európa Könyvkiadó', 9630791397, 'magyar', null);
INSERT INTO BOOK VALUES (2, 'Vének háborúja', 'Az ember már űrbéli gyarmatokon él, és folytat kegyetlen harcokat újabbakért a Gyarmati Véderő vezetésével. Ez mindenképp szükséges, ugyanis az erőforrások szűkösek, lakható bolygókból pedig kevés van, és emiatt a kivándorlást erősen korlátozzák a Földről. Sokaknak csak az a lehetőség marad, hogy a hetvenöt éves kort elérve jelentkezzenek katonának, majd két év frontszolgálat után letelepedjenek valamelyik kolónián, busásan megjutalmazva. Nem csak emiatt hangzik csábítónak ez a lehetőség: a közvélekedés szerint a Gyarmati Véderő valamilyen módszerrel meg is fiatalítja az embereket, hiszen öregekkel nem lehet harcba indulni. Úgyhogy John Perry két dolgot tett a hetvenötödik születésnapján. Meglátogatta a felesége sírját, majd belépett a hadseregbe. Csak két évet kell túlélnie. Azt viszont még ő sem sejti, hogy milyen megpróbáltatások várnak rá - a brutális háborúk és az otthon fényévekre nyúló távolsága örökké megváltoztatja az embert. Valami sokkal különösebbé és veszélyesebbé.', 'puhatáblás', 294, 'EFO nyomda', 3280, 252, '16-AUG-19', 'Agave Könyvek Kiadó', 9789634196297, 'magyar', null);
INSERT INTO BOOK VALUES (3, 'Szellemhadtest', 'Az emberiséget soha nem látott veszély fenyegeti, űrbéli gyarmatai ellen három intelligens faj szövetkezik. Három faj és egyetlen ember. Az emberiség egyetlen reménye az, hogy az áruló tudós a dezertálása során hátrahagyta az elméjéről készült biztonsági másolatot. De senki nem tudja, hogyan lehetne azt felhasználni. A Gyarmati Véderő Szellemhadteste soha nem az erkölcsi aggályairól volt híres, így megpróbálja egy klónkatonájába ültetni a lemásolt lelket... elvégre a háborút nekik kell megakadályozni, és ezért a célért mindenre hajlandóak.', 'puhatáblás', 350, 'EFO nyomda', 3880, 291, '01-NOV-19', 'Agave Könyvek Kiadó', 9789634196303, 'magyar', null);
INSERT INTO BOOK VALUES (4, 'Az utolsó gyarmat', 'Katonai szolgálata végeztével John Perry leszerelt, és eseménytelen veteránéveit Huckleberry távoli bolygóján tengeti, ahol a Gyarmati Szövetség falusi békebírójaként a telepesek tyúk- és kecskepereiben szolgáltat igazságot. A korábban a Különleges Erőknél szolgáló feleségével, Jane Sagannel él együtt, több hektáros tanyájukon földet művelnek, és büszkeségtől dagadó kebellel figyelik, hogyan cseperedik örökbefogadott lányuk, Zoë. A falusi idill nyolc évét azonban egy csapásra fenekestül felforgatja, amikor a Gyarmati Szövetség fejese kopogtat a portájukon. Az emberek gyarmatbirodalma ugyanis politikai válság szélére sodródott: a maroknyi telepes segítségével létesített egykori kolóniák az évek során annyira megerősödtek, hogy egy ideje maguk is gyarmatosítási jogot követelnek saját bolygóiknak. Ki más lehetne rátermettebb kormányzója a baljós nevű Roanoke-ra készülő, tíz külön világról érkezett, önfejű telepesnek, mint Perry, a békebíró és az ellentmondást nem tűrő Sagan?', 'puhatáblás', 350, 'EFO nyomda', 3880, 294, '03-JAN-23', 'Agave Könyvek Kiadó', 9789634196310, 'magyar', null);





--AUTHOR--

INSERT INTO AUTHOR VALUES (1, 'Glukhovsky', 'Dmitry');
INSERT INTO AUTHOR VALUES (2, 'Scalzi', 'John');
INSERT INTO AUTHOR VALUES (3, 'Scalzi', 'John');
INSERT INTO AUTHOR VALUES (4, 'Scalzi', 'John');




--GENRE--

INSERT INTO GENRE VALUES (1, 'sci-fi');
INSERT INTO GENRE VALUES (2, 'sci-fi');
INSERT INTO GENRE VALUES (3, 'sci-fi');
INSERT INTO GENRE VALUES (4, 'sci-fi');




--STORE--

INSERT INTO STORE VALUES (1, 'BookAround Szeged Pláza Könyvesbolt', 'Kossuth Lajos sgrt. 119', 'Csongrád-Csanád vármegye', '6724', 'Magyarország');
INSERT INTO STORE VALUES (2, 'Bookaround Westend Könyvesbolt', 'Váci út 1-3', 'Budapest', '1062', 'Magyarország');
INSERT INTO STORE VALUES (3, 'Győri BookAround', 'Árpád út 60', 'Győr-Moson-Sopron vármegye', '9022', 'Magyarország');




--STOCK--

INSERT INTO STOCK VALUES (1, 1, 10);
INSERT INTO STOCK VALUES (2, 1, 12);
INSERT INTO STOCK VALUES (3, 1, 12);
INSERT INTO STOCK VALUES (4, 1, 12);
INSERT INTO STOCK VALUES (1, 2, 17);
INSERT INTO STOCK VALUES (2, 2, 20);
INSERT INTO STOCK VALUES (3, 2, 20);
INSERT INTO STOCK VALUES (2, 3, 13);
INSERT INTO STOCK VALUES (3, 3, 13);
INSERT INTO STOCK VALUES (4, 3, 15);




--BUSINESS_HOURS--

INSERT INTO BUSINESS_HOURS VALUES (1, 1, '2023-01-01 08:00:00', '2023-01-01 20:00:00', 1);
INSERT INTO BUSINESS_HOURS VALUES (2, 2, '2023-01-01 08:00:00', '2023-01-01 18:00:00', 1);
INSERT INTO BUSINESS_HOURS VALUES (3, 3, '2023-01-01 08:00:00', '2023-01-01 20:00:00', 1);
INSERT INTO BUSINESS_HOURS VALUES (4, 4, '2023-01-01 08:00:00', '2023-01-01 18:00:00', 1);
INSERT INTO BUSINESS_HOURS VALUES (5, 5, '2023-01-01 08:00:00', '2023-01-01 18:00:00', 1);
INSERT INTO BUSINESS_HOURS VALUES (6, 1, '2023-01-01 10:00:00', '2023-01-01 22:00:00', 2);
INSERT INTO BUSINESS_HOURS VALUES (7, 2, '2023-01-01 10:00:00', '2023-01-01 20:00:00', 2);
INSERT INTO BUSINESS_HOURS VALUES (8, 3, '2023-01-01 10:00:00', '2023-01-01 20:00:00', 2);
INSERT INTO BUSINESS_HOURS VALUES (9, 4, '2023-01-01 10:00:00', '2023-01-01 20:00:00', 2);
INSERT INTO BUSINESS_HOURS VALUES (10, 5, '2023-01-01 10:00:00', '2023-01-01 20:00:00', 2);
INSERT INTO BUSINESS_HOURS VALUES (11, 6, '2023-01-01 12:00:00', '2023-01-01 20:00:00', 2);
INSERT INTO BUSINESS_HOURS VALUES (12, 7, '2023-01-01 12:00:00', '2023-01-01 16:00:00', 2);
INSERT INTO BUSINESS_HOURS VALUES (13, 1, '2023-01-01 8:00:00', '2023-01-01 18:00:00', 3);
INSERT INTO BUSINESS_HOURS VALUES (14, 2, '2023-01-01 8:00:00', '2023-01-01 18:00:00', 3);
INSERT INTO BUSINESS_HOURS VALUES (15, 4, '2023-01-01 8:00:00', '2023-01-01 18:00:00', 3);
INSERT INTO BUSINESS_HOURS VALUES (16, 5, '2023-01-01 08:00:00', '2023-01-01 18:00:00', 3);
INSERT INTO BUSINESS_HOURS VALUES (17, 6, '2023-01-01 10:00:00', '2023-01-01 18:00:00', 3);
INSERT INTO BUSINESS_HOURS VALUES (18, 7, '2023-01-01 12:00:00', '2023-01-01 16:00:00', 3);




--CUSTOMER--

INSERT INTO CUSTORMER VALUES (1, 'test.user1@email.com', 'Béla', 'Kis', '11-MAR-23', null, 0, null, null, null, null, null);
INSERT INTO CUSTORMER VALUES (2, 'test.user2@email.com', 'István', 'Kovács', '11-MAR-23', null, 0, null, null, null, null, '11-MAR-23');
INSERT INTO CUSTORMER VALUES (3, 'test.admin1@email.com', 'Zoltán', 'Kalapács', '11-MAR-23', null, 1, null, null, null, null, null);




--NOTIFICATION--

INSERT INTO NOTIFICATION VALUES (1, 'Kívánságlistán lévő könyvek újra kaphatóak!', 1);




--ORDERS--

INSERT INTO ORDERS VALUES (1, '11-MAR-23', null, 0, 1);
INSERT INTO ORDERS VALUES (2, '11-MAR-23', 1, 1, 3);
INSERT INTO ORDERS VALUES (3, '11-MAR-23', 0, 1, 2);




--CONTAINS--

INSERT INTO CONTAINS VALUES (1, 2);
INSERT INTO CONTAINS VALUES (1, 3);
INSERT INTO CONTAINS VALUES (1, 4);
INSERT INTO CONTAINS VALUES (2, 1);
INSERT INTO CONTAINS VALUES (3, 1);
INSERT INTO CONTAINS VALUES (3, 2);




--INVOICE--

INSERt INTO INVOICE VALUES (1, 11040, 'kézpénz', 1, 1);
INSERt INTO INVOICE VALUES (2, 3990, 'kártya', 1, 2);
INSERt INTO INVOICE VALUES (3, 7270, 'kártya', 0, 3);




--WHISLIST--
INSERT INTO WHISLIST VALUES (1, 'Szülinaposak', '11-MAR-23', 1);




--PARTOF--
INSERT INTO PARTOF VALUES (1, 1, '11-MAR-23');








