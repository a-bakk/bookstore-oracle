SET SERVEROUTPUT ON;

DECLARE
    table_count NUMBER;
BEGIN

    SELECT COUNT(*) INTO table_count FROM ALL_TABLES WHERE table_name = 'CUSTOMER';
    IF table_count = 0 THEN
        EXECUTE IMMEDIATE '
        CREATE TABLE customer (
            customer_id         NUMBER(19)          PRIMARY KEY,
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
        )
        ';
        DBMS_OUTPUT.PUT_LINE('"CUSTOMER" tábla létrehozva!');
    ELSE DBMS_OUTPUT.PUT_LINE('már létezik a "CUSTOMER" tábla!');
    END IF;

    SELECT COUNT(*) INTO table_count FROM ALL_TABLES WHERE table_name = 'WISHLIST';
    IF table_count = 0 THEN
        EXECUTE IMMEDIATE '
        CREATE TABLE wishlist (
            wishlist_id         NUMBER(19)          PRIMARY KEY,
            name                VARCHAR2(256)       NOT NULL,
            created_at          DATE                NOT NULL,
            customer_id         NUMBER(19)          REFERENCES customer(customer_id)
        )
        ';
        DBMS_OUTPUT.PUT_LINE('"WISHLIST" tábla létrehozva!');
    ELSE DBMS_OUTPUT.PUT_LINE('már létezik a "WISHLIST" tábla!');
    END IF;

    SELECT COUNT(*) INTO table_count FROM ALL_TABLES WHERE table_name = 'ORDERS';
    IF table_count = 0 THEN
        EXECUTE IMMEDIATE '
        CREATE TABLE orders (
            order_id            NUMBER(19)          PRIMARY KEY,
            created_at          DATE                NOT NULL,
            shipped             NUMBER(1),
            pickup              NUMBER(1),
            customer_id         NUMBER(19)          REFERENCES customer(customer_id)
        )
        ';
        DBMS_OUTPUT.PUT_LINE('"ORDERS" tábla létrehozva!');
    ELSE DBMS_OUTPUT.PUT_LINE('már létezik az "ORDERS" tábla!');
    END IF;

    SELECT COUNT(*) INTO table_count FROM ALL_TABLES WHERE table_name = 'INVOICE';
    IF table_count = 0 THEN
        EXECUTE IMMEDIATE '
        CREATE TABLE invoice (
            invoice_id          NUMBER(19)          PRIMARY KEY,
            value               NUMBER(12),
            payment_mode        VARCHAR2(30),
            paid                NUMBER(1),
            order_id            NUMBER(19)          REFERENCES orders(order_id)
        )
        ';
        DBMS_OUTPUT.PUT_LINE('"INVOICE" tábla létrehozva!');
    ELSE DBMS_OUTPUT.PUT_LINE('már létezik az "INVOICE" tábla!');
    END IF;

    SELECT COUNT(*) INTO table_count FROM ALL_TABLES WHERE table_name = 'BOOK';
    IF table_count = 0 THEN
        EXECUTE IMMEDIATE '
        CREATE TABLE book (
            book_id             NUMBER(19)          PRIMARY KEY,
            name                VARCHAR2(128)       NOT NULL,
            description         VARCHAR2(2048),
            cover               VARCHAR2(30),
            weight              FLOAT,
            price               NUMBER(10),
            number_of_pages     NUMBER(5),
            published_at        DATE,
            publisher           VARCHAR2(50),
            isbn                VARCHAR2(13)        UNIQUE NOT NULL,
            language            VARCHAR2(16),
            discounted_price    NUMBER(10)
        )
        ';
        DBMS_OUTPUT.PUT_LINE('"BOOK" tábla létrehozva!');
    ELSE DBMS_OUTPUT.PUT_LINE('már létezik a "BOOK" tábla!');
    END IF;

    SELECT COUNT(*) INTO table_count FROM ALL_TABLES WHERE table_name = 'AUTHOR';
    IF table_count = 0 THEN
        EXECUTE IMMEDIATE '
        CREATE TABLE author (
            book_id             NUMBER(19)          REFERENCES book(book_id),
            first_name          VARCHAR2(30),
            last_name           VARCHAR2(30),
            PRIMARY KEY (book_id, first_name, last_name)
        )
        ';
        DBMS_OUTPUT.PUT_LINE('"AUTHOR" tábla létrehozva!');
    ELSE DBMS_OUTPUT.PUT_LINE('már létezik az "AUTHOR" tábla!');
    END IF;

    SELECT COUNT(*) INTO table_count FROM ALL_TABLES WHERE table_name = 'GENRE';
    IF table_count = 0 THEN
        EXECUTE IMMEDIATE '
        CREATE TABLE genre (
            book_id             NUMBER(19)          REFERENCES book(book_id),
            genre_name          VARCHAR2(20),
            PRIMARY KEY (book_id, genre_name)
        )
        ';
        DBMS_OUTPUT.PUT_LINE('"GENRE" tábla létrehozva!');
    ELSE DBMS_OUTPUT.PUT_LINE('már létezik a "GENRE" tábla!');
    END IF;

    SELECT COUNT(*) INTO table_count FROM ALL_TABLES WHERE table_name = 'STORE';
    IF table_count = 0 THEN
        EXECUTE IMMEDIATE '
        CREATE TABLE store (
            store_id            NUMBER(19)          PRIMARY KEY,
            name                VARCHAR2(50)        NOT NULL,
            street              VARCHAR2(50),
            city                VARCHAR2(50),
            state_or_region     VARCHAR2(50),
            postcode            VARCHAR2(10),
            country             VARCHAR2(56)
        )
        ';
        DBMS_OUTPUT.PUT_LINE('"STORE" tábla létrehozva!');
    ELSE DBMS_OUTPUT.PUT_LINE('már létezik a "STORE" tábla!');
    END IF;

    SELECT COUNT(*) INTO table_count FROM ALL_TABLES WHERE table_name = 'STOCK';
    IF table_count = 0 THEN
        EXECUTE IMMEDIATE '
        CREATE TABLE stock (
            book_id             NUMBER(19)          REFERENCES book(book_id),
            store_id            NUMBER(19)          REFERENCES store(store_id),
            count               NUMBER(6),
            PRIMARY KEY (book_id, store_id)
        )
        ';
        DBMS_OUTPUT.PUT_LINE('"STOCK" tábla létrehozva!');
    ELSE DBMS_OUTPUT.PUT_LINE('már létezik a "STOCK" tábla!');
    END IF;

    SELECT COUNT(*) INTO table_count FROM ALL_TABLES WHERE table_name = 'PARTOF';
    IF table_count = 0 THEN
        EXECUTE IMMEDIATE '
        CREATE TABLE partof (
            book_id             NUMBER(19)          REFERENCES book(book_id),
            wishlist_id         NUMBER(19)          REFERENCES wishlist(wishlist_id),
            added_at             DATE,
            PRIMARY KEY (book_id, wishlist_id)
        )
        ';
        DBMS_OUTPUT.PUT_LINE('"PARTOF" tábla létrehozva!');
    ELSE DBMS_OUTPUT.PUT_LINE('már létezik a "PARTOF" tábla!');
    END IF;

    SELECT COUNT(*) INTO table_count FROM ALL_TABLES WHERE table_name = 'CONTAINS';
    IF table_count = 0 THEN
        EXECUTE IMMEDIATE '
        CREATE TABLE contains (
            order_id            NUMBER(19)          REFERENCES orders(order_id),
            book_id             NUMBER(19)          REFERENCES book(book_id),
            PRIMARY KEY (order_id, book_id)
        )
        ';
        DBMS_OUTPUT.PUT_LINE('"CONTAINS" tábla létrehozva!');
    ELSE DBMS_OUTPUT.PUT_LINE('már létezik a "CONTAINS" tábla!');
    END IF;

    SELECT COUNT(*) INTO table_count FROM ALL_TABLES WHERE table_name = 'BUSINESS_HOURS';
    IF table_count = 0 THEN
        EXECUTE IMMEDIATE '
        CREATE TABLE business_hours (
            hours_id            NUMBER(19)          PRIMARY KEY,
            day_of_week         NUMBER(1, 0),
            opening_time        VARCHAR2(30),
            closing_time        VARCHAR2(30),
            store_id            NUMBER(19)          REFERENCES store(store_id)
        )
        ';
        DBMS_OUTPUT.PUT_LINE('"BUSINESS_HOURS" tábla létrehozva!');
    ELSE DBMS_OUTPUT.PUT_LINE('már létezik a "BUSINESS_HOURS" tábla!');
    END IF;

    SELECT COUNT(*) INTO table_count FROM ALL_TABLES WHERE table_name = 'NOTIFICATION';
    IF table_count = 0 THEN
        EXECUTE IMMEDIATE '
        CREATE TABLE notification (
            notification_id     NUMBER(19)          PRIMARY KEY,
            message             VARCHAR2(256),
            customer_id         NUMBER(19)          REFERENCES customer(customer_id)
        )
        ';
        DBMS_OUTPUT.PUT_LINE('"NOTIFICATION" tábla létrehozva!');
    ELSE DBMS_OUTPUT.PUT_LINE('már létezik a "NOTIFICATION" tábla!');
    END IF;

END;
/