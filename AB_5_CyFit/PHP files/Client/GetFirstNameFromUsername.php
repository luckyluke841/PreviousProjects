<?php

	include_once dirname(__FILE__).'/DBOperation.php';

	$op = new DBOperation();

	if(isset($_GET['username'])) {
		$op->GetFirstName($_GET['username']);
	}
?>
