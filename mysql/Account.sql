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
