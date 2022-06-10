COPY MENU
FROM '/home/csmajs/nrahm009/cs166project/data/menu.csv'
WITH DELIMITER ';';

COPY USERS
FROM '/home/csmajs/nrahm009/cs166project/data/users.csv'
WITH DELIMITER ';';

COPY ORDERS
FROM '/home/csmajs/nrahm009/cs166project/data/orders.csv'
WITH DELIMITER ';';
ALTER SEQUENCE orders_orderid_seq RESTART 87257;

COPY ITEMSTATUS
FROM '/home/csmajs/nrahm009/cs166project/data/itemStatus.csv'
WITH DELIMITER ';';