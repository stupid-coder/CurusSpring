SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for DrugComp
-- ----------------------------
DROP TABLE IF EXISTS `PatientUseDrug`;
CREATE TABLE `PatientUseDrug` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_id` INT(11) NOT NULL,
  `drug_id` VARCHAR(36) NOT NULL,
  `use_policy` VARCHAR(200) NOT NULL,
  `change_time` DATETIME NOT NULL,
  `last` TINYINT(1) NOT NULL DEFAULT TRUE,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
