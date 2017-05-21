<?php
include_once dirname(__FILE__).'/DBOperation.php';

$op = new DBOperation();
$response = array('Status' => 'Failure');

if (isset($_POST['loginEmail']) && isset($_POST['loginPassword'])) {
 
    $email = $_POST['loginEmail'];
    $password = $_POST['loginPassword'];

    $ID = $op->UserLogin($email, $password);
    $ID = json_decode($ID);
    if ($ID != "false") {
        //user exists in db with entered credentials
	$response['Status'] = 'Success';
        $response['Message'] = 'Login successful';
	$response['Data'] = $ID;
        echo json_encode($response);
    } else {
        //user does not exist in db with entered credentials
        $response['Message'] = 'Error: Invalid credentials';
        echo json_encode($response);
    }
} else {
    //one of the login fields is missing
    $response['Message'] = 'Error: Username or Password is not set for login';
    echo json_encode($response);
}
?>
