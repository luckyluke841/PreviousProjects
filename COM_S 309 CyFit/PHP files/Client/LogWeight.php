<?php

include_once dirname(__FILE__).'/DBOperation.php';

        $op = new DBOperation();

	if ( isset($_POST['userID']) && isset($_POST['weight']) && isset($_POST['date']) ) {
		$op->LogWeight( $_POST['userID'], $_POST['date'], $_POST['weight'] );
	}
?>
