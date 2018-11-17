CREATE TABLE `localizacao` (
  `id` int(4) NOT NULL,
  `name` varchar(100) NOT NULL,
  `address` varchar(150) NOT NULL,
  `type` varchar(20) NOT NULL,
  `lat` float(10,6) DEFAULT NULL,
  `lng` float(10,6) DEFAULT NULL
);

ALTER TABLE `localizacao`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `localizacao`
  MODIFY `id` int(4) NOT NULL AUTO_INCREMENT;


