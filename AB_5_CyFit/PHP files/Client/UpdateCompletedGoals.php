<?php

        include_once dirname(__FILE__).'/DBOperation.php';

        $op = new DBOperation();

        if(isset($_POST['userID']) && isset($_POST['complete']) && isset($_POST['goalID'])) {
                $op->UpdateCompletedGoals($_POST['userID'], $_POST['complete'], $_POST['goalID']);
        }

?>

