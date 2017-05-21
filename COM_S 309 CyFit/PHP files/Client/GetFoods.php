<?php

	include_once dirname(__FILE__).'/DBOperation.php';

	$op = new DBOperation();

	if (isset($_POST['user']) && isset($_POST['date'])) {
		$id = $_POST['user'];
		$date =  $_POST['date'];
		$op->GetFoodArray($id, $date);
	}

?>
