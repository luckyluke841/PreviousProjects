<?php
/**
 * File containing the DBOperation class.
 */

/**
 * Class DBOperation used to execute operations or queries on the database.
 */
class DBOperation {

    /**
     * @var mysqli|object Stores an instance of the connection object to the MYSQL database.
     */
    private $connection;

    /**
     * DBOperation constructor.
     */
    function __construct() {
			
			 //include rsa library
                	$path = 'phpseclib';
                	set_include_path(get_include_path() . PATH_SEPARATOR . $path);
                	include_once('Crypt/RSA.php');

			include_once dirname(__FILE__).'/DBConnect.php';

			$database = new DBConnect();

			$this->connection = $database->connect();

		}


    /**
     * Gets the number of users in the table and echos the encoded json result.
	 *
	 * Performs the query 'SELECT * FROM users' on the database, and echos an encoded json
	 * containing an integer field with the number of rows in the result query. Then closes
	 * the connection.
	 *
	 * @return void
     */
    function GetNumUsers() {
	
			if ($result = $this->connection->query('SELECT * FROM users')) {
				echo json_encode($result->num_rows);
				$result->close();
			}
				
			$this->connection->close();
		}


    /**
     * Gets an array of the users in the database and returns the result in an encoded json.
	 *
	 * Performs the query 'SELECT username FROM users' on the database, and populates a json
     * array with the results and echos the encoded json. Then closes the connection.
     *
     * @return void
     */
    function GetUsers() {
			
			if($result = $this->connection->query('SELECT username FROM users')) {
		
				$users = array();
		
				while ($row = $result->fetch_assoc()) {
					array_push($users, $row['username']);
				}				

				echo json_encode($users);

				$result->close();
			}
			
			$this->connection->close();
		}

     /* Gets an array of JSON objects representing the workouts stored in the database.
     *
     * Calls a query on the database that retreives the stored workouts in the database
     * and builds an array of them, encodes the array, and closes the connection.
     *
     * @return void
     */
    function GetWorkoutsForBrowsing($userID) {
			
			if($result = $this->connection->query('SELECT DISTINCT workoutID, workoutName FROM workouts JOIN users ON \''.$userID.'\' = workouts.userID')) {

				$workouts = array();

				while ($row = $result->fetch_assoc()) {
					array_push($workouts, array('workoutName' => $row['workoutName']));
				}

				$response = array('workouts' => json_encode($workouts));

				echo json_encode($response);

				$result->close();
			}

			$this->connection->close();
		}


    /**
     * Creates a workout from the given $name and $description parameters.
     *
     * Crates a workout from parameters that are passed in as strings. Executes
     * the MySQL statement to add the workout to the database. Encodes a json object
     * stating the operation was a success and closes the connection.
     *
     * @param string $name The name of the workout.
     * @param string $description The description for the workout.
     *
     * @return void
     */
    function CreateNewWorkout($name, $description, $userID) {
	
			$stmt = 'INSERT INTO workouts (workoutName, description, userID, created_at) VALUES (\''.$name.'\', \''.$description.'\', \''.$userID.'\', NOW())';

			if ($result = $this->connection->query($stmt)) {
			}

			$this->connection->close();

		}
		
