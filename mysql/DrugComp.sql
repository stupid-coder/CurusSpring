/*
Navicat MySQL Data Transfer

Source Server         : LocalServer14
Source Server Version : 50540
Source Host           : 10.2.7.14:3306
Source Database       : Drugs

Target Server Type    : MYSQL
Target Server Version : 50540
File Encoding         : 65001

Date: 2016-05-28 14:51:33
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for DrugComp
-- ----------------------------
DROP TABLE IF EXISTS `DrugComp`;
CREATE TABLE `DrugComp` (
  `comp_id` varchar(36) NOT NULL,
  `comp_name` varchar(200) DEFAULT NULL,
  `comp_ename` varchar(200) DEFAULT NULL,
  `comp_aim` varchar(50) DEFAULT NULL,
  `comp_type` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`comp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of DrugComp
-- ----------------------------
INSERT INTO `DrugComp` VALUES ('2fc6fce2-1b27-11e6-85a1-00164137c275', '格列齐特', 'gliclazide', '01', '3');
INSERT INTO `DrugComp` VALUES ('33251163-1b1c-11e6-85a1-00164137c275', '盐酸二甲双胍', 'Metformin hydrochloride', '01', '4');
INSERT INTO `DrugComp` VALUES ('4ba2f07d-1b24-11e6-85a1-00164137c275', '格列本脲', 'glibenclamide', '01', '3');
INSERT INTO `DrugComp` VALUES ('4db963d9-1c28-11e6-85a1-00164137c275', '吡格列酮', 'Pioglitazone', '01', '3');
INSERT INTO `DrugComp` VALUES ('502e5bcf-1b33-11e6-85a1-00164137c275', '格列喹酮', 'gliquidone', '01', '3');
INSERT INTO `DrugComp` VALUES ('a3da05b0-1c05-11e6-85a1-00164137c275', '重组人胰岛素', 'Recombinant human insulin', '01', '1');
INSERT INTO `DrugComp` VALUES ('e3968d7b-1b25-11e6-85a1-00164137c275', '格列吡嗪', 'glipizide', '01', '3');
