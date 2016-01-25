SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `phone` varchar(20) UNIQUE NOT NULL,
  `is_exp_user` tinyint(1) NOT NULL DEFAULT 1,
  `create_time` datetime DEFAULT NULL,
  `name` varchar(30) NOT NULL DEFAULT '',
  `passwd` varchar(30) NOT NULL,
  `gender` tinyint(1) NOT NULL DEFAULT 1,
  `birth` date DEFAULT NULL,
  `id_number` varchar(18) DEFAULT NULL,
  `address` varchar(40) DEFAULT NULL,
  `other_contact` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for account_patient
-- ----------------------------
DROP TABLE IF EXISTS `account_patient`;
CREATE TABLE `account_patient` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` int(11) DEFAULT NULL,
  `patient_id` int(11) DEFAULT NULL,
  `is_self` tinyint(1) NOT NULL,
  `is_super_validate` tinyint(1) NOT NULL,
  `is_patient_validate` tinyint(1) NOT NULL,
  `role_id` int(11) DEFAULT NULL,
  `appellation_id` int(11) DEFAULT NULL,
  `status` smallint(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for appellation
-- ----------------------------
DROP TABLE IF EXISTS `appellation`;
CREATE TABLE `appellation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `issure_id` int(11) DEFAULT NULL,
  `author` varchar(20) DEFAULT NULL,
  `title` varchar(40) NOT NULL,
  `outline` varchar(200) DEFAULT NULL,
  `content` longtext NOT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for decision
-- ----------------------------
DROP TABLE IF EXISTS `decision`;
CREATE TABLE `decision` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` int(11) DEFAULT NULL,
  `patient_id` int(11) DEFAULT NULL,
  `issure_id` int(11) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `end_date` date DEFAULT NULL,
  `assess` longtext,
  `initial` varchar(300) DEFAULT NULL,
  `target` longtext,
  `status` smallint(6) NOT NULL,
  `policy` longtext NOT NULL,
  `result` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for django_migrations
-- ----------------------------
DROP TABLE IF EXISTS `django_migrations`;
CREATE TABLE `django_migrations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `applied` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for issure
-- ----------------------------
DROP TABLE IF EXISTS `issure`;
CREATE TABLE `issure` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for med_aim
-- ----------------------------
DROP TABLE IF EXISTS `med_aim`;
CREATE TABLE `med_aim` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(15) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for med_component
-- ----------------------------
DROP TABLE IF EXISTS `med_component`;
CREATE TABLE `med_component` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(15) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for med_component_synergy
-- ----------------------------
DROP TABLE IF EXISTS `med_component_synergy`;
CREATE TABLE `med_component_synergy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_medcomponent_id` int(11) NOT NULL,
  `to_medcomponent_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `from_medcomponent_id` (`from_medcomponent_id`,`to_medcomponent_id`),
  KEY `med_component_sy_to_medcomponent_id_3b61852f_fk_med_component_id` (`to_medcomponent_id`),
  CONSTRAINT `med_component_sy_to_medcomponent_id_3b61852f_fk_med_component_id` FOREIGN KEY (`to_medcomponent_id`) REFERENCES `med_component` (`id`),
  CONSTRAINT `med_component_s_from_medcomponent_id_e53e71e_fk_med_component_id` FOREIGN KEY (`from_medcomponent_id`) REFERENCES `med_component` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for med_component_tabu
-- ----------------------------
DROP TABLE IF EXISTS `med_component_tabu`;
CREATE TABLE `med_component_tabu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_medcomponent_id` int(11) NOT NULL,
  `to_medcomponent_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `from_medcomponent_id` (`from_medcomponent_id`,`to_medcomponent_id`),
  KEY `med_component_ta_to_medcomponent_id_73449357_fk_med_component_id` (`to_medcomponent_id`),
  CONSTRAINT `med_component_ta_to_medcomponent_id_73449357_fk_med_component_id` FOREIGN KEY (`to_medcomponent_id`) REFERENCES `med_component` (`id`),
  CONSTRAINT `med_component__from_medcomponent_id_6fce9b76_fk_med_component_id` FOREIGN KEY (`from_medcomponent_id`) REFERENCES `med_component` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for med_formulation
