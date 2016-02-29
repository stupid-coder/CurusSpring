-- ----------------------------
-- Table structure for PatientSuperviseList
-- ----------------------------
DROP TABLE IF EXISTS `PatientSuperviseList`;
CREATE TABLE `PatientSuperviseList` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` int(11) DEFAULT NULL,
  `patient_id` int(11) DEFAULT NULL,
  `quota_cat_id` int(11) DEFAULT NULL,
  `list` VARCHAR(300) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;
