CREATE TABLE IF NOT EXISTS `pontos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `imei` varchar(45) DEFAULT NULL,
  `longitude` varchar(45) DEFAULT NULL,
  `latitude` varchar(45) DEFAULT NULL,
  `endereco` varchar(75) DEFAULT NULL,
  `dataCadastro` varchar(75) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=latin1;
