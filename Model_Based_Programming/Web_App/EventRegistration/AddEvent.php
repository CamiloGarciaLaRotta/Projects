<?php
require_once __DIR__.'\.\Controller\Controller.php';

session_start();
$c =new Controller();
try {
	
	$_SESSION['errorEventDate'] = "";
	$_SESSION["errorEventName"] = "";
	$_SESSION["errorEventStartTime"] = "";
	$_SESSION["errorEventEndTime"] = "";
	$c->createEvent($_POST['event_name'],$_POST['event_date'], $_POST['starttime'], $_POST['endtime']);
	
			
}
catch (Exception $e) {
	$errors = explode("@", $e->getMessage());
	foreach($errors as $error) {
		if (substr($error,0,1) == "1") {
			$_SESSION["errorEventName"] = substr($error, 1);
		}
		if(substr($error,0,1)=="2"){
			$_SESSION["errorEventDate"] = substr($error, 1);
		}
		if(substr($error,0,1)=="3"){
			$_SESSION["errorEventStartTime"] = substr($error, 1);
		}
		if (substr($error,0,1) == "5") {
			$_SESSION["errorEventEndTime"] = substr($error, 1);
		}
	}
}
?>

<!
DOCTYPE html>
<html>
	<head>
		<meta http-equiv = "refresh" content = "0; url=/EventRegistration/"/>
	</head>
</html>