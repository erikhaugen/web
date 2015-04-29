-- -----------------------------------------------------
-- TA11378 Add 'uid' column to 'user' table.
-- -----------------------------------------------------
ALTER TABLE `consumer`.`user` ADD COLUMN uid varchar(128) NOT NULL;
