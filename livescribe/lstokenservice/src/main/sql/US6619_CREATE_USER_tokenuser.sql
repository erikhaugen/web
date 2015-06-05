
CREATE USER 'tokenuser'@'%' IDENTIFIED BY 't0k3nk33p3r';

GRANT SELECT, INSERT, UPDATE, DELETE ON consumer.* to 'tokenuser'@'%' IDENTIFIED BY 't0k3nk33p3r';
