SET FOREIGN_KEY_CHECKS=0;
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