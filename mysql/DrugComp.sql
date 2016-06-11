/*
Navicat MySQL Data Transfer

Source Server         : LocalServer14
Source Server Version : 50540
Source Host           : 10.2.7.14:3306
Source Database       : Drugs

Target Server Type    : MYSQL
Target Server Version : 50540
File Encoding         : 65001

Date: 2016-06-11 16:53:29
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
  `comp_process` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`comp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of DrugComp
-- ----------------------------
INSERT INTO `DrugComp` VALUES ('12a7910d-2fad-11e6-85a1-00164137c275', '胰岛素 ', 'insulin', '01', '1', '2');
INSERT INTO `DrugComp` VALUES ('2fc6fce2-1b27-11e6-85a1-00164137c275', '格列齐特', 'gliclazide', '01', '7', '2');
INSERT INTO `DrugComp` VALUES ('33251163-1b1c-11e6-85a1-00164137c275', '盐酸二甲双胍', 'Metformin hydrochloride', '01', '4', '2');
INSERT INTO `DrugComp` VALUES ('47bf0c2e-2e2a-11e6-85a1-00164137c275', 'df', 'ddd', '01', '7', '2');
INSERT INTO `DrugComp` VALUES ('4ba2f07d-1b24-11e6-85a1-00164137c275', '格列本脲', 'glibenclamide', '01', '7', '1');
INSERT INTO `DrugComp` VALUES ('4db963d9-1c28-11e6-85a1-00164137c275', '吡格列酮', 'Pioglitazone', '01', '7', '1');
INSERT INTO `DrugComp` VALUES ('502e5bcf-1b33-11e6-85a1-00164137c275', '格列喹酮', 'gliquidone', '01', '8', '1');
INSERT INTO `DrugComp` VALUES ('9775e19b-2fad-11e6-85a1-00164137c275', '精蛋白锌胰岛素', 'Protamine Zinc Insulin', '01', '1', '4');
INSERT INTO `DrugComp` VALUES ('a3da05b0-1c05-11e6-85a1-00164137c275', '重组人胰岛素', 'Recombinant human insulin', '01', '1', '4');
INSERT INTO `DrugComp` VALUES ('ccbca744-2fab-11e6-85a1-00164137c275', '门冬胰岛素', 'Insulin aspart', '01', '1', '2');
INSERT INTO `DrugComp` VALUES ('e3968d7b-1b25-11e6-85a1-00164137c275', '格列吡嗪', 'glipizide', '01', '3', '3');
