<?php


include_once dirname(__FILE__).'/DBOperation.php';

	$op = new DBOperation();

	if (isset($_POST['userID'])) {

		$id = $_POST['userID'];
                echo $op->GetUserByID($id);
        }

?>
