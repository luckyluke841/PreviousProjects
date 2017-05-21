<?php


include_once dirname(__FILE__).'/DBOperation.php';

        $op = new DBOperation();

        if (isset($_POST['userID']) && isset($_POST['weight']) && isset($_POST['height']) && isset($_POST['age'])) {
                echo $op->CreateWeightAndHeight($_POST['userID'], $_POST['weight'], $_POST['height'], $_POST['age']);
        }

?>
