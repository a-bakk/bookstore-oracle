# Presentation checklist

## Autentikáció előtt

- [ ] Megfelelő oldalak jelennek meg, nem lehet "garázdálkodni"
- [ ] Próbáljunk ki egy elérhetetlen endpointot, pl. localhost:8080/profile -> redirection loginra

## Regisztráljunk egy felhasználót

- [ ] A regisztráció helyesen működik?

## Jelentkezzünk be

- [ ] A bejelentkezés sikeres, átirányítás történik a főoldalra?
- [ ] A menüben megjelennek az új elemek?
- [ ] Az adminisztrátori funkciókat (pl. localhost:8080/admin-panel) el lehet érni?
- [ ] Tekintsük meg a következő oldalakat: Főoldal, Sikerlista, Áruházak, Kívánságlisták, Kosár, Profil
- [ ] Módosítsuk a profiladatainkat! Sikeres a művelet?
- [ ] Válasszunk két könyvet, adjuk a kosárhoz lehetőleg különböző számban.
- [ ] Menjünk a kosárhoz, adjunk le rendelést. A rendelés sikeres? Legenerálódott a számla?
- [ ] Jelentkezzünk ki! Megfelelően működik?

## Váltsunk adminisztrátori jogokkal rendelkező felhasználóra

- [ ] Elérhetőek az adminisztrátori funkciók? Tekintsünk meg egy könyvet. Új funkciók: törlés, módosítás.
- [ ] Navigáljunk az áruházakra. Hozzunk létre újat.
- [ ] Módosítsuk az áruházat, adjunk hozzá pl. nyitvatartást.
- [ ] Adjunk hozzá könyvet az áruházhoz. Pl. Sömmi
- [ ] Töröljük az áruházat.
- [ ] Navigáljunk az értesítésekhez. Hoppá: a listában első értesítést nem manuálisan szúrtuk be az adatbázisba, hanem trigger generálta.
- [ ] Tekintsük meg az adminisztrátori panel statisztikai adatait, rengeteg összetett lekérdezés eredménye itt jelenik meg.

## Idő függvényében

- [ ] Hozzunk létre új könyvet, módosítsuk, töröljük, bármilyen más funkciót mutassunk be.
