<?php
	require_once(dirname(__FILE__).'/ConnectionInfo.php');

	
if (isset($_POST['FirstName']) && isset($_POST['LastName']))
{
	//Get the POST variables
	$mName = $_POST['FirstName'];
	$mNumber = $_POST['LastName'];
	
	//Set up our connection
	$connectionInfo = new ConnectionInfo();
	$connectionInfo->GetConnection();
	
	if (!$connectionInfo->conn)
	{
		//Connection failed
		echo 'No Connection';
	}
	
	else
	{
		//Insert new contact into database
		$query = 'INSERT INTO Users (Users_First_Name, Users_Last_Name) VALUES (?, ?)';
		$parameters = array($mFirstName, $mLastName);

		//Execute query
		$stmt = sqlsrv_query($connectionInfo->conn, $query, $parameters);
		
		if (!$stmt)
		{	//The query failed
			echo 'Query Failed';	
		}
		
		else
		{
			//The query succeeded, now echo back the new contact ID
			$query = "SELECT IDENT_CURRENT('Users') AS NewID";
			$stmt = sqlsrv_query($connectionInfo->conn, $query);
			
			$row = sqlsrv_fetch_array($stmt,SQLSRV_FETCH_ASSOC);
						
			echo $row['NewID'];	
		}
	}
}

?>