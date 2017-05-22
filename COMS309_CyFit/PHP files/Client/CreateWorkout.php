<?php

	include_once dirname(__FILE__).'/DBOperation.php';

	$op = new DBOperation();

	if(isset($_POST['workoutName']) && isset($_POST['workoutDescription']) && isset($_POST['userID'])) {
		$op->CreateNewWorkout($_POST['workoutName'], $_POST['workoutDescription'], $_POST['userID']);
	}

?>