	    /**
     * Creates a new goal from the given $name and $description and $goalID parameters.
     *
     * Crates a goal from parameters that are passed in as strings. Executes
     * the MySQL statement to add the workout to the database. Encodes a json object
     * stating the operation was a success and closes the connection.
     *
     * @param string $name The name of the workout.
     * @param string $description The description for the workout.
     * @param string $userID The userID
     * @return void
     */
	function CreateNewGoal($goalID, $goalName, $description, $userID){
		$stmt = 'INSERT INTO goals (goalID, goalName, description, userID, complete) VALUES ('.$goalID.', \''.$goalName.'\', \''.$description.'\', \''.$userID.'\', 0)';
	
		if($result = $this->connection->query($stmt)) {
		}
		
		$this->connection->close();
	}
    /**
     * Gets a all the workouts and builds an array of JSON objects containing the names and
     * descriptions of the workouts.
     *
     * Gets all the workouts from the workouts table in the database and builds a JSON object for
     * each one containing the workout name and description. An array of all the JSON objects is
     * then built and encoded and echoed. The connection is then closed.
     *
     * @return void
     */
    function GetWorkoutsArray($userID) {
			
			$stmt = 'SELECT DISTINCT workoutName, description FROM workouts JOIN users ON \''.$userID.'\' = workouts.userID';

			if ($result = $this->connection->query($stmt)) {

				$arr = array();

				while ($row = $result->fetch_assoc()) {
					array_push($arr, array("workoutName" => $row['workoutName'], "description" => $row['description']));
				}

				echo json_encode($arr);

				$result->close();
			}

			$this->connection->close();

		}

    function GetWorkoutsByDate($userID, $date) {

	$stmt = 'SELECT DISTINCT workoutName, description FROM workouts JOIN users ON \''.$userID.'\' = workouts.userID WHERE \''.$date.'\' = DATE(workouts.created_at)';

 	if ($result = $this->connection->query($stmt)) {

                                $workouts = array();

                                while ($workout = $result->fetch_assoc()) {
                                        array_push($workouts, array('name' => $workout['workoutName'], 'description' => $workout['description']));
                                }
				$result->close();
                        }
        $this->connection->close();
	return json_encode($workouts);

    }

    /**
     * Performs an statement on the database to get an array of JSON's for all the workouts in the workout table.
     *
     * Executes a statement on the database to retreive all the workouts from the nutrition table. Each workout
     * is used to build a JSON object containing the nutrition name, description, and calories data. Each JSON
     * is then added to an array which is encoded and echoed. The connection is then closed.
     *
     * @return void
     */
    function GetFoodArray($userID, $date) {
		
			$seconds = $date / 1000;
                	$curDate = date("Y-m-d", $seconds);
	
			if ($result = $this->connection->query('SELECT * FROM food WHERE userID=\''.$userID.'\' AND date=\''.$curDate.'\'')) {
				$arr = array();

				while ($row = $result->fetch_assoc()) {
					$ID = $row['ID'];
					$brand = $row['brand'];
					$name = $brand . " " . $row['name'] . " (" . $row['servings'] . ")";
					$description = "Calories: " . $row['calories'] . " | Carbs: " . $row['carbohydrates'] . " | Protein: " . $row['protein'] . " | Fat: " . $row['fat'];  
					array_push($arr, array("ID" => $ID, "name" => $name, "brand" => $brand, "description" => $description));
				}

				echo json_encode($arr);

				$result->close();
			}

			$this->connection->close();
		
    }

	function LogWeight($id, $date, $weight) {

		$seconds = $date / 1000;
		$curDate = date("Y-m-d", $seconds);
		$stmt1 = 'UPDATE userData SET weight=\''.$weight.'\' WHERE userID=\''.$id.'\'';
		$stmt2 = 'INSERT INTO weight_history(date, weight, userID) VALUES (\''.$curDate.'\', \''.$weight.'\', \''.$id.'\')';
		$stmt3 = 'SELECT * FROM weight_history WHERE date=\''.$curDate.'\' AND userID=\''.$id.'\'';  
		$stmt4 = 'UPDATE weight_history SET weight=\''.$weight.'\' WHERE userID=\''.$id.'\' AND date=\''.$curDate.'\'';

		$result3 = $this->connection->query($stmt3);

		if ($result3->num_rows == 1) {
			$result4 = $this->connection->query($stmt4);
		} elseif ( date('Ymd') == date('Ymd', $seconds) ) {
			$result1 = $this->connection->query($stmt1);
		}
		$result2 = $this->connection->query($stmt2);
		echo json_encode("Update: " . $result1 . " Insert : " . $result2);
		$this->connection->close();
	}

