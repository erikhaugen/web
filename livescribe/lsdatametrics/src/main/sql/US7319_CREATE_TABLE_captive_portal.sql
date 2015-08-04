USE `metrics` ;

-- Drop old Table `metrics`.`portal_capture`
DROP TABLE IF EXISTS `metrics`.`portal_capture` ;

-- -----------------------------------------------------
-- Table `metrics`.`captive_portal`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`captive_portal` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`captive_portal` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL ,
  `metric_version` VARCHAR(32) NULL ,
  `device_id` VARCHAR(64) NULL ,
  `device_type` VARCHAR(64) NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `path` MEDIUMTEXT NULL ,
  `passed` TINYINT(1) NULL ,
  `is_test_data` TINYINT(1) NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_captive_portal_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_captive_portal_metric_group1`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;
