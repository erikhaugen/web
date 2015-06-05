SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `manufacturing` ;
CREATE SCHEMA IF NOT EXISTS `manufacturing` DEFAULT CHARACTER SET utf8 ;
USE `manufacturing` ;

-- -----------------------------------------------------
-- Table `manufacturing`.`pen_request`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `manufacturing`.`pen_request` ;

CREATE  TABLE IF NOT EXISTS `manufacturing`.`pen_request` (
  `pen_request_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `vendor_id` VARCHAR(32) NOT NULL ,
  `user_id` BIGINT NULL ,
  `pen_type` VARCHAR(255) NOT NULL ,
  `invoice_number` INT NOT NULL ,
  `requested` INT NOT NULL ,
  `start_seq` INT NOT NULL ,
  `created` TIMESTAMP NOT NULL ,
  `last_modified` TIMESTAMP NULL ,
  `last_modified_by` VARCHAR(64) NULL ,
  PRIMARY KEY (`pen_request_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `manufacturing`.`pen`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `manufacturing`.`pen` ;

CREATE  TABLE IF NOT EXISTS `manufacturing`.`pen` (
  `pen_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `display_id` VARCHAR(128) NOT NULL ,
  `serialnumber_hex` VARCHAR(128) NOT NULL ,
  `serialnumber` VARCHAR(128) NOT NULL ,
  `firmware_version` VARCHAR(32) NULL ,
  `pen_certificate_id` BIGINT NULL ,
  `pen_request_id` BIGINT NULL ,
  `pen_type` VARCHAR(255) NOT NULL ,
  `created` TIMESTAMP NULL ,
  `last_modified` TIMESTAMP NULL ,
  `last_modified_by` VARCHAR(64) NULL ,
  PRIMARY KEY (`pen_id`) ,
  INDEX `fk_pen_pen_request1` (`pen_request_id` ASC) ,
  CONSTRAINT `fk_pen_pen_request1`
    FOREIGN KEY (`pen_request_id` )
    REFERENCES `manufacturing`.`pen_request` (`pen_request_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `manufacturing`.`pen_certificate`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `manufacturing`.`pen_certificate` ;

CREATE  TABLE IF NOT EXISTS `manufacturing`.`pen_certificate` (
  `pen_certificate_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `pen_id` BIGINT NULL ,
  `certificate_path` VARCHAR(2048) NULL ,
  `issuer_dn` VARCHAR(128) NULL COMMENT '			' ,
  `subject_dn` VARCHAR(128) NULL ,
  `created` TIMESTAMP NULL ,
  `last_modified` TIMESTAMP NULL ,
  `last_modified_by` VARCHAR(64) NULL ,
  PRIMARY KEY (`pen_certificate_id`) ,
  INDEX `fk_pen_certificate_pen1` (`pen_id` ASC) ,
  CONSTRAINT `fk_pen_certificate_pen1`
    FOREIGN KEY (`pen_id` )
    REFERENCES `manufacturing`.`pen` (`pen_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
