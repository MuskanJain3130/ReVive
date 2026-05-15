-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: May 15, 2026 at 07:36 AM
-- Server version: 9.1.0
-- PHP Version: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `revive_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `addresses`
--

DROP TABLE IF EXISTS `addresses`;
CREATE TABLE IF NOT EXISTS `addresses` (
  `addressid` int NOT NULL AUTO_INCREMENT,
  `userid` int DEFAULT NULL,
  `fullname` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phone` varchar(15) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `street` varchar(150) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `city` varchar(80) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `state` varchar(80) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `zipcode` varchar(15) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `country` varchar(80) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `address_type` enum('Home','Work','Other') COLLATE utf8mb4_general_ci DEFAULT NULL,
  `is_default` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`addressid`),
  KEY `userid` (`userid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `addresses`
--

INSERT INTO `addresses` (`addressid`, `userid`, `fullname`, `phone`, `street`, `city`, `state`, `zipcode`, `country`, `address_type`, `is_default`) VALUES
(1, 1, 'Buyer One', '9999999999', 'Street 1', 'Mumbai', 'Maharashtra', '400001', 'India', 'Home', 1),
(2, 1, 'Buyer One Office', '8888888888', 'Street 2', 'Mumbai', 'Maharashtra', '400002', 'India', 'Work', 0),
(3, 6, 'tamanna shah', '6355133411', 'surat', 'surat', NULL, '395007', NULL, NULL, 0);

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
CREATE TABLE IF NOT EXISTS `orders` (
  `orderid` int NOT NULL AUTO_INCREMENT,
  `buyerid` int DEFAULT NULL,
  `orderdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('Placed','Packed','Shipped','Delivered','Cancelled','Returned') COLLATE utf8mb4_general_ci DEFAULT NULL,
  `addressid` int DEFAULT NULL,
  PRIMARY KEY (`orderid`),
  KEY `buyerid` (`buyerid`),
  KEY `addressid` (`addressid`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`orderid`, `buyerid`, `orderdate`, `status`, `addressid`) VALUES
(1, 1, '2026-02-26 14:56:15', 'Placed', 1),
(2, 2, '2026-03-30 12:30:00', 'Placed', NULL),
(3, 6, '2026-05-07 16:54:39', 'Placed', 3),
(4, 6, '2026-05-07 16:59:37', 'Placed', 3),
(5, 6, '2026-05-07 17:12:46', 'Placed', 3);

-- --------------------------------------------------------

--
-- Table structure for table `order_details`
--

DROP TABLE IF EXISTS `order_details`;
CREATE TABLE IF NOT EXISTS `order_details` (
  `orderdetailid` int NOT NULL AUTO_INCREMENT,
  `orderid` int DEFAULT NULL,
  `productid` int DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `price` double(10,2) DEFAULT NULL,
  PRIMARY KEY (`orderdetailid`),
  KEY `orderid` (`orderid`),
  KEY `productid` (`productid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `order_details`
--

INSERT INTO `order_details` (`orderdetailid`, `orderid`, `productid`, `quantity`, `price`) VALUES
(1, 1, 3, 1, 2000.00),
(2, 1, 1, 1, 25000.00),
(3, 5, 16, 1, 150.00);

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
CREATE TABLE IF NOT EXISTS `payments` (
  `paymentid` int NOT NULL AUTO_INCREMENT,
  `orderid` int DEFAULT NULL,
  `amount` double(10,2) DEFAULT NULL,
  `payment_mode` enum('UPI','Card','NetBanking','COD') COLLATE utf8mb4_general_ci DEFAULT NULL,
  `payment_status` enum('Pending','Completed','Failed') COLLATE utf8mb4_general_ci DEFAULT NULL,
  `payment_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`paymentid`),
  KEY `orderid` (`orderid`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `payments`
--

INSERT INTO `payments` (`paymentid`, `orderid`, `amount`, `payment_mode`, `payment_status`, `payment_date`) VALUES
(1, 1, 27000.00, 'UPI', 'Completed', '2026-02-26 14:57:39'),
(2, 3, 30.00, 'COD', 'Pending', '2026-05-07 16:54:39'),
(3, 4, 760.00, 'COD', 'Pending', '2026-05-07 16:59:37'),
(4, 5, 150.00, 'COD', 'Pending', '2026-05-07 17:12:46');

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
CREATE TABLE IF NOT EXISTS `products` (
  `productid` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `image_url` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `price` double(10,2) DEFAULT NULL,
  `product_condition` enum('New','Like New','Good','Fair','Poor') COLLATE utf8mb4_general_ci DEFAULT NULL,
  `quantity` int DEFAULT '1',
  `status` enum('Active','Inactive') COLLATE utf8mb4_general_ci DEFAULT 'Active',
  `sellerid` int DEFAULT NULL,
  `approval_status` enum('Pending','Approved','Rejected') COLLATE utf8mb4_general_ci DEFAULT 'Pending',
  `approved_by` int DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `approved_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`productid`),
  KEY `sellerid` (`sellerid`),
  KEY `approved_by` (`approved_by`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`productid`, `title`, `image_url`, `description`, `price`, `product_condition`, `quantity`, `status`, `sellerid`, `approval_status`, `approved_by`, `created_at`, `approved_at`) VALUES
(1, 'iPhone 11', '', 'Good condition used phone', 25000.00, 'Good', 5, 'Active', 1, 'Approved', NULL, '2026-02-26 14:39:22', NULL),
(2, 'Office Chair', '', 'Comfortable ergonomic chair', 3500.00, 'Like New', 2, 'Active', 1, 'Rejected', NULL, '2026-02-26 14:39:22', NULL),
(3, 'Gaming Keyboard', '', 'RGB mechanical keyboard', 1800.00, 'Good', 10, 'Active', 1, 'Approved', NULL, '2026-02-26 14:39:22', NULL),
(4, 'Test Laptop', '', 'i5 8GB RAM', 45000.00, NULL, 5, 'Active', 3, 'Approved', NULL, '2026-03-07 13:33:03', NULL),
(5, 'Test Laptop', '', 'i5 8GB RAM', 45000.00, NULL, 5, 'Active', 3, 'Approved', NULL, '2026-03-07 13:33:06', NULL),
(6, 'Test Laptop', '', 'i5 8GB RAM', 45000.00, NULL, 5, 'Active', 3, 'Rejected', NULL, '2026-03-14 08:28:39', NULL),
(7, 'Test Laptop', '', 'i5 8GB RAM', 45000.00, NULL, 5, 'Active', 3, 'Rejected', NULL, '2026-03-14 08:30:42', NULL),
(8, 'Test Laptop', '', 'i5 8GB RAM', 45000.00, NULL, 5, 'Active', 3, 'Rejected', NULL, '2026-03-14 08:31:18', NULL),
(9, 'Sample Product', '', 'A test product', 59.99, 'New', 5, 'Active', 3, 'Approved', NULL, '2026-03-29 09:43:47', NULL),
(10, 'shampoo', '2f733d3d-b501-420d-b4e7-fc05c56034e5.avif', 'abc', 700.00, 'Like New', NULL, NULL, 6, 'Approved', NULL, '2026-05-02 18:26:28', NULL),
(11, 'shampoo2', '12312492-3416-415d-a2a0-bfac7cd779a9.jpeg', 'shampoos', 400.00, 'New', 8, NULL, 6, 'Approved', NULL, '2026-05-02 18:33:27', NULL),
(12, 'fruit', 'b97921bd-f657-47cc-9037-1facb2d77626.jpeg;26eb339b-1951-48a0-b1ef-cd9b0816d96c.jpg;96e1b54b-47fe-4e8d-9fe8-5f76a01bb1b2.png', 'abc', 50.00, 'New', 5, NULL, 6, 'Rejected', NULL, '2026-05-02 18:59:32', NULL),
(13, 'fruit', '75702bfd-195c-4ab0-a30e-c227bc93fc8d.jpeg;beee112e-193a-4c80-bc13-16985271fab2.jpg', 'abc', 60.00, 'New', 5, NULL, 6, 'Approved', NULL, '2026-05-02 19:02:11', NULL),
(14, 'orange', 'ad1253c8-fd9b-43f5-b429-2347940fd90f.jpg', 'Fresh oranges straight from the orchard', 30.00, 'New', 15, NULL, 6, 'Approved', NULL, '2026-05-07 16:48:35', NULL),
(15, 'soap', 'placeholder.png', 'soap!', 200.00, 'Good', 3, NULL, 6, 'Approved', NULL, '2026-05-07 17:00:36', NULL),
(16, 'shower gel', '47a2c472-86d5-49b9-a410-33d834af9e3a.jpg', 'shower gel\n', 150.00, 'New', 4, NULL, 6, 'Approved', NULL, '2026-05-07 17:01:40', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `refunds`
--

DROP TABLE IF EXISTS `refunds`;
CREATE TABLE IF NOT EXISTS `refunds` (
  `refundid` int NOT NULL AUTO_INCREMENT,
  `returnid` int DEFAULT NULL,
  `amount` double(10,2) DEFAULT NULL,
  `status` enum('Pending','Processed','Failed') COLLATE utf8mb4_general_ci DEFAULT NULL,
  `processed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`refundid`),
  KEY `returnid` (`returnid`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `refunds`
--

INSERT INTO `refunds` (`refundid`, `returnid`, `amount`, `status`, `processed_at`) VALUES
(3, 2, 2000.00, 'Processed', '2026-02-26 15:05:53'),
(4, 3, 25000.00, 'Pending', '2026-02-26 15:02:31');

-- --------------------------------------------------------

--
-- Table structure for table `return_requests`
--

DROP TABLE IF EXISTS `return_requests`;
CREATE TABLE IF NOT EXISTS `return_requests` (
  `returnid` int NOT NULL AUTO_INCREMENT,
  `orderdetailid` int DEFAULT NULL,
  `reason` text COLLATE utf8mb4_general_ci,
  `status` enum('Requested','Approved','Rejected','Completed') COLLATE utf8mb4_general_ci DEFAULT NULL,
  `requested_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`returnid`),
  KEY `orderdetailid` (`orderdetailid`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `return_requests`
--

INSERT INTO `return_requests` (`returnid`, `orderdetailid`, `reason`, `status`, `requested_at`) VALUES
(2, 1, 'Item defective', 'Approved', '2026-02-26 14:57:55'),
(3, 2, 'Wrong item', 'Requested', '2026-02-26 14:57:55'),
(4, 2, 'Product was damaged on arrival', 'Requested', '2026-03-29 11:47:38');

-- --------------------------------------------------------

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
CREATE TABLE IF NOT EXISTS `reviews` (
  `reviewid` int NOT NULL AUTO_INCREMENT,
  `orderdetailid` int DEFAULT NULL,
  `reviewerid` int DEFAULT NULL,
  `rating` int DEFAULT NULL,
  `comment` text COLLATE utf8mb4_general_ci,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`reviewid`),
  KEY `orderdetailid` (`orderdetailid`),
  KEY `reviewerid` (`reviewerid`)
) ;

--
-- Dumping data for table `reviews`
--

INSERT INTO `reviews` (`reviewid`, `orderdetailid`, `reviewerid`, `rating`, `comment`, `created_at`) VALUES
(1, 1, 2, 5, 'Excellent product', '2026-02-26 15:03:14'),
(2, 2, 2, 3, 'Average condition', '2026-02-26 15:03:14'),
(3, 1, 3, 5, 'Excellent product, fast delivery!', '2026-03-29 11:38:12');

-- --------------------------------------------------------

--
-- Table structure for table `role_master`
--

DROP TABLE IF EXISTS `role_master`;
CREATE TABLE IF NOT EXISTS `role_master` (
  `roleid` int NOT NULL,
  `rolename` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`roleid`),
  UNIQUE KEY `rolename` (`rolename`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `role_master`
--

INSERT INTO `role_master` (`roleid`, `rolename`) VALUES
(2, 'admin'),
(1, 'user');

-- --------------------------------------------------------

--
-- Table structure for table `shopping_cart`
--

DROP TABLE IF EXISTS `shopping_cart`;
CREATE TABLE IF NOT EXISTS `shopping_cart` (
  `cartid` int NOT NULL AUTO_INCREMENT,
  `userid` int DEFAULT NULL,
  `productid` int DEFAULT NULL,
  `quantity` int DEFAULT '1',
  `added_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`cartid`),
  UNIQUE KEY `userid` (`userid`,`productid`),
  KEY `productid` (`productid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `shopping_cart`
--

INSERT INTO `shopping_cart` (`cartid`, `userid`, `productid`, `quantity`, `added_at`) VALUES
(1, 1, 1, 1, '2026-03-29 10:15:32');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `userid` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `roleid` int DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `phone` varchar(15) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`userid`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `roleid` (`roleid`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`userid`, `username`, `email`, `password`, `roleid`, `created_at`, `phone`) VALUES
(1, 'admin1', 'admin@mail.com', '123', 2, '2026-02-26 14:25:08', NULL),
(2, 'buyer1', 'buyer@mail.com', '123', 1, '2026-02-26 14:25:08', NULL),
(3, 'seller1', 'seller@mail.com', '123', 1, '2026-02-26 14:25:08', NULL),
(5, 'tamanna', 'tam@gmail.com', 'PBKDF2WithHmacSHA256:2048:8SJJ5tzgp1OYqxAwQx+m0upkCPWM8eb3Nn97/cLsk14=:4cupkOp2Cob5a4prb/4S6+bZIGzp9NFHRWL5YSfOAkI=', 1, '2026-03-30 11:32:50', NULL),
(6, 'abc', 'abc@gmail.com', 'PBKDF2WithHmacSHA256:2048:LNnLSjtPYsglauBJ5oW0Bjz6aZXGa3RhTM5J9XwGWm4=:7elJ+YxFnP0Q76j5UvBvkn9jvFctu5DZwnE85Pazp0g=', 2, '2026-03-31 18:29:36', '6355133411');

-- --------------------------------------------------------

--
-- Table structure for table `wishlist`
--

DROP TABLE IF EXISTS `wishlist`;
CREATE TABLE IF NOT EXISTS `wishlist` (
  `wishlistid` int NOT NULL AUTO_INCREMENT,
  `userid` int DEFAULT NULL,
  `productid` int DEFAULT NULL,
  `added_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`wishlistid`),
  UNIQUE KEY `userid` (`userid`,`productid`),
  KEY `productid` (`productid`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `wishlist`
--

INSERT INTO `wishlist` (`wishlistid`, `userid`, `productid`, `added_at`) VALUES
(1, 1, 1, '2026-02-26 15:02:59'),
(2, 1, 3, '2026-02-26 15:02:59'),
(3, 1, 2, '2026-03-14 08:28:40'),
(8, 2, 1, '2026-03-29 11:29:18');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `addresses`
--
ALTER TABLE `addresses`
  ADD CONSTRAINT `addresses_ibfk_1` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`);

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`buyerid`) REFERENCES `users` (`userid`),
  ADD CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`addressid`) REFERENCES `addresses` (`addressid`);

--
-- Constraints for table `order_details`
--
ALTER TABLE `order_details`
  ADD CONSTRAINT `order_details_ibfk_1` FOREIGN KEY (`orderid`) REFERENCES `orders` (`orderid`),
  ADD CONSTRAINT `order_details_ibfk_2` FOREIGN KEY (`productid`) REFERENCES `products` (`productid`);

--
-- Constraints for table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`orderid`) REFERENCES `orders` (`orderid`);

--
-- Constraints for table `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `products_ibfk_1` FOREIGN KEY (`sellerid`) REFERENCES `users` (`userid`),
  ADD CONSTRAINT `products_ibfk_2` FOREIGN KEY (`approved_by`) REFERENCES `users` (`userid`);

--
-- Constraints for table `refunds`
--
ALTER TABLE `refunds`
  ADD CONSTRAINT `refunds_ibfk_1` FOREIGN KEY (`returnid`) REFERENCES `return_requests` (`returnid`);

--
-- Constraints for table `return_requests`
--
ALTER TABLE `return_requests`
  ADD CONSTRAINT `return_requests_ibfk_1` FOREIGN KEY (`orderdetailid`) REFERENCES `order_details` (`orderdetailid`);

--
-- Constraints for table `reviews`
--
ALTER TABLE `reviews`
  ADD CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`orderdetailid`) REFERENCES `order_details` (`orderdetailid`),
  ADD CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`reviewerid`) REFERENCES `users` (`userid`);

--
-- Constraints for table `shopping_cart`
--
ALTER TABLE `shopping_cart`
  ADD CONSTRAINT `shopping_cart_ibfk_1` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`),
  ADD CONSTRAINT `shopping_cart_ibfk_2` FOREIGN KEY (`productid`) REFERENCES `products` (`productid`);

--
-- Constraints for table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`roleid`) REFERENCES `role_master` (`roleid`);

--
-- Constraints for table `wishlist`
--
ALTER TABLE `wishlist`
  ADD CONSTRAINT `wishlist_ibfk_1` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`),
  ADD CONSTRAINT `wishlist_ibfk_2` FOREIGN KEY (`productid`) REFERENCES `products` (`productid`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
