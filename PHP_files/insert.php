<?php

if($_SERVER["REQUEST_METHOD"]=="POST"){
	require 'connections.php';
	createLocation();
}


function createLocation()
{
	global $connect;
	
        $imei = $_POST["imei"];
	$latitude = $_POST["latitude"];	
	$longitude = $_POST["longitude"];
        $endereco = $_POST["endereco"];
        $dataCadastro = $_POST["dataCadastro"];
        
	
	$query = " Insert into pontos(imei, latitude, longitude, dataCadastro) values ('$imei','$latitude','$longitude','$dataCadastro');";
	
	mysqli_query($connect, $query) or die (mysqli_error($connect));
	mysqli_close($connect);
	
}



?>