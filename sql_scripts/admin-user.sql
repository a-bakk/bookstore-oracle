DECLARE
    number_of_users NUMBER;
BEGIN
    SELECT COUNT(*) INTO number_of_users FROM ALL_USERS WHERE username = 'BOOKAROUND';
    IF number_of_users = 0 THEN
        EXECUTE IMMEDIATE 'CREATE USER bookaround IDENTIFIED BY bookaroundadmin';
        EXECUTE IMMEDIATE 'GRANT CONNECT, RESOURCE, DBA TO bookaround';
        DBMS_OUTPUT.PUT_LINE('"bookaround" felhasználó létrehozva!');
    ELSE DBMS_OUTPUT.PUT_LINE('már létezik a felhasználó!');
    END IF;
END;
/