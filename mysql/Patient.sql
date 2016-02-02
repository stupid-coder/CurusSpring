SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for patient
-- ----------------------------
DROP TABLE IF EXISTS `Patient`;
CREATE TABLE `Patient` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL DEFAULT "",
  `gender` tinyint(1) NOT NULL DEFAULT 1,
  `birth` date NOT NULL,
  `id_number` varchar(18) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `address` varchar(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `weixin` VARCHAR(30) DEFAULT NULL,
  `qq` VARCHAR(10) DEFAULT NULL,
  `email` varchar(30) DEFAULT NULL,
  `other_contact` varchar(30) DEFAULT NULL,
  `can_phone_push` tinyint(1) NOT NULL DEFAULT FALSE ,
  `can_app_push` tinyint(1) NOT NULL DEFAULT FALSE ,
  `can_weixin_push` tinyint(1) NOT NULL DEFAULT FALSE ,
  `can_qq_push` tinyint(1) NOT NULL DEFAULT FALSE ,
  `can_email_push` tinyint(1) NOT NULL DEFAULT FALSE ,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;
