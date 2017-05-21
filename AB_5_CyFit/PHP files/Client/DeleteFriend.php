<?php
        include_once dirname(__FILE__).'/DBOperation.php';

        $op = new DBOperation();
        if(isset($_POST['userIDMe']) && isset($_POST['userIDFriend'])){
                $op->DeleteFriend($_POST['userIDMe'], $_POST['userIDFriend']);
        }
?>
