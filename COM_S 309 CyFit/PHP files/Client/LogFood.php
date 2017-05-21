<?php
 include_once dirname(__FILE__).'/DBOperation.php';

        $op = new DBOperation();

        if(isset($_POST['ID']) && isset($_POST['name']) && isset($_POST['brand']) && isset($_POST['calories']) && isset($_POST['carbohydrates']) && isset($_POST['protein']) && isset($_POST['fat']) && isset($_POST['servings']) && isset($_POST['date']) && isset($_POST['user'])) {
                $result = $op->LogNewFood($_POST['ID'], $_POST['name'], $_POST['brand'], $_POST['calories'], $_POST['carbohydrates'], $_POST['protein'], $_POST['fat'], $_POST['servings'], $_POST['date'], $_POST['user']);
		echo json_encode($result);
        }
?>
