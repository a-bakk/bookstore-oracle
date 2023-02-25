# Bookstore

## Követelmények

- Könyvek felvétele és kezelése az adatbázisban (kiadás, kiadó, szerző, oldalszám, kötés,
méret, ár)
- Műfajok és alműfajok kezelése, könyvek műfajokba sorolása
- Könyv vásárlása (kiszállítással)
- __NTL__ Műfajok mellé kigyűjteni, hogy hány, az adott műfajba tartozó könyv található az
adatbázisban
- __NTL__ Keresés címre, szerzőre, találatok számának kigyűjtése
- __NTL__ A legújabb könyvek megjelenítése a kezdőoldalon
- Áruházak kezelése (egy országban egy könyvesbolt-hálózatnak több üzlete is van)
- __NTL__ Mely könyvek kaphatók egy áruházban? Egy adott könyv mely áruházban kapható?
- __NTL__ Könyv vásárlása, ahol megadható, hogy kiszállítással vagy üzletben kívánja átvenni a
vásárló a könyvet
- Számla készítése a vásárlásról
- Felhasználók kezelése, csak regisztrált felhasználó tud vásárolni
- __NTL__ Készlet nyilvántartása boltonként
- __T__ Figyelmeztetés készlet kimerüléséről
- Zene, filmek, elektronikus könyvek kezelése az adatbázisban
- „Olcsó könyvek” listázása árkategóriánkként
- Törzsvásárlók nyilvántartása, törzsvásárlói kedvezmények
- __T__ Törzsvásárlóvá válás
- __NTL__ Egy könyv adatlapjánál azon könyveket is kilistázni, amelyeket megvettek azok a vásárlók,
amelyek az aktuális könyvet megvették
- __NTL__ A legnépszerűbb könyvek műfajonként
- __NTL__ Heti/havi toplista a vásárlások alapján (mindegy, hogy interneten vagy boltban vásárolták
meg a könyvet)

## Határidők

- 1. MF: Dokumentáció és adatbázisterv bemutatása -> __2023. március 7.__ (5. hét)
- 2. MF: Adatbázist létrehozó szkriptek beadása -> __2023. március 21.__ (7. hét)
- 3. MF: Adatlekérés bemutatása grafikus felhasználói felületen, min. elvárás: legalább a táblák feléből lehessen adatot lekérni -> __2023. március 28.__ (8. hét)
- 4. MF: Adatfelvitel bemutatása grafikus felhasználói felületen, min. elvárás: legalább a táblák feléhez legyen adatbeszúrás vagy módosítás vagy törlés -> __2023. április 25.__ (12. hét)
- Projektmunka leadásának időpontja: __2023. április 30.__ (vasárnap, 23:55), bemutatás azt ezt követő hetekben

## Dokumentáció

- munka felosztásának részletes leírása
- szöveges, részletes feladatleírás, követelménykatalógus
- AFD (logikai és fizikain legalább 1. és 2. szinten)
- egyedmodell, E-K diagram
- adatmodellezés és relációs adatelemzés (leképezés, normalizálás, adattáblák leírása)
- funkciómeghatározás/egyed-esemény mátrix/szerep-funkció mátrix -> max. ponthoz mind3
- összetett lekérdezések (SQL szintaxis, helye a programkódban (fájl, sor), a lekérdezés által megvalósított funkció)
- fordításhoz/futtatáshoz szükséges eszközök, stack
- lehet még: menüterv, képernyőterv etc.

## Adatbázis

- összefüggő adattáblák száma legalább 6
- min. 120 darab rekord, logikusan elosztva
- releváns rekordok
- összetett lekérdezés (legalább két tábla összekapcsolása/csoportosítást összesítő függvény/alkérdés) min 6 darab
- integritásellenőrzés
- nem számláló funkciót megvalósító triggerek legalább 2
- adatbázisban tárolt eljárások min 2

- jelszavak hash-elt tárolása
- SQL injection védelem
- nézettáblák
- max ponthoz: adattáblák, összetett lekérdezések min 9
- triggerek, tárolt eljárások min 5
- rekordok min 240

## Alkalmazás

- CRUD-hoz GUI
- megfelel a dokumentációnak
- hány százalékát lehet a tábláknak elérni az alkalmazásból
- használhatóság/letisztultság
