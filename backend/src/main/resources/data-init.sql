-- 种子数据
INSERT IGNORE INTO `user` (username, password, role) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'ADMIN'),
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'USER'),
('merchant1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'MERCHANT');

-- 商家
INSERT IGNORE INTO `merchant` (id, name, address, phone, description, status) VALUES
(1, '麦当劳', '朝阳区建国路1号', '13800000001', '全球知名快餐连锁', 'APPROVED'),
(2, '星巴克', '朝阳区建国路2号', '13800000002', '全球知名咖啡连锁', 'APPROVED'),
(3, '兰州拉面', '海淀区中关村大街3号', '13800000003', '正宗兰州拉面', 'APPROVED');

-- 分类
INSERT IGNORE INTO `category` (id, merchant_id, name, sort) VALUES
(1, 1, '汉堡', 1),
(2, 1, '小食', 2),
(3, 2, '咖啡', 1),
(4, 2, '甜点', 2),
(5, 3, '拉面', 1),
(6, 3, '凉菜', 2);

-- 商品
INSERT IGNORE INTO `product` (id, merchant_id, category_id, name, price, status, description) VALUES
(1, 1, 1, '巨无霸', 22.00, 'ON_SHELF', '经典双层牛肉'),
(2, 1, 1, '麦辣鸡腿堡', 19.00, 'ON_SHELF', '香辣酥脆'),
(3, 1, 2, '薯条', 10.00, 'ON_SHELF', '金黄酥脆'),
(4, 1, 2, '可乐', 5.00, 'ON_SHELF', '冰镇'),
(5, 2, 3, '拿铁', 28.00, 'ON_SHELF', '经典拿铁'),
(6, 2, 3, '美式', 22.00, 'ON_SHELF', '纯黑咖啡'),
(7, 2, 4, '蛋糕', 18.00, 'ON_SHELF', '鲜奶油蛋糕'),
(8, 3, 5, '牛肉拉面', 20.00, 'ON_SHELF', '大碗牛肉面'),
(9, 3, 5, '羊肉拉面', 22.00, 'ON_SHELF', '精选羊肉'),
(10, 3, 6, '拍黄瓜', 6.00, 'ON_SHELF', '清爽小菜');
