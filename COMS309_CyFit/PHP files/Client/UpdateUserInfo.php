<?php


include_once dirname(__FILE__).'/DBOperation.php';

        $op = new DBOperation();

        if (isset($_POST['userID']) && isset($_POST['firstname']) && isset($_POST['lastname']) && isset($_POST['email'])) { 
		echo $op->UpdateUserInfo($_POST['userID'], $_POST['firstname'], $_POST['lastname'], $_POST['email']);
        }

?>