-- ----------------------------
DROP TABLE IF EXISTS `med_formulation`;
CREATE TABLE `med_formulation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(15) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for med_mainclass
-- ----------------------------
DROP TABLE IF EXISTS `med_mainclass`;
CREATE TABLE `med_mainclass` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for med_medicine
-- ----------------------------
DROP TABLE IF EXISTS `med_medicine`;
CREATE TABLE `med_medicine` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `approval_num` varchar(50) DEFAULT NULL,
  `name` varchar(40) NOT NULL,
  `chemical_name` varchar(40) DEFAULT NULL,
  `factory` varchar(40) DEFAULT NULL,
  `specification` varchar(20) DEFAULT NULL,
  `min_dose_pertime` varchar(20) DEFAULT NULL,
  `max_dose_pertime` varchar(20) DEFAULT NULL,
  `common_dose_pertime` varchar(20) DEFAULT NULL,
  `min_dose_perday` varchar(20) DEFAULT NULL,
  `max_dose_perday` varchar(20) DEFAULT NULL,
  `common_dose_perday` varchar(20) DEFAULT NULL,
  `notes` varchar(200) DEFAULT NULL,
  `manual` longtext,
  `formulation_id` int(11) DEFAULT NULL,
  `mainclass_id` int(11) DEFAULT NULL,
  `med_type_id` int(11),
  `subclass_id` int(11),
  `usage_id` int(11),
  PRIMARY KEY (`id`),
  KEY `med_medicine_32539977` (`med_type_id`),
  KEY `med_medicine_23d99215` (`subclass_id`),
  KEY `med_medicine_0528eb2a` (`usage_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for med_medicine_aim
-- ----------------------------
DROP TABLE IF EXISTS `med_medicine_aim`;
CREATE TABLE `med_medicine_aim` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `medmedicine_id` int(11) NOT NULL,
  `medaim_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `medmedicine_id` (`medmedicine_id`,`medaim_id`),
  KEY `med_medicine_aim_medaim_id_6b3fc08e_fk_med_aim_id` (`medaim_id`),
  CONSTRAINT `med_medicine_aim_medaim_id_6b3fc08e_fk_med_aim_id` FOREIGN KEY (`medaim_id`) REFERENCES `med_aim` (`id`),
  CONSTRAINT `med_medicine_aim_medmedicine_id_3e47ae20_fk_med_medicine_id` FOREIGN KEY (`medmedicine_id`) REFERENCES `med_medicine` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for med_medicine_component
-- ----------------------------
DROP TABLE IF EXISTS `med_medicine_component`;
CREATE TABLE `med_medicine_component` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `medmedicine_id` int(11) NOT NULL,
  `medcomponent_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `medmedicine_id` (`medmedicine_id`,`medcomponent_id`),
  KEY `med_medicine_compon_medcomponent_id_4ad6d156_fk_med_component_id` (`medcomponent_id`),
  CONSTRAINT `med_medicine_compon_medcomponent_id_4ad6d156_fk_med_component_id` FOREIGN KEY (`medcomponent_id`) REFERENCES `med_component` (`id`),
  CONSTRAINT `med_medicine_component_medmedicine_id_b1aa548_fk_med_medicine_id` FOREIGN KEY (`medmedicine_id`) REFERENCES `med_medicine` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for med_subclass
-- ----------------------------
DROP TABLE IF EXISTS `med_subclass`;
CREATE TABLE `med_subclass` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for med_type
-- ----------------------------
DROP TABLE IF EXISTS `med_type`;
CREATE TABLE `med_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(15) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for med_usage
-- ----------------------------
DROP TABLE IF EXISTS `med_usage`;
CREATE TABLE `med_usage` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(15) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` datetime NOT NULL,
  `mfrom` varchar(20) NOT NULL,
  `mto_id` int(11) DEFAULT NULL,
  `title` varchar(20) NOT NULL,
  `content` longtext NOT NULL,
  `extra` longtext,
  `read` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for notify
