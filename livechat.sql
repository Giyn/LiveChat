/*
 Navicat Premium Data Transfer

 Source Server         : MySQL
 Source Server Type    : MySQL
 Source Server Version : 80019
 Source Host           : localhost:3306
 Source Schema         : livechat

 Target Server Type    : MySQL
 Target Server Version : 80019
 File Encoding         : 65001

 Date: 17/12/2020 17:25:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for friendlist
-- ----------------------------
DROP TABLE IF EXISTS `friendlist`;
CREATE TABLE `friendlist`  (
  `srcid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `dstid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of friendlist
-- ----------------------------
INSERT INTO `friendlist` VALUES ('01', '03');
INSERT INTO `friendlist` VALUES ('03', '01');
INSERT INTO `friendlist` VALUES ('01', '03');
INSERT INTO `friendlist` VALUES ('03', '01');

-- ----------------------------
-- Table structure for userinfo
-- ----------------------------
DROP TABLE IF EXISTS `userinfo`;
CREATE TABLE `userinfo`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `nickName` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of userinfo
-- ----------------------------
INSERT INTO `userinfo` VALUES ('01', '38ad35c5826202a217179c18516f80b99147a5325fb3e52e', 'Giyn');
INSERT INTO `userinfo` VALUES ('02', 'a9452f07822887b472e2df3091d84970a332a53d67512a04', 'Helen');
INSERT INTO `userinfo` VALUES ('03', '51d19422ba3a854807b5b29de19981f80f49409185c5ba9d', 'Bob');
INSERT INTO `userinfo` VALUES ('04', '534c33d1df2ae94244b76b5b09791452862647ba63f2b517', 'John');

SET FOREIGN_KEY_CHECKS = 1;
