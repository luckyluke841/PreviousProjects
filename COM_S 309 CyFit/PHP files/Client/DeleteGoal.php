<?php
	include_once dirname(__FILE__).'/DBOperation.php';
	
	$op = new DBOperation();
	if(isset($_POST['userID']) && isset($_POST['goalID'])){
		$op->DeleteGoal($_POST['userID'], $_POST['goalID']);
	}	
?>