-- ----------------------------
DROP TABLE IF EXISTS `notify`;
CREATE TABLE `notify` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `push_mode` smallint(6) NOT NULL,
  `period` smallint(6) NOT NULL,
  `frequency` smallint(6) DEFAULT NULL,
  `notify_cat_id` int(11) DEFAULT NULL,
  `notify_content` longtext NOT NULL,
  `decision_id` int(11) DEFAULT NULL,
  `can_patient_push` tinyint(1) NOT NULL,
  `can_account_push` tinyint(1) NOT NULL,
  `job_id` varchar(30) DEFAULT NULL,
  `is_send` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for notify_cat
-- ----------------------------
DROP TABLE IF EXISTS `notify_cat`;
CREATE TABLE `notify_cat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for patient
-- ----------------------------
DROP TABLE IF EXISTS `patient`;
CREATE TABLE `patient` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `gender` tinyint(1) NOT NULL,
  `birth` date NOT NULL,
  `id_number` varchar(18) NOT NULL,
  `phone` bigint(20) NOT NULL,
  `address` varchar(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `weixin` varchar(30) DEFAULT NULL,
  `qq` smallint(6) DEFAULT NULL,
  `email` varchar(30) DEFAULT NULL,
  `other_contact` varchar(30) DEFAULT NULL,
  `can_phone_push` tinyint(1) NOT NULL,
  `can_app_push` tinyint(1) NOT NULL,
  `can_weixin_push` tinyint(1) NOT NULL,
  `can_qq_push` tinyint(1) NOT NULL,
  `can_email_push` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for patient_issure
-- ----------------------------
DROP TABLE IF EXISTS `patient_issure`;
CREATE TABLE `patient_issure` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_id` int(11) DEFAULT NULL,
  `issure_id` int(11) DEFAULT NULL,
  `latest_visit_date` date DEFAULT NULL,
  `visit_count` int(11) NOT NULL,
  `latest_interval_decide_date` date DEFAULT NULL,
  `visit_interval` smallint(6) NOT NULL,
  `status` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=365 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for patient_medicine
-- ----------------------------
DROP TABLE IF EXISTS `patient_medicine`;
CREATE TABLE `patient_medicine` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usage` varchar(30) NOT NULL,
  `create_time` datetime NOT NULL,
  `take_time` varchar(100) DEFAULT NULL,
  `dos_pertime` varchar(20) DEFAULT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime DEFAULT NULL,
  `status` tinyint(1) NOT NULL,
  `nouse_reason` varchar(100) DEFAULT NULL,
  `account_id` int(11) DEFAULT NULL,
  `medicine_id` int(11) DEFAULT NULL,
  `patient_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for quota
-- ----------------------------
DROP TABLE IF EXISTS `quota`;
CREATE TABLE `quota` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` int(11) DEFAULT NULL,
  `patient_id` int(11) DEFAULT NULL,
  `submit_time` datetime NOT NULL,
  `measure_time` datetime DEFAULT NULL,
  `quota_cat_id` int(11) DEFAULT NULL,
  `record` longtext NOT NULL,
  `sub_cat` varchar(10),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for quota_cat
-- ----------------------------
DROP TABLE IF EXISTS `quota_cat`;
CREATE TABLE `quota_cat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for smoke_stat
-- ----------------------------
DROP TABLE IF EXISTS `smoke_stat`;
CREATE TABLE `smoke_stat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_id` int(11) DEFAULT NULL,
  `assess_point` smallint(6) NOT NULL,
  `sign_in_times` int(11) NOT NULL,
  `praise_times` int(11) NOT NULL,
  `confess_times` int(11) NOT NULL,
  `has_reduced_num` int(11) NOT NULL,
  `smoke_age` int(11) NOT NULL,
  `num_per_day` int(11) NOT NULL,
  `price_per_packet` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;