<?php


include_once dirname(__FILE__).'/DBOperation.php';

        $op = new DBOperation();

        if (isset($_POST['firstname'])) {

                $firstname = $_POST['firstname'];
                echo $op->GetUserByName($firstname);
        }

?>
