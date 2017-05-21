<?php

	include_once dirname(__FILE__).'/DBOperation.php';

	$op = new DBOperation();

	if (isset($_POST['userID'])) {
		$id = $_POST['userID'];
		$op->GetNutritionArray($id);
	}

?>
