/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50720
Source Host           : localhost:3306
Source Database       : gateway

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2018-05-21 15:55:33
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for zuul_route_vo
-- ----------------------------
DROP TABLE IF EXISTS `zuul_route_vo`;
CREATE TABLE `zuul_route_vo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `path` varchar(64) DEFAULT NULL,
  `service_id` varchar(128) DEFAULT NULL,
  `url` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of zuul_route_vo
-- ----------------------------
INSERT INTO `zuul_route_vo` VALUES ('1', '/user/**', 'mics-user', '');
INSERT INTO `zuul_route_vo` VALUES ('2', '/user11/**', '', 'http://127.0.0.1:8080');
