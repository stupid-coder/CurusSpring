SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `InternalData`;
CREATE TABLE `InternalData` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_id` int(11) UNIQUE NOT NULL,
  `cate` int(4) NOT NULL,
  `data` varchar(200) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX internal_data_index ON InternalData(patient_id,cate);