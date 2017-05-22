<?php
/**
 * File that contains the DBConnect php class.
 */

	/**
	 *	Class representing a connection to the database. 
	 */
	class DBConnect {

        /**
         * @var mysqli|object Stores the connection to the database.
         */
        private $connection;

        /**
         * DBConnect constructor.
         */
        function __construct() {

		}

		/**
		 *	Creates and returns an object representing the connection to the MySQL database.
		 *
		 *	@return object Returns an object representing the connection to the database.
		 */
		function connect() {

			include_once dirname(__FILE__).'/Constants.php';
			
			$this->connection = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);

			if (mysqli_connect_errno()) {
				echo "Failed to connect".mysqli_connect_err();
			}

			return $this->connection;	

		}

	}

?>
