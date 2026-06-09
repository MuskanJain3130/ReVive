-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Jun 09, 2026 at 02:52 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ReVive_DB`
--

-- --------------------------------------------------------

--
-- Table structure for table `addresses`
--

CREATE TABLE `addresses` (
  `addressid` int(11) NOT NULL,
  `userid` int(11) DEFAULT NULL,
  `fullname` varchar(100) DEFAULT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `street` varchar(150) DEFAULT NULL,
  `city` varchar(80) DEFAULT NULL,
  `state` varchar(80) DEFAULT NULL,
  `zipcode` varchar(15) DEFAULT NULL,
  `country` varchar(80) DEFAULT NULL,
  `address_type` enum('Home','Work','Other') DEFAULT NULL,
  `is_default` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `addresses`
--

INSERT INTO `addresses` (`addressid`, `userid`, `fullname`, `phone`, `street`, `city`, `state`, `zipcode`, `country`, `address_type`, `is_default`) VALUES
(1, 1, 'Buyer One', '9999999999', 'Street 1', 'Mumbai', 'Maharashtra', '400001', 'India', 'Home', 1),
(2, 1, 'Buyer One Office', '8888888888', 'Street 2', 'Mumbai', 'Maharashtra', '400002', 'India', 'Work', 0),
(3, 6, 'tamanna shah', '6355133411', 'surat', 'surat', NULL, '395007', NULL, NULL, 0),
(4, 7, 'khushi', '09974900151', 'khushishah2427@gmail.com', 'Surat', 'Gujarat', '394210', 'India', 'Home', 1),
(5, 8, 'Muskan Jain', '09898588821', '803 Bhavya, Punyabhoomi, near Bhagwan Mahavir College, VIP Road 2', 'Surat', NULL, '395007', NULL, NULL, 0);

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `orderid` int(11) NOT NULL,
  `buyerid` int(11) DEFAULT NULL,
  `orderdate` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` enum('Placed','Packed','Shipped','Delivered','Cancelled','Returned') DEFAULT NULL,
  `addressid` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`orderid`, `buyerid`, `orderdate`, `status`, `addressid`) VALUES
(1, 1, '2026-02-26 14:56:15', 'Placed', 1),
(2, 2, '2026-03-30 12:30:00', 'Placed', NULL),
(3, 6, '2026-05-07 16:54:39', 'Placed', 3),
(4, 6, '2026-05-07 16:59:37', 'Placed', 3),
(5, 6, '2026-05-07 17:12:46', 'Placed', 3),
(6, 7, '2026-06-05 10:45:05', 'Placed', 4),
(7, 7, '2026-06-05 11:01:05', 'Placed', 4),
(8, 7, '2026-06-05 11:15:45', 'Packed', 4),
(9, 7, '2026-06-05 11:20:35', 'Placed', 4),
(10, 8, '2026-06-09 12:41:57', 'Placed', 5),
(11, 8, '2026-06-09 12:45:26', 'Placed', 5);

-- --------------------------------------------------------

--
-- Table structure for table `order_details`
--

