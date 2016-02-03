SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `Message`;
CREATE TABLE `Message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` datetime NOT NULL,
  `mfrom` varchar(20) NOT NULL,
  `mto_id` int(11) DEFAULT NULL,
  `title` varchar(20) NOT NULL,
  `content` longtext NOT NULL,
  `extra` longtext NOT NULL,
  `read` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
