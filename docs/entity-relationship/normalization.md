# Mapping, normalization

Customer (<u>customerId</u>, email, firstName, lastName, createdAt, lastLogin, admin, street, stateOrRegion, postcode, country, regularSince)

Wishlist (<u>wishlistId</u>, name, createdAt, _customerId_)

Order (<u>orderId</u>, createdAt, shipped, pickup, _customerId_)

Invoice (<u>invocieId</u>, value, paymentMode, paid, _orderId_)

Book (<u>bookId</u>, description, cover, weight, contractor, price, numberOfPages, publishedAt, publisher, ISBN, language)

Author (<u>_bookId_, firstName, lastName</u>)

Genre (<u>_bookId_, genreName</u>)

Store (<u>storeId</u>, name, street, stateOrRegion, postcode, country)

Stock (<u>_bookId_</u>, <u>_storeId_</u>, count)

PartOf (<u>_bookId_</u>, <u>_wishlistId_</u>, addedAt)

1NF: Az összes séma megfelel az első normálforma előírásainak, hiszen leképezés után minden
attribútum atomi.

2NF: A következő sémákban minden kulcs egy attribútumból áll, ezért lesznek 2NF-ben: Customer, Wishlist, Order, Invoice, Book, Store.

A következő sémákban nincs másodlagos attribútum, ezért lesznek 2NF-ben: Author, Genre.

A maradék két sémában (Stock és PartOf) a kulcson kívüli egyetlen attribútum (count és addedAt) teljesen függ a kulcstól.
(külön a bookId-től nem függ a count, a storeId-tól szintén nem, és hasonlóan, a bookId-tól nem függ az addedAt és a wishlistId-tól sem külön)

3NF: Az Author és Genre témákban nincs másodlagos attribútum, ezért 3NF-ben vannak.

A Customer, Wishlist, Order, Invoice, Book, Store, Stock és PartOf sémák esetén kijelenthető, hogy nincs
bennük tranzitív függés, ezért lesznek 3NF-ben.

Megjegyzés: Változtatna a dolgon, ha az országok egyértelműen meghatároznák az irányítószámokat. Viszont ez nem teljesül, https://www.quora.com/Do-the-same-ZIP-codes-exist-in-different-countries . Ugyanilyen alapon jelenthető ki ez a városokról is.