	function GetWeightHistory ($id, $date) {

		$seconds = $date / 1000;
        	$curDate = date("Y-m-d", $seconds);
        	if ($result = $this->connection->query('SELECT * FROM weight_history WHERE userID=\''.$id.'\'')) {
                                $history = array();
                                while ($row = $result->fetch_assoc()) {        
					array_push($history, array("date" => $row['date'], "weight" => $row['weight']));
				}
			echo json_encode($history);
		}
	}

	//check if supplied login credentials are valid
	function UserLogin($email, $password) {
 		$stmt = 'SELECT * FROM users WHERE email=\''.$email.'\'';
		//check if user exists
		if ($result = $this->connection->query($stmt)) {
			$user = $result->fetch_assoc();
			$encrypted_password = base64_decode($user['encrypted_password']);
			//decrypt stored password
			$rsa = new Crypt_RSA();
			$rsa->loadKey($user['privatekey']);
			$rsa->setEncryptionMode(CRYPT_RSA_ENCRYPTION_PKCS1);
			$decrypted_password = $rsa->decrypt($encrypted_password);
			//check if passwords match
			if (strcmp($decrypted_password, $password) == 0) {
				return json_encode($user['ID']);
			} else {
				return json_encode("false");
			}
		} else {
			 return json_encode("false");
		}
		$this->connection->close();
	}
	
	//Retrieves the current goals for the user
    	function GetGoals($userID){

		if($result = $this->connection->query('SELECT DISTINCT goals.goalID, goals.goalName, goals.description, goals.complete FROM goals JOIN users ON \''.$userID.'\' = goals.userID')){
			$arr = array();
			while($row = $result->fetch_assoc()){
				array_push($arr, array("goalID" => $row['goalID'], "goalName" => $row['goalName'], "description" => $row['description'], "complete" => $row['complete']));		
			}
			echo json_encode($arr);
			
			$result->close();
		}		
		$this->connection->close();
	}

	/**
	*Returns the total number of goals so that the overall goal ID can be chosen.
	*/
	function GetTotalNumGoals($userID){
		$query = 'SELECT COUNT(*) FROM goals WHERE userID=\''.$userID.'\'';
		if($result = $this->connection->query($query)){
			$arr = array();
			while($row = $result->fetch_assoc()){
				array_push($arr, array('totalNumGoals' => $row['COUNT(*)']));
			}
			echo json_encode($arr);
		}
		$this->connection->close();
	} 


	/**
	*Obtains the current user's friends from the DB.
	* @param userID
	*/
	function GetFriends($userID){
		if($result = $this->connection->query('SELECT userNameFriend FROM friends WHERE userIDMe=\''.$userID.'\'')) {
			$arr = array();
			while($row = $result->fetch_assoc()){
				array_push($arr, array('friendName' => $row['userNameFriend']));
			}
			echo json_encode($arr);

			$result->close();	
		}
		$this->connection->close();
	}


	function LogNewFood($id, $name, $brand, $calories, $carbs, $protein, $fat, $servings, $date, $user) {

		$seconds = $date / 1000;
		$curDate = date("Y-m-d", $seconds);
		
		$stmt1 = 'SELECT * FROM food WHERE date=\''.$curDate.'\' AND ID=\''.$id.'\'';  
		$result1 = $this->connection->query($stmt1);

		if ($result1->num_rows == 1) {
			$food = $result1->fetch_assoc();
			$curServings = $food['servings'];
			$updateServings = (int)$curServings + (int)$servings;
			$newServings = "$updateServings";

			$stmt2 = 'UPDATE food SET servings=\''.$newServings.'\' WHERE date=\''.$curDate.'\' AND ID=\''.$id.'\'';
		} else {
                	$stmt2 = 'INSERT INTO food (ID, name, brand, calories, carbohydrates, protein, fat, servings, date, userID) VALUES (\''.$id.'\', \''.$name.'\', \''.$brand.'\', \''.$calories.'\', \''.$carbs.'\', \''.$protein.'\', \''.$fat.'\', \''.$servings.'\', \''.$curDate.'\', \''.$user.'\')';
		}
                $result2 = $this->connection->query($stmt2);
        	$this->connection->close();
		return $result2;
        }
	
	function GetFoodTotal($date, $id) {

		$seconds = $date / 1000;
        	$curDate = date("Y-m-d", $seconds);

		$stmt = 'SELECT * FROM food WHERE date=\''.$curDate.'\' AND userID=\''.$id.'\'';

		$protein = $carbs = $fat = $calories = 0;

		if ($result = $this->connection->query($stmt)) {
			while($food = $result->fetch_assoc()) {
				$servings = (double)$food['servings'];
				$protein = $protein + ((double)$food['protein'] * $servings);
				$carbs = $carbs + ((double)$food['carbohydrates'] * $servings);
				$fat = $fat + ((double)$food['fat'] * $servings);
				$calories = $calories + ((double)$food['calories'] * $servings);
			}
			$result->close();
		}
		$total = json_encode(array('protein' => $protein, 'carbs' => $carbs, 'fat' => $fat, 'calories' => $calories));
		$this->connection->close();
        	return $total;
	}

	//add user credentials to database with password encryption
	function UserRegister($firstname, $lastname, $email, $password) {
    		//setup rsa key
    		$rsa = new Crypt_RSA();
    		$rsa->setPrivateKeyFormat(CRYPT_RSA_PRIVATE_FORMAT_PKCS1);
    		$rsa->setPublicKeyFormat(CRYPT_RSA_PUBLIC_FORMAT_PKCS1);
    		extract($rsa->createKey(1024));
		
		//encrypt password
		$rsa->loadkey($publickey);
		$rsa->setEncryptionMode(CRYPT_RSA_ENCRYPTION_PKCS1);
		$encrypted_password = $rsa->encrypt($password);
		$encrypted_password = base64_encode($encrypted_password);
		
		//generate unique id for user
		$uuid = uniqid('', true);

		$stmt = 'INSERT INTO users (ID, firstname, lastname, email, encrypted_password, privatekey, created_at) VALUES (\''.$uuid.'\', \''.$firstname.'\', \''.$lastname.'\', \''.$email.'\', \''.$encrypted_password.'\', \''.$privatekey.'\', NOW())';                         
		$result = $this->connection->query($stmt);

        	//echo db execution status
		$this->connection->close();
		return json_encode($result);
	}

	function GetUserByID($userID) {
		$stmt = 'SELECT * FROM users WHERE ID=\''.$userID.'\'';

                if ($result = $this->connection->query($stmt)) {
			$user = $result->fetch_assoc();
			$this->connection->close();
			return json_encode($user);
		}

	}

	/**
	*Given the user's email, return all information from the users table about that particular user.
	*/
	function GetUserByEmail($email){
		$stmt = 'SELECT * FROM users WHERE email=\''.$email.'\'';
		if($result = $this->connection->query($stmt)){
			$user = $result->fetch_assoc();
			$this->connection->close();
			return json_encode($user);
		}

	}

	/**
	*Create new friendship between two users. Add two new rows to the friends table.
	*/
	function AddNewFriend($userIDMe, $userIDFriend, $userNameMe, $userNameFriend){
		$stmt = 'INSERT INTO friends (userIDMe, userIDFriend, userNameFriend) VALUES (\''.$userIDMe.'\', \''.$userIDFriend.'\', \''.$userNameFriend.'\'), (\''.$userIDFriend.'\', \''.$userIDMe.'\', \''.$userNameMe.'\')';
		
		
		if($result = $this->connection->query($stmt)){
		}
		$this->connection->close();
	}

	/**
	*Get the weight and height when the userID is passed in.
	*/
	function GetWeightAndHeight($userID){
		$stmt = 'SELECT * FROM userData WHERE userID = \''.$userID.'\'';

		if($result = $this->connection->query($stmt)){
			$arr = array();
			
                        while($row = $result->fetch_assoc()){
                                array_push($arr, array('weight' => $row['weight'], 'height' => $row['height'], 'age' => $row['age'], 'gender' => $row['gender']));
                        }
                        echo json_encode($arr);
                }
                $this->connection->close();
	}

	/**
	*Update user info for the $userID given.
	*/
	function UpdateUserInfo($userID, $firstname, $lastname, $email){
		$userTableQuery = 'UPDATE users SET firstname=\''.$firstname.'\', lastname=\''.$lastname.'\', email=\''.$email.'\' WHERE ID=\''.$userID.'\'';

		if($result = $this->connection->query($userTableQuery)){
		}
		$this->connection->close();		
		
	}

	/**
	*Update the User's weight and height. 
	*Parameters are $userID, weight, and height
	*/
	function UpdateWeightAndHeight($userID, $weight, $height, $age){
		$updateQuery = 'UPDATE userData SET height=\''.$height.'\', weight=\''.$weight.'\', age=\''.$age.'\'  WHERE userID=\''.$userID.'\'';
		if($result = $this->connection->query($updateQuery)){
                }
                $this->connection->close();
	}

	/**
	*Creates a new row in the weight and height table
	*Parameters are userID, weight, and height
	*/
	function CreateWeightAndHeight($userID, $weight, $height, $age){
		$createQuery='INSERT INTO userData (userID, height, weight, age) VALUES (\''.$userID.'\', \''.$height.'\', \''.$weight.'\', \''.$age.'\')';
                if($result = $this->connection->query($createQuery)){
                }
                $this->connection->close();
	}

	/**
	*Update the goal's completion value
	*/
	function UpdateCompletedGoals($userID, $completed, $goalID){
		$updateQuery = 'UPDATE goals SET complete=\''.$completed.'\' WHERE userID=\''.$userID.'\' AND goalID=\''.$goalID.'\'';
                if($result = $this->connection->query($updateQuery)){
                }
                $this->connection->close();
	}

	/**
	*Deletes the goal with the given user ID and goal ID
	*/
	function DeleteGoal($userID, $goalID){
		echo "in Method";
		$query = 'DELETE FROM goals WHERE userID=\''.$userID.'\' AND goalID=\''.$goalID.'\'';

                if($result = $this->connection->query($query)){

                }
                $this->connection->close();
	}

	/**
	*Add a new favorite workout when given userID, name, and description
	*/
	function AddFavoriteWorkout($userID, $name, $description){
		$query = 'INSERT INTO favorite_workouts (userID, name, description) VALUES (\''.$userID.'\', \''.$name.'\', \''.$description.'\')';
                
		if($result = $this->connection->query($query)){
                        
                }
                $this->connection->close();

	}

	function GetFavoriteWorkouts($userID){
                $query = 'SELECT * FROM favorite_workouts WHERE userID=\''.$userID.'\'';

                if($result = $this->connection->query($query)){
                        $arr = array();

                        while($row = $result->fetch_assoc()){
                                array_push($arr, array('name' => $row['name'], 'description' => $row['description']));
                        }
                        echo json_encode($arr);
                }
                $this->connection->close();

        }
	
	/**
	*Delete Friends when given the two user ID's
	*/
	function DeleteFriend($userIDMe, $userIDFriend){
		$query = 'DELETE FROM friends WHERE userIDMe=\''.$userIDMe.'\' AND userIDFriend=\''.$userIDFriend.'\'';
		if($result = $this->connection->query($query)){
		}

		$query2 = 'DELETE FROM friends WHERE userIDMe=\''.$userIDFriend.'\' AND userIDFriend=\''.$userIDMe.'\'';
		if($result = $this->connection->query($query2)){
		}
		$this->connection->close();
	}

	function GetUserByName($firstname){
		$query = 'SELECT * FROM users WHERE firstname=\''.$firstname.'\'';
                if($result = $this->connection->query($query)){
			$arr = array();
			while($row = $result->fetch_assoc()){
				array_push($arr, array('ID'=> $row['ID']));
			}
			echo json_encode($arr);
                }
		$this->connection->close();
	}


}
?>

