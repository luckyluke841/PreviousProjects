<?php

class ConnectionInfo
{
	public $mServerName;
	public $mConnectionInfo;
	public $conn;
	
	public function GetConnection()
	{
		$this->mServerName = 'http://proj-309-ab-5.cs.iastate.edu';
		$this->mConnectionInfo = array("Database"=>"dbu309ab5", "UID"=>"dbu309ab5", "PWD"=>"MWRlYjA5NWYz");
		$this->conn = sqlsrv_connect($this->mServerName,$this->mConnectionInfo);
		
		return $this->conn;
	}
}
?>