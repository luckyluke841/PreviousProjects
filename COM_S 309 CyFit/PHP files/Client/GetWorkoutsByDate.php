<?php

        require_once dirname(__FILE__).'/DBOperation.php';

        $op = new DBOperation();

	if (isset($_POST['userID']) && isset($_POST['calendarDate'])) {

                $id = $_POST['userID'];
		$date = $_POST['calendarDate'];
		echo $op->GetWorkoutsByDate($id, $date);
	}

?>

