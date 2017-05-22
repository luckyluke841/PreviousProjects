<?php
 include_once dirname(__FILE__).'/DBOperation.php';

        $op = new DBOperation();
	
	if (isset($_POST['date']) && isset($_POST['user'])) {
		echo $op->GetFoodTotal($_POST['date'], $_POST['user']);
	}
?>
