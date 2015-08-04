USE `metrics` ;

-- -----------------------------------------------------
-- Table `metrics`.`new_host_spot`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`new_host_spot` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`new_host_spot` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL ,
  `metric_version` VARCHAR(32) NULL ,
  `device_id` VARCHAR(64) NULL ,
  `device_type` VARCHAR(64) NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `url` VARCHAR(255) ,
  `s3_location` VARCHAR(255) ,
  `is_test_data` TINYINT(1) NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_new_host_spot_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_new_host_spot_metric_group1`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `metrics`.`diagnostic`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics`.`diagnostic` ;

CREATE  TABLE IF NOT EXISTS `metrics`.`diagnostic` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `metric_group_id` BIGINT NULL ,
  `message_id` VARCHAR(64) NULL ,
  `metric_version` VARCHAR(32) NULL ,
  `device_id` VARCHAR(64) NULL ,
  `device_type` VARCHAR(64) NULL ,
  `fw_version` VARCHAR(32) NULL ,
  `sub_type` VARCHAR(64) ,
  `s3_location` VARCHAR(255) ,
  `is_test_data` TINYINT(1) NULL ,
  `event_time` TIMESTAMP NULL ,
  `metric_sent` TIMESTAMP NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_diagnostic_metric_group1` (`metric_group_id` ASC) ,
  CONSTRAINT `fk_diagnostic_metric_group1`
    FOREIGN KEY (`metric_group_id` )
    REFERENCES `metrics`.`metric_group` (`metric_group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;