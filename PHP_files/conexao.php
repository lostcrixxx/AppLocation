<?php

$hostname = "";
$databaseo = "";
$username = "";
$password = "";
try 
{
    $conn = new PDO('mysql:host='.$hostname.';dbname='.$database, $username, $password,array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} 
catch(PDOException $e) 
{
    echo 'ERROR: ' . $e->getMessage();
}

?>