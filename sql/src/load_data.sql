COPY MENU
FROM '/extra/vmeno003/cs166project/data/menu.csv'
WITH DELIMITER ';';

COPY USERS
FROM '/extra/vmeno003/cs166project/data/users.csv'
WITH DELIMITER ';';

COPY ORDERS
FROM '/extra/vmeno003/cs166project/data/orders.csv'
WITH DELIMITER ';';
ALTER SEQUENCE orders_orderid_seq RESTART 87257;

COPY ITEMSTATUS
FROM '/extra/vmeno003/cs166project/data/itemStatus.csv'
WITH DELIMITER ';';

