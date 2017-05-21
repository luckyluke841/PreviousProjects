<?php
	include_once dirname(__FILE__).'/DBOperation.php';

        $op = new DBOperation();
		
        if(isset($_POST['userID'])){
		$userID = $_POST['userID'];
                $op->GetFriends($userID);
        }


