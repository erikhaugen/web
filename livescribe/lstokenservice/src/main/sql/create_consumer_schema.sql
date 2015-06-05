SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `consumer` ;
CREATE SCHEMA IF NOT EXISTS `consumer` DEFAULT CHARACTER SET utf8 ;
USE `consumer` ;

-- -----------------------------------------------------
-- Table `consumer`.`us_state`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `consumer`.`us_state` ;

CREATE  TABLE IF NOT EXISTS `consumer`.`us_state` (
  `us_state_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `state_name` VARCHAR(64) NULL ,
  `is_active` TINYINT(1)  NULL ,
  `state_abbrev` VARCHAR(2) NULL ,
  `collect_sales_tax_start` DATETIME NULL ,
  `collect_sales_tax_end` DATETIME NULL ,
  `created` TIMESTAMP NULL ,
  `last_modified` TIMESTAMP NULL ,
  `last_modified_by` VARCHAR(64) NULL ,
  PRIMARY KEY (`us_state_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `consumer`.`country`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `consumer`.`country` ;

CREATE  TABLE IF NOT EXISTS `consumer`.`country` (
  `country_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `is_active` TINYINT(1)  NULL ,
  `iso_code_2` VARCHAR(2) NULL ,
  `iso_code_3` VARCHAR(3) NULL ,
  `iso_numeric_code` INT NULL ,
  `name` VARCHAR(64) NULL ,
  PRIMARY KEY (`country_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `consumer`.`address`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `consumer`.`address` ;

CREATE  TABLE IF NOT EXISTS `consumer`.`address` (
  `address_id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `country_id` BIGINT(20) NOT NULL ,
  `us_state_id` BIGINT(20) NOT NULL ,
  `address1` VARCHAR(256) NULL DEFAULT NULL ,
  `address2` VARCHAR(256) NULL DEFAULT NULL ,
  `city` VARCHAR(128) NULL DEFAULT NULL ,
  `county` VARCHAR(128) NULL DEFAULT NULL ,
  `post_code` VARCHAR(10) NULL DEFAULT NULL ,
  `province` VARCHAR(128) NULL DEFAULT NULL ,
  `last_validated` DATETIME NULL DEFAULT NULL ,
  `created` TIMESTAMP NULL DEFAULT NULL ,
  `last_modified` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ,
  `last_modified_by` VARCHAR(64) NULL DEFAULT NULL ,
  PRIMARY KEY (`address_id`) ,
  INDEX `fk_address_us_state1` (`us_state_id` ASC) ,
  INDEX `fk_address_country1` (`country_id` ASC) ,
  CONSTRAINT `fk_address_us_state1`
    FOREIGN KEY (`us_state_id` )
    REFERENCES `consumer`.`us_state` (`us_state_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_address_country1`
    FOREIGN KEY (`country_id` )
    REFERENCES `consumer`.`country` (`country_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `consumer`.`user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `consumer`.`user` ;

CREATE  TABLE IF NOT EXISTS `consumer`.`user` (
  `user_id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `address_id` BIGINT(20) NULL ,
  `preferred_country_id` BIGINT(20) NOT NULL ,
  `email` VARCHAR(45) NULL DEFAULT NULL ,
  `first_name` VARCHAR(45) NULL DEFAULT NULL ,
  `last_name` VARCHAR(45) NULL DEFAULT NULL ,
  `grad_year` INT(11) NULL DEFAULT NULL ,
  `birth_year` INT(11) NULL DEFAULT NULL ,
  `major` VARCHAR(128) NULL DEFAULT NULL ,
  `organization` VARCHAR(128) NULL DEFAULT NULL ,
  `occupation` VARCHAR(128) NULL DEFAULT NULL ,
  `screenname` VARCHAR(64) NULL DEFAULT NULL ,
  `send_email` TINYINT(1) NULL DEFAULT NULL ,
  `sex` CHAR NULL DEFAULT NULL ,
  `university` VARCHAR(128) NULL DEFAULT NULL ,
  `phone` VARCHAR(24) NULL DEFAULT NULL ,
  `password` VARCHAR(64) NULL DEFAULT NULL ,
  `passwordResetString` VARCHAR(12) NULL DEFAULT NULL ,
  `customer_id` BIGINT(20) NULL DEFAULT NULL ,
  `customer_password` VARCHAR(2047) NULL DEFAULT NULL ,
  `confirmed` TINYINT(1) NULL DEFAULT NULL ,
  `confirmation_date` TIMESTAMP NULL DEFAULT NULL ,
  `created` TIMESTAMP NULL DEFAULT NULL ,
  `last_modified` TIMESTAMP NULL DEFAULT NULL ,
  `last_modified_by` VARCHAR(64) NULL DEFAULT NULL ,
  PRIMARY KEY (`user_id`) ,
  INDEX `fk_users_address2` (`address_id` ASC) ,
  INDEX `fk_users_country1` (`preferred_country_id` ASC) ,
  CONSTRAINT `fk_users_address2`
    FOREIGN KEY (`address_id` )
    REFERENCES `consumer`.`address` (`address_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_country1`
    FOREIGN KEY (`preferred_country_id` )
    REFERENCES `consumer`.`country` (`country_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `consumer`.`role`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `consumer`.`role` ;

CREATE  TABLE IF NOT EXISTS `consumer`.`role` (
  `role_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `role_name` VARCHAR(45) NULL ,
  PRIMARY KEY (`role_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `consumer`.`x_user_role`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `consumer`.`x_user_role` ;

CREATE  TABLE IF NOT EXISTS `consumer`.`x_user_role` (
  `user_id` BIGINT(20) NOT NULL ,
  `role_id` BIGINT(20) NOT NULL ,
  `created` TIMESTAMP NULL DEFAULT NULL ,
  `created_by` VARCHAR(64) NULL DEFAULT NULL ,
  PRIMARY KEY (`user_id`, `role_id`) ,
  INDEX `fk_users_x_roles_users` (`user_id` ASC) ,
  INDEX `fk_users_x_roles_roles1` (`role_id` ASC) ,
  CONSTRAINT `fk_users_x_roles_users`
    FOREIGN KEY (`user_id` )
    REFERENCES `consumer`.`user` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_x_roles_roles1`
    FOREIGN KEY (`role_id` )
    REFERENCES `consumer`.`role` (`role_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `consumer`.`client`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `consumer`.`client` ;

CREATE  TABLE IF NOT EXISTS `consumer`.`client` (
  `client_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `client_name` VARCHAR(64) NULL ,
  PRIMARY KEY (`client_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `consumer`.`logged_in`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `consumer`.`logged_in` ;

CREATE  TABLE IF NOT EXISTS `consumer`.`logged_in` (
  `logged_in_id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `client_id` BIGINT(20) NOT NULL ,
  `user_id` BIGINT(20) NOT NULL ,
  `token` VARCHAR(45) NULL DEFAULT NULL ,
  `created` TIMESTAMP NULL DEFAULT NULL ,
  `created_by` VARCHAR(64) NULL DEFAULT NULL ,
  PRIMARY KEY (`logged_in_id`) ,
  INDEX `fk_logged_in_users1` (`user_id` ASC) ,
  INDEX `fk_logged_in_client1` (`client_id` ASC) ,
  CONSTRAINT `fk_logged_in_users1`
    FOREIGN KEY (`user_id` )
    REFERENCES `consumer`.`user` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_logged_in_client1`
    FOREIGN KEY (`client_id` )
    REFERENCES `consumer`.`client` (`client_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `consumer`.`group`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `consumer`.`group` ;

CREATE  TABLE IF NOT EXISTS `consumer`.`group` (
  `group_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `group_name` VARCHAR(64) NULL ,
  `group_type` VARCHAR(32) NULL ,
  PRIMARY KEY (`group_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `consumer`.`x_user_group`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `consumer`.`x_user_group` ;

CREATE  TABLE IF NOT EXISTS `consumer`.`x_user_group` (
  `user_id` BIGINT(20) NOT NULL ,
  `group_id` BIGINT(20) NOT NULL ,
  `created` TIMESTAMP NULL DEFAULT NULL ,
  `created_by` VARCHAR(64) NULL DEFAULT NULL ,
  PRIMARY KEY (`user_id`, `group_id`) ,
  INDEX `fk_users_x_groups_users1` (`user_id` ASC) ,
  INDEX `fk_users_x_groups_groups1` (`group_id` ASC) ,
  CONSTRAINT `fk_users_x_groups_users1`
    FOREIGN KEY (`user_id` )
    REFERENCES `consumer`.`user` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_x_groups_groups1`
    FOREIGN KEY (`group_id` )
    REFERENCES `consumer`.`group` (`group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `consumer`.`x_group_role`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `consumer`.`x_group_role` ;

CREATE  TABLE IF NOT EXISTS `consumer`.`x_group_role` (
  `group_id` BIGINT(20) NOT NULL ,
  `role_id` BIGINT(20) NOT NULL ,
  `created` TIMESTAMP NULL DEFAULT NULL ,
  `created_by` VARCHAR(64) NULL DEFAULT NULL ,
  PRIMARY KEY (`group_id`, `role_id`) ,
  INDEX `fk_groups_x_roles_groups1` (`group_id` ASC) ,
  INDEX `fk_groups_x_roles_roles1` (`role_id` ASC) ,
  CONSTRAINT `fk_groups_x_roles_groups1`
    FOREIGN KEY (`group_id` )
    REFERENCES `consumer`.`group` (`group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_groups_x_roles_roles1`
    FOREIGN KEY (`role_id` )
    REFERENCES `consumer`.`role` (`role_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `consumer`.`user_credential`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `consumer`.`user_credential` ;

CREATE  TABLE IF NOT EXISTS `consumer`.`user_credential` (
  `user_credential_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `user_id` BIGINT NULL ,
  `password` VARCHAR(64) NULL ,
  `reg_token` VARCHAR(64) NULL ,
  `aws_access_key_id` VARCHAR(64) NULL ,
  `aws_secret_key` VARCHAR(128) NULL ,
  `aws_token` VARCHAR(128) NULL ,
  `aws_token_expiration` TIMESTAMP NULL ,
  `credential_provider` VARCHAR(45) NULL ,
  `created` TIMESTAMP NULL ,
  `last_modified` TIMESTAMP NULL ,
  `last_modified_by` VARCHAR(64) NULL ,
  PRIMARY KEY (`user_credential_id`) ,
  INDEX `fk_user_credential_users1` (`user_id` ASC) ,
  CONSTRAINT `fk_user_credential_users1`
    FOREIGN KEY (`user_id` )
    REFERENCES `consumer`.`user` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `consumer`.`user_email`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `consumer`.`user_email` ;

CREATE  TABLE IF NOT EXISTS `consumer`.`user_email` (
  `user_email_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `user_id` BIGINT NULL ,
  `email_address` VARCHAR(64) NULL ,
  `created` TIMESTAMP NULL ,
  `last_modified` TIMESTAMP NULL ,
  `last_modified_by` VARCHAR(64) NULL ,
  PRIMARY KEY (`user_email_id`) ,
  INDEX `fk_user_email_users1` (`user_id` ASC) ,
  CONSTRAINT `fk_user_email_users1`
    FOREIGN KEY (`user_id` )
    REFERENCES `consumer`.`user` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `consumer`.`registered_device`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `consumer`.`registered_device` ;

CREATE  TABLE IF NOT EXISTS `consumer`.`registered_device` (
  `registered_device_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `user_id` BIGINT NULL ,
  `device_id` BIGINT NULL ,
  `device_serial_number` VARCHAR(128) NULL ,
  `device_certificate_id` BIGINT NULL ,
  `device_type` VARCHAR(64) NULL ,
  `reg_token` VARCHAR(64) NULL ,
  `created` TIMESTAMP NULL ,
  `last_modified` TIMESTAMP NULL ,
  `last_modified_by` VARCHAR(64) NULL ,
  PRIMARY KEY (`registered_device_id`) ,
  INDEX `fk_user_pen_users1` (`user_id` ASC) ,
  CONSTRAINT `fk_user_pen_users1`
    FOREIGN KEY (`user_id` )
    REFERENCES `consumer`.`user` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `consumer`.`user_certificate`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `consumer`.`user_certificate` ;

CREATE  TABLE IF NOT EXISTS `consumer`.`user_certificate` (
  `user_certificate_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `user_id` BIGINT NULL ,
  `private_key_text` MEDIUMTEXT NULL ,
  `public_key_text` MEDIUMTEXT NULL ,
  `cert_type` TINYINT NULL ,
  `version` TINYINT NULL ,
  `revoked` TINYINT(1)  NULL ,
  `created` TIMESTAMP NULL ,
  `last_modified` TIMESTAMP NULL ,
  `last_modified_by` VARCHAR(64) NULL ,
  PRIMARY KEY (`user_certificate_id`) ,
  INDEX `fk_user_certificate_user1` (`user_id` ASC) ,
  CONSTRAINT `fk_user_certificate_user1`
    FOREIGN KEY (`user_id` )
    REFERENCES `consumer`.`user` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `consumer`.`user_contact`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `consumer`.`user_contact` ;

CREATE  TABLE IF NOT EXISTS `consumer`.`user_contact` (
  `user_contact_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `owner_id` BIGINT NULL ,
  `contact_email_id` BIGINT NULL ,
  `nickname` VARCHAR(64) NULL ,
  `created` TIMESTAMP NULL ,
  `last_modified` TIMESTAMP NULL ,
  `last_modified_by` VARCHAR(64) NULL ,
  PRIMARY KEY (`user_contact_id`) ,
  INDEX `fk_user_contact_user1` (`owner_id` ASC) ,
  INDEX `fk_user_contact_user_email1` (`contact_email_id` ASC) ,
  CONSTRAINT `fk_user_contact_user1`
    FOREIGN KEY (`owner_id` )
    REFERENCES `consumer`.`user` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_contact_user_email1`
    FOREIGN KEY (`contact_email_id` )
    REFERENCES `consumer`.`user_email` (`user_email_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
