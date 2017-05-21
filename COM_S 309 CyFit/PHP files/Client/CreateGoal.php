<?php

        include_once dirname(__FILE__).'/DBOperation.php';

        $op = new DBOperation();

        if(isset($_POST['goalName']) && isset($_POST['description']) && isset($_POST['userID']) && isset($_POST['goalID'])) {
                $op->CreateNewGoal($_POST['goalID'], $_POST['goalName'], $_POST['description'], $_POST['userID']);
        }

?>
