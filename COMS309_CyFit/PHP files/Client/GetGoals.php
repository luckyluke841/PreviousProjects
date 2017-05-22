<?php

        include_once dirname(__FILE__).'/DBOperation.php';

        $op = new DBOperation();	
	if(isset($_POST['userID'])){
        	$op->GetGoals($_POST['userID']);
	}

?>
