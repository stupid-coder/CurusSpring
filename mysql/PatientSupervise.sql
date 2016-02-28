-- ----------------------------
-- Table structure for PatientSupervise
-- ----------------------------
DROP TABLE IF EXISTS `PatientSupervise`;
CREATE TABLE `PatientSupervise` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` int(11) DEFAULT NULL,
  `patient_id` int(11) DEFAULT NULL,
  `quota_cate_id` int(11) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `end_date` date DEFAULT NULL,
  `initial` varchar(300) DEFAULT NULL,
  `target` varchar(300) DEFAULT NULL,
  `current` varchar(300) DEFAULT NULL,
  `last` TINYINT(1) DEFAULT TRUE,
  `policy` VARCHAR(300) NOT NULL,
  `result` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;
