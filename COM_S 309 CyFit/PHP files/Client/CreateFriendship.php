<?php
	
	include_once dirname(__FILE__).'/DBOperation.php';

        $op = new DBOperation();

        if(isset($_POST['userIDMe']) && isset($_POST['userIDFriend']) && isset($_POST['userNameMe']) && isset($_POST['userNameFriend'])) {
                $op->AddNewFriend($_POST['userIDMe'], $_POST['userIDFriend'], $_POST['userNameMe'], $_POST['userNameFriend']);
        }

?>
