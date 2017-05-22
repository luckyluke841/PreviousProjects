<?php
include_once dirname(__FILE__).'/DBOperation.php';

        $op = new DBOperation();

        if (isset($_POST['email'])) {

                $email = $_POST['email'];
                echo $op->GetUserByEmail($email);
        }

?>
