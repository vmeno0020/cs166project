CREATE INDEX index1
ON orders
(timeStampRecieved);

CREATE INDEX index2
ON ItemStatus
( orderid );
