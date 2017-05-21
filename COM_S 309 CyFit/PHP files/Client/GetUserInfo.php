<?php

	define('DB_NAME', 'db309ab5');
	define('DB_USER', 'dbu309ab5');
	define('DB_PASSWORD', 'MWRlYjA5NWYz');
	define('DB_HOST', 'mysql.cs.iastate.edu');

	class DBConnect {
		
		private $connection;

		function __construct() {

		}

		function connect() {
			$this->connection = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);

			if (mysqli_connect_errno()) {
				echo "FAILED TO CONNECT WITH DATABASE. ".mysqli_connect_err();
			}
		
			return $this->connection;
		}

	}

	class DBOperation {
		
		private $connection;

		function __construct() {
		
			$db = new DBConnect();

			$this->connection = $db->connect();

		}

		function getUsers() {

			if ($result = $this->connection->query('SELECT * FROM users')) {
				echo json_encode($result->num_rows);
				$result->close();
			}
				
			$this->connection->close();
		}

	}


	$database = new DBOperation();
	$database->getUsers();


?>
