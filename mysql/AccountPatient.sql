SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for account_patient
-- ----------------------------
DROP TABLE IF EXISTS `AccountPatient`;
CREATE TABLE `AccountPatient` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` int(11) DEFAULT NULL,
  `patient_id` int(11) DEFAULT NULL,
  `is_self` tinyint(1) NOT NULL DEFAULT FALSE,
  `is_super_validate` tinyint(1) NOT NULL DEFAULT FALSE,
  `is_patient_validate` tinyint(1) NOT NULL DEFAULT FALSE,
  `role_id` int(11) DEFAULT NULL,
  `appellation_id` int(11) DEFAULT NULL,
  `status` smallint(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8;
