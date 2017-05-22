<?php

	include_once dirname(__FILE__).'/DBOperation.php';

	$op = new DBOperation();

	if (isset($_POST['email']) && isset($_POST['password'])) {
		$op->UserLogin($_POST['email'], $_POST['password']);
	} else {
		echo "Nope";
	}

?>
