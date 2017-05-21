<?php

	include_once dirname(__FILE__).'/DBOperation.php';

	$op = new DBOperation();

	if(isset($_POST['nutritionName']) && isset($_POST['nutritionDescription']) && isset($_POST['userID'])) {
		$op->CreateNewNutrition($_POST['nutritionName'], $_POST['nutritionDescription'], $_POST['userID']);
	}

?>
