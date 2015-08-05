CREATE TABLE `registration` (
  `registration_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `device_id` varchar(64) DEFAULT NULL,
  `app_id` varchar(64) NOT NULL DEFAULT '',
  `edition` int(10) unsigned DEFAULT NULL,
  `pen_serial` varchar(14) NOT NULL DEFAULT '',
  `display_id` varchar(14) NOT NULL DEFAULT '',
  `pen_name` varchar(64) DEFAULT NULL,
  `first_name` varchar(64) DEFAULT NULL,
  `last_name` varchar(64) DEFAULT NULL,
  `email` varchar(64) NOT NULL DEFAULT '',
  `locale` varchar(45) DEFAULT NULL,
  `country` varchar(64) NOT NULL DEFAULT '',
  `loc_lat` double DEFAULT NULL,
  `loc_long` double DEFAULT NULL,
  `opt_in` bit(1) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_modified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_modified_by` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`registration_id`),
  UNIQUE KEY `key_unique_registration` (`app_id`,`pen_serial`,`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `warranty` (
  `registration_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `device_id` varchar(64) DEFAULT NULL,
  `app_id` varchar(64) NOT NULL DEFAULT '',
  `edition` int(10) unsigned DEFAULT NULL,
  `pen_serial` varchar(14) NOT NULL DEFAULT '',
  `display_id` varchar(14) NOT NULL DEFAULT '',
  `pen_name` varchar(64) DEFAULT NULL,
  `first_name` varchar(64) DEFAULT NULL,
  `last_name` varchar(64) DEFAULT NULL,
  `email` varchar(64) NOT NULL DEFAULT '',
  `locale` varchar(45) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_modified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_modified_by` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`registration_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `registration_history` (
  `registration_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `device_id` varchar(64) DEFAULT NULL,
  `app_id` varchar(64) NOT NULL DEFAULT '',
  `edition` int(10) unsigned DEFAULT NULL,
  `pen_serial` varchar(14) NOT NULL DEFAULT '',
  `display_id` varchar(14) NOT NULL DEFAULT '',
  `pen_name` varchar(64) DEFAULT NULL,
  `first_name` varchar(64) DEFAULT NULL,
  `last_name` varchar(64) DEFAULT NULL,
  `email` varchar(64) NOT NULL DEFAULT '',
  `locale` varchar(45) DEFAULT NULL,
  `country` varchar(64) NOT NULL DEFAULT '',
  `loc_lat` double DEFAULT NULL,
  `loc_long` double DEFAULT NULL,
  `opt_in` bit(1) DEFAULT NULL,
  `registration_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `created` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_modified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_modified_by` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`registration_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
