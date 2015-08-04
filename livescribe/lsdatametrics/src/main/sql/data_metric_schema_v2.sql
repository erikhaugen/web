SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `metrics` DEFAULT CHARACTER SET utf8 ;
USE `metrics` ;

-- -----------------------------------------------------
-- Table `metrics`.`metric_group`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`metric_group` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`metric_group` (
  `metric_group_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `message_id` VARCHAR(64) NULL ,
  `metric_sent` TIMESTAMP NOT NULL DEFAULT '1971-01-01 00:00:01' ,
  `created` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`metric_group_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `metrics`.`pen_on`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`pen_on` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`pen_on` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL DEFAULT NULL ,
  `metric_version` VARCHAR(32) NULL DEFAULT NULL ,
  `device_id` VARCHAR(64) NULL DEFAULT NULL ,
  `device_type` VARCHAR(64) NULL DEFAULT NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `device_action` VARCHAR(64) NULL DEFAULT NULL ,
  `is_test_data` TINYINT(1) NULL DEFAULT NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_pen_on_metric_group` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_pen_on_metric_group`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `metrics`.`pen_off`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`pen_off` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`pen_off` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL DEFAULT NULL ,
  `metric_version` VARCHAR(32) NULL DEFAULT NULL ,
  `device_id` VARCHAR(64) NULL DEFAULT NULL ,
  `device_type` VARCHAR(64) NULL DEFAULT NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `reason` VARCHAR(64) NULL DEFAULT NULL ,
  `duration` BIGINT NULL ,
  `is_test_data` TINYINT(1) NULL DEFAULT NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_pen_off_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_pen_off_metric_group1`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `metrics`.`launch_app`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`launch_app` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`launch_app` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL DEFAULT NULL ,
  `metric_version` VARCHAR(32) NULL DEFAULT NULL ,
  `device_id` VARCHAR(64) NULL DEFAULT NULL ,
  `device_type` VARCHAR(64) NULL DEFAULT NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `app_name` VARCHAR(64) NULL ,
  `reason` VARCHAR(64) NULL ,
  `is_test_data` TINYINT(1) NULL DEFAULT NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_pen_off_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_pen_off_metric_group10`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `metrics`.`button_tap`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`button_tap` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`button_tap` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL DEFAULT NULL ,
  `metric_version` VARCHAR(32) NULL DEFAULT NULL ,
  `device_id` VARCHAR(64) NULL DEFAULT NULL ,
  `device_type` VARCHAR(64) NULL DEFAULT NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `class` VARCHAR(64) NULL ,
  `button_type` VARCHAR(64) NULL ,
  `value` VARCHAR(64) NULL ,
  `is_test_data` TINYINT(1) NULL DEFAULT NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_pen_off_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_pen_off_metric_group11`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `metrics`.`firmware_update`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`firmware_update` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`firmware_update` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL DEFAULT NULL ,
  `metric_version` VARCHAR(32) NULL DEFAULT NULL ,
  `device_id` VARCHAR(64) NULL DEFAULT NULL ,
  `device_type` VARCHAR(64) NULL DEFAULT NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `update_version` VARCHAR(64) NULL DEFAULT NULL ,
  `svn_revision` VARCHAR(64) NULL DEFAULT NULL ,
  `build_time` TIMESTAMP NULL ,
  `is_test_data` TINYINT(1) NULL DEFAULT NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_pen_off_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_pen_off_metric_group12`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `metrics`.`crash`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`crash` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`crash` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL ,
  `metric_version` VARCHAR(32) NULL ,
  `device_id` VARCHAR(64) NULL ,
  `device_type` VARCHAR(64) NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `line` INT NULL ,
  `dump_path` VARCHAR(255) NULL ,
  `is_test_data` TINYINT(1) NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_crash_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_crash_metric_group1`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `metrics`.`launch_line`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`launch_line` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`launch_line` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL ,
  `metric_version` VARCHAR(32) NULL ,
  `device_id` VARCHAR(64) NULL ,
  `device_type` VARCHAR(64) NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `action` VARCHAR(64) NULL ,
  `class` VARCHAR(64) NULL ,
  `is_test_data` TINYINT(1) NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_launch_line_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_launch_line_metric_group1`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `metrics`.`page_switch`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`page_switch` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`page_switch` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL ,
  `metric_version` VARCHAR(32) NULL ,
  `device_id` VARCHAR(64) NULL ,
  `device_type` VARCHAR(64) NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `page_address` VARCHAR(64) NULL ,
  `page` INT NULL ,
  `copy` INT NULL ,
  `is_test_data` TINYINT(1) NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_page_switch_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_page_switch_metric_group1`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `metrics`.`paper_replay`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`paper_replay` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`paper_replay` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL ,
  `metric_version` VARCHAR(32) NULL ,
  `device_id` VARCHAR(64) NULL ,
  `device_type` VARCHAR(64) NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `start` TIMESTAMP NULL ,
  `duration` BIGINT NULL ,
  `is_test_data` TINYINT(1) NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_paper_replay_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_paper_replay_metric_group1`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `metrics`.`pen_undocked`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`pen_undocked` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`pen_undocked` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL ,
  `metric_version` VARCHAR(32) NULL ,
  `device_id` VARCHAR(64) NULL ,
  `device_type` VARCHAR(64) NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `duration` BIGINT NULL , 
  `is_test_data` TINYINT(1) NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_pen_undocked_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_pen_undocked_metric_group1`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `metrics`.`usb_command`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`usb_command` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`usb_command` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL ,
  `metric_version` VARCHAR(32) NULL ,
  `device_id` VARCHAR(64) NULL ,
  `device_type` VARCHAR(64) NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `command` VARCHAR(64) NULL ,
  `is_test_data` TINYINT(1) NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_usb_command_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_usb_command_metric_group1`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `metrics`.`switch_lang`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`switch_lang` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`switch_lang` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL COMMENT '   ' ,
  `metric_version` VARCHAR(32) NULL ,
  `device_id` VARCHAR(64) NULL ,
  `device_type` VARCHAR(64) NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `locale` VARCHAR(32) NULL ,
  `is_test_data` TINYINT(1) NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_switch_lang_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_switch_lang_metric_group1`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `metrics`.`dynnavplus_created`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`dynnavplus_created` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`dynnavplus_created` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL ,
  `metric_version` VARCHAR(32) NULL ,
  `device_id` VARCHAR(64) NULL ,
  `device_type` VARCHAR(64) NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `is_test_data` TINYINT(1) NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_dynnavplus_created_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_dynnavplus_created_metric_group1`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `metrics`.`quickcmd`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`quickcmd` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`quickcmd` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL ,
  `metric_version` VARCHAR(32) NULL ,
  `device_id` VARCHAR(64) NULL ,
  `device_type` VARCHAR(64) NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `action` VARCHAR(64) NULL ,
  `cmd` VARCHAR(64) NULL ,
  `is_test_data` TINYINT(1) NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_quickcmd_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_quickcmd_metric_group1`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `metrics`.`wifi_on`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`wifi_on` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`wifi_on` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL ,
  `metric_version` VARCHAR(32) NULL ,
  `device_id` VARCHAR(64) NULL ,
  `device_type` VARCHAR(64) NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `is_test_data` TINYINT(1) NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_wifi_on_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_wifi_on_metric_group1`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `metrics`.`wifi_off`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`wifi_off` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`wifi_off` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL ,
  `metric_version` VARCHAR(32) NULL ,
  `device_id` VARCHAR(64) NULL ,
  `device_type` VARCHAR(64) NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `is_test_data` TINYINT(1) NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_wifi_off_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_wifi_off_metric_group1`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `metrics`.`shortcut`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`shortcut` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`shortcut` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL ,
  `metric_version` VARCHAR(32) NULL ,
  `device_id` VARCHAR(64) NULL ,
  `device_type` VARCHAR(64) NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `action` VARCHAR(64) ,
  `shortcut_id` INT NULL ,
  `cmd` VARCHAR(64) ,
  `is_test_data` TINYINT(1) NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_shortcut_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_shortcut_metric_group1`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `metrics`.`quickrecord`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`quickrecord` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`quickrecord` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL ,
  `metric_version` VARCHAR(32) NULL ,
  `device_id` VARCHAR(64) NULL ,
  `device_type` VARCHAR(64) NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `action` VARCHAR(64) ,
  `is_test_data` TINYINT(1) NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_quickrecord_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_quickrecord_metric_group1`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
