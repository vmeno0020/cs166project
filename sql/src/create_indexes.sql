CREATE INDEX index1
ON orders
(timeStampRecieved);

CREATE INDEX index2
ON ItemStatus
( orderid );

CREATE INDEX user_login_ind ON Users USING BTREE (login);
CREATE INDEX user_phonenum_ind ON Users USING BTREE (phoneNum); 
CREATE INDEX user_type_ind ON Users USING BTREE (type); 

CREATE INDEX menu_itemname_ind ON Menu USING BTREE (itemName); 
CREATE INDEX menu_type_ind ON Menu USING BTREE (type); 
CREATE INDEX menu_price_ind ON Menu USING BTREE (price); 
CREATE INDEX menu_desc_ind ON Menu USING BTREE (description); 

CREATE INDEX orders_orderid_ind ON Orders USING BTREE (orderid); 
CREATE INDEX orders_login_ind ON Orders USING BTREE (login); 
CREATE INDEX orders_paid_ind ON Orders USING BTREE (paid); 
CREATE INDEX orders_timerecieved_ind ON Orders USING BTREE (timestampRecieved);
CREATE INDEX orders_total_ind ON Orders USING BTREE (total); 

CREATE INDEX itemstatus_orderid_ind ON ItemStatus USING BTREE (orderid); 
CREATE INDEX itemstatus_itemname_ind ON ItemStatus USING BTREE (itemName); 
CREATE INDEX itemstatus_lastupdated_ind ON ItemStatus USING BTREE (lastUpdated); 
CREATE INDEX itemstatus_comments_ind ON ItemStatus USING BTREE (comments); 
