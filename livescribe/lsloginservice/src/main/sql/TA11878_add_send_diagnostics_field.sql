-- -----------------------------------------------------
-- TA11878 Add 'send_diagnostics' column to 'user' table.
-- -----------------------------------------------------
ALTER TABLE `consumer`.`user` ADD COLUMN `send_diagnostics` BIT(1) NULL DEFAULT b'0';