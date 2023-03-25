# Adatbázis alapú rendszerek: Könyvesbolt

## Adatbázis kezelő szkriptek

Készítők: Bakk Ábel, Ocztos Károly Levente

### PL/SQL alapú adatbázis kezelő szkript

Elérési útvonala: /sql_scripts/db_pl_sql.sql

User input alapján működik. Minimális hibakezelés is társul mellé:
a felhasználók, táblák, sequencek csak akkor jönnek létre, ha még nem léteznek.
Ha rossz input kerül átadásra a szkriptnek, nem hajt végre semmilyen műveletet,
egyébként pedig minden művelet után commitolja a változtatásokat.

Input opciók:
- user: az adatbázishoz tartozó felhasználó létrehozása, melyhez az 
applikáció-specifikus táblák tartoznak (system vagy hasonló felhasználóval kiadva,
a többi már a bookaround felhasználóval szükséges (bookaround, bookaroundadmin))
- tables: a táblák és sequencek létrehozása
- records: a rekordok beszúrása a létrehozott táblákba
- clean: a táblák és sequencek megfelelő sorrendben való droppolása

### Kizárólag sql alapú létrehozó szkript

Hibakezelés nem létezik, a sequenceket, táblákat létrehozza, majd beszúrja
a megfelelő rekordokat.
