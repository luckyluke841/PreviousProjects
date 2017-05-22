<?php

include_once dirname(__FILE__).'/DBOperation.php';

$op = new DBOperation();
$response = array('Status' => 'Failure');
 
if (isset($_POST['newFirstname']) && isset($_POST['newEmail']) && isset($_POST['newPassword'])) {
 
    $firstname = $_POST['newFirstname'];
    (isset($_POST['newLastname'])) ? $lastname = $_POST['newLastname'] :
        $lastname = "";
    $email = $_POST['newEmail'];
    $password = $_POST['newPassword'];

    //check if user already exists in db
    //TODO change if statement to query for email 
    if (false) {
	$response['message'] = 'Error: User already exists';
        echo json_encode($response);
    } else {
        $status = $op->UserRegister($firstname, $lastname, $email, $password);
    	$status = json_decode($status);
        if ($status == "true") {
	    $response['Status'] = 'Success';
	    $response['Message'] = 'User created successfully';
            echo json_encode($response);
        } else {
	    $response['Message'] = 'Error: could not create new user';	
            echo json_encode($response);
        }
    }
} else {
    $response['Message'] = 'Error: Username or Password not set for registration';
    echo json_encode($response);
}
?>