CREATE TABLE `order_details` (
  `orderdetailid` int(11) NOT NULL,
  `orderid` int(11) DEFAULT NULL,
  `productid` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `price` double(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `order_details`
--

INSERT INTO `order_details` (`orderdetailid`, `orderid`, `productid`, `quantity`, `price`) VALUES
(1, 1, 3, 1, 2000.00),
(2, 1, 1, 1, 25000.00),
(3, 5, 16, 1, 150.00),
(4, 6, 4, 1, 45000.00),
(5, 7, 4, 1, 45000.00),
(6, 7, 5, 1, 45000.00),
(7, 8, 4, 1, 45000.00),
(8, 9, 4, 1, 45000.00),
(9, 10, 15, 1, 200.00),
(10, 10, 4, 1, 45000.00),
(11, 11, 1, 1, 25000.00);

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
  `paymentid` int(11) NOT NULL,
  `orderid` int(11) DEFAULT NULL,
  `amount` double(10,2) DEFAULT NULL,
  `payment_mode` enum('UPI','Card','NetBanking','COD','Razorpay') DEFAULT NULL,
  `payment_status` enum('Pending','Completed','Failed') DEFAULT NULL,
  `payment_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `razorpay_payment_id` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `payments`
--

INSERT INTO `payments` (`paymentid`, `orderid`, `amount`, `payment_mode`, `payment_status`, `payment_date`, `razorpay_payment_id`) VALUES
(1, 1, 27000.00, 'UPI', 'Completed', '2026-02-26 14:57:39', NULL),
(2, 3, 30.00, 'COD', 'Pending', '2026-05-07 16:54:39', NULL),
(3, 4, 760.00, 'COD', 'Pending', '2026-05-07 16:59:37', NULL),
(4, 5, 150.00, 'COD', 'Pending', '2026-05-07 17:12:46', NULL),
(5, 6, 45000.00, 'COD', 'Pending', '2026-06-05 10:45:05', NULL),
(6, 7, 90000.00, 'COD', 'Pending', '2026-06-05 11:01:05', NULL),
(7, 8, 45000.00, 'COD', 'Pending', '2026-06-05 11:15:45', NULL),
(8, 9, 45000.00, 'COD', 'Pending', '2026-06-05 11:20:35', NULL),
(9, 10, 45200.00, 'Razorpay', 'Completed', '2026-06-09 12:41:57', 'pay_SzXJZco30CoCSx'),
(10, 11, 25000.00, 'Razorpay', 'Completed', '2026-06-09 12:45:26', 'pay_SzXNM3MivYEGbk');

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `productid` int(11) NOT NULL,
  `title` varchar(100) DEFAULT NULL,
  `image_url` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `price` double(10,2) DEFAULT NULL,
  `product_condition` enum('New','Like New','Good','Fair','Poor') DEFAULT NULL,
  `quantity` int(11) DEFAULT 1,
  `status` enum('Active','Inactive') DEFAULT 'Active',
  `sellerid` int(11) DEFAULT NULL,
  `approval_status` enum('Pending','Approved','Rejected') DEFAULT 'Pending',
  `approved_by` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `approved_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`productid`, `title`, `image_url`, `description`, `price`, `product_condition`, `quantity`, `status`, `sellerid`, `approval_status`, `approved_by`, `created_at`, `approved_at`) VALUES
(1, 'iPhone 11', 'iphone11.png', 'Good condition used phone', 25000.00, 'Good', 4, 'Active', 1, 'Approved', NULL, '2026-02-26 14:39:22', NULL),
(2, 'Office Chair', 'office_chair.png', 'Comfortable ergonomic chair', 3500.00, 'Like New', 2, 'Active', 1, 'Rejected', NULL, '2026-02-26 14:39:22', NULL),
(3, 'Gaming Keyboard', 'gaming_keyboard.png', 'RGB mechanical keyboard', 1800.00, 'Good', 10, 'Active', 1, 'Approved', NULL, '2026-02-26 14:39:22', NULL),
(4, 'Test Laptop', 'laptop.png', 'i5 8GB RAM', 45000.00, NULL, 0, 'Active', 3, 'Approved', NULL, '2026-03-07 13:33:03', NULL),
(5, 'Test Laptop', 'laptop.png', 'i5 8GB RAM', 45000.00, NULL, 4, 'Active', 3, 'Approved', NULL, '2026-03-07 13:33:06', NULL),
(6, 'Test Laptop', 'laptop.png', 'i5 8GB RAM', 45000.00, NULL, 5, 'Active', 3, 'Rejected', NULL, '2026-03-14 08:28:39', NULL),
(7, 'Test Laptop', 'laptop.png', 'i5 8GB RAM', 45000.00, NULL, 5, 'Active', 3, 'Rejected', NULL, '2026-03-14 08:30:42', NULL),
(8, 'Test Laptop', 'laptop.png', 'i5 8GB RAM', 45000.00, NULL, 5, 'Active', 3, 'Rejected', NULL, '2026-03-14 08:31:18', NULL),
(9, 'Sample Product', 'sample_product.png', 'A test product', 59.99, 'New', 5, 'Active', 3, 'Approved', NULL, '2026-03-29 09:43:47', NULL),
(10, 'shampoo', 'shampoo.png', 'abc', 700.00, 'Like New', NULL, NULL, 6, 'Approved', NULL, '2026-05-02 18:26:28', NULL),
(11, 'shampoo2', 'shampoo.png', 'shampoos', 400.00, 'New', 8, NULL, 6, 'Approved', NULL, '2026-05-02 18:33:27', NULL),
(12, 'fruit', 'b97921bd-f657-47cc-9037-1facb2d77626.jpeg;26eb339b-1951-48a0-b1ef-cd9b0816d96c.jpg;96e1b54b-47fe-4e8d-9fe8-5f76a01bb1b2.png', 'abc', 50.00, 'New', 5, NULL, 6, 'Rejected', NULL, '2026-05-02 18:59:32', NULL),
(13, 'fruit', '75702bfd-195c-4ab0-a30e-c227bc93fc8d.jpeg;beee112e-193a-4c80-bc13-16985271fab2.jpg', 'abc', 60.00, 'New', 5, NULL, 6, 'Approved', NULL, '2026-05-02 19:02:11', NULL),
(14, 'orange', 'ad1253c8-fd9b-43f5-b429-2347940fd90f.jpg', 'Fresh oranges straight from the orchard', 30.00, 'New', 15, NULL, 6, 'Approved', NULL, '2026-05-07 16:48:35', NULL),
(15, 'soap', 'soap.png', 'soap!', 200.00, 'Good', 2, NULL, 6, 'Approved', NULL, '2026-05-07 17:00:36', NULL),
(16, 'shower gel', '47a2c472-86d5-49b9-a410-33d834af9e3a.jpg', 'shower gel\n', 150.00, 'New', 4, NULL, 6, 'Approved', NULL, '2026-05-07 17:01:40', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `refunds`
--

CREATE TABLE `refunds` (
  `refundid` int(11) NOT NULL,
  `returnid` int(11) DEFAULT NULL,
  `amount` double(10,2) DEFAULT NULL,
  `status` enum('Pending','Processed','Failed') DEFAULT NULL,
  `processed_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `refunds`
--

INSERT INTO `refunds` (`refundid`, `returnid`, `amount`, `status`, `processed_at`) VALUES
(3, 2, 2000.00, 'Processed', '2026-02-26 15:05:53'),
(4, 3, 25000.00, 'Pending', '2026-02-26 15:02:31'),
(5, 5, 45000.00, 'Pending', '2026-06-05 11:26:13');

-- --------------------------------------------------------

--
-- Table structure for table `return_requests`
--

CREATE TABLE `return_requests` (
  `returnid` int(11) NOT NULL,
  `orderdetailid` int(11) DEFAULT NULL,
  `reason` text DEFAULT NULL,
  `status` enum('Requested','Approved','Rejected','Completed') DEFAULT NULL,
  `requested_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `return_requests`
--

INSERT INTO `return_requests` (`returnid`, `orderdetailid`, `reason`, `status`, `requested_at`) VALUES
(2, 1, 'Item defective', 'Approved', '2026-02-26 14:57:55'),
(3, 2, 'Wrong item', 'Requested', '2026-02-26 14:57:55'),
(4, 2, 'Product was damaged on arrival', 'Requested', '2026-03-29 11:47:38'),
(5, 8, 'my life my rules', 'Approved', '2026-06-05 11:23:11');

-- --------------------------------------------------------

--
-- Table structure for table `reviews`
--

CREATE TABLE `reviews` (
  `reviewid` int(11) NOT NULL,
  `orderdetailid` int(11) DEFAULT NULL,
  `reviewerid` int(11) DEFAULT NULL,
  `rating` int(11) DEFAULT NULL,
  `comment` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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

CREATE TABLE `role_master` (
  `roleid` int(11) NOT NULL,
  `rolename` varchar(30) DEFAULT NULL
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

CREATE TABLE `shopping_cart` (
  `cartid` int(11) NOT NULL,
  `userid` int(11) DEFAULT NULL,
  `productid` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT 1,
  `added_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `shopping_cart`
--

INSERT INTO `shopping_cart` (`cartid`, `userid`, `productid`, `quantity`, `added_at`) VALUES
(1, 1, 1, 1, '2026-03-29 10:15:32');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `userid` int(11) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `roleid` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `phone` varchar(15) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`userid`, `username`, `email`, `password`, `roleid`, `created_at`, `phone`) VALUES
(1, 'admin1', 'admin@mail.com', '123', 2, '2026-02-26 14:25:08', NULL),
(2, 'buyer1', 'buyer@mail.com', '123', 1, '2026-02-26 14:25:08', NULL),
(3, 'seller1', 'seller@mail.com', '123', 1, '2026-02-26 14:25:08', NULL),
(5, 'tamanna', 'tam@gmail.com', 'PBKDF2WithHmacSHA256:2048:8SJJ5tzgp1OYqxAwQx+m0upkCPWM8eb3Nn97/cLsk14=:4cupkOp2Cob5a4prb/4S6+bZIGzp9NFHRWL5YSfOAkI=', 1, '2026-03-30 11:32:50', NULL),
(6, 'abc', 'abc@gmail.com', 'PBKDF2WithHmacSHA256:2048:LNnLSjtPYsglauBJ5oW0Bjz6aZXGa3RhTM5J9XwGWm4=:7elJ+YxFnP0Q76j5UvBvkn9jvFctu5DZwnE85Pazp0g=', 2, '2026-03-31 18:29:36', '6355133411'),
(7, 'khushi', 'khushishah2427@gmail.com', 'PBKDF2WithHmacSHA256:2048:e2L8Lai5WzYVMnFX2MdJQvInCiT/l6NTwsBMyMffJHI=:UtvExjFP5iTk8tI6kR6xuuMgmXN41008vv2MsbiRYxQ=', 2, '2026-06-04 11:46:03', '9974900151'),
(8, 'muskan30', 'muskanajain30@gmail.com', 'PBKDF2WithHmacSHA256:2048:sTiYpOHG9ewV6seSBnpCCfHPa40/lWZVQNrVY/z6/1U=:dR/jG6N00d0OstXTNbjIc2Ey/DqgTWhXtOlRLdQs65M=', 2, '2026-06-09 11:47:21', '9898588821');

-- --------------------------------------------------------

--
-- Table structure for table `wishlist`
--

CREATE TABLE `wishlist` (
  `wishlistid` int(11) NOT NULL,
  `userid` int(11) DEFAULT NULL,
  `productid` int(11) DEFAULT NULL,
  `added_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `wishlist`
--

INSERT INTO `wishlist` (`wishlistid`, `userid`, `productid`, `added_at`) VALUES
(1, 1, 1, '2026-02-26 15:02:59'),
(2, 1, 3, '2026-02-26 15:02:59'),
(3, 1, 2, '2026-03-14 08:28:40'),
(8, 2, 1, '2026-03-29 11:29:18');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `addresses`
--
ALTER TABLE `addresses`
  ADD PRIMARY KEY (`addressid`),
  ADD KEY `userid` (`userid`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`orderid`),
  ADD KEY `buyerid` (`buyerid`),
  ADD KEY `addressid` (`addressid`);

--
-- Indexes for table `order_details`
--
ALTER TABLE `order_details`
  ADD PRIMARY KEY (`orderdetailid`),
  ADD KEY `orderid` (`orderid`),
  ADD KEY `productid` (`productid`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`paymentid`),
  ADD KEY `orderid` (`orderid`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`productid`),
  ADD KEY `sellerid` (`sellerid`),
  ADD KEY `approved_by` (`approved_by`);

--
-- Indexes for table `refunds`
--
ALTER TABLE `refunds`
  ADD PRIMARY KEY (`refundid`),
  ADD KEY `returnid` (`returnid`);

--
-- Indexes for table `return_requests`
--
ALTER TABLE `return_requests`
  ADD PRIMARY KEY (`returnid`),
  ADD KEY `orderdetailid` (`orderdetailid`);

--
-- Indexes for table `reviews`
--
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`reviewid`),
  ADD KEY `orderdetailid` (`orderdetailid`),
  ADD KEY `reviewerid` (`reviewerid`);

--
-- Indexes for table `role_master`
--
ALTER TABLE `role_master`
  ADD PRIMARY KEY (`roleid`),
  ADD UNIQUE KEY `rolename` (`rolename`);

--
-- Indexes for table `shopping_cart`
--
ALTER TABLE `shopping_cart`
  ADD PRIMARY KEY (`cartid`),
  ADD UNIQUE KEY `userid` (`userid`,`productid`),
  ADD KEY `productid` (`productid`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`userid`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `roleid` (`roleid`);

--
-- Indexes for table `wishlist`
--
ALTER TABLE `wishlist`
  ADD PRIMARY KEY (`wishlistid`),
  ADD UNIQUE KEY `userid` (`userid`,`productid`),
  ADD KEY `productid` (`productid`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `addresses`
--
ALTER TABLE `addresses`
  MODIFY `addressid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `orderid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `order_details`
--
ALTER TABLE `order_details`
  MODIFY `orderdetailid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `paymentid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `productid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `refunds`
--
ALTER TABLE `refunds`
  MODIFY `refundid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `return_requests`
--
ALTER TABLE `return_requests`
  MODIFY `returnid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `reviews`
--
ALTER TABLE `reviews`
  MODIFY `reviewid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `shopping_cart`
--
ALTER TABLE `shopping_cart`
  MODIFY `cartid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `userid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `wishlist`
--
ALTER TABLE `wishlist`
  MODIFY `wishlistid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

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
