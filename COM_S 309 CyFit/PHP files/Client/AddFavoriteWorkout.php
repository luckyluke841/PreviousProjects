<?php

        include_once dirname(__FILE__).'/DBOperation.php';

        $op = new DBOperation();

        if(isset($_POST['userID']) && isset($_POST['name']) && isset($_POST['description'])){
                $op->AddFavoriteWorkout($_POST['userID'], $_POST['name'], $_POST['description']);
        }

?>
