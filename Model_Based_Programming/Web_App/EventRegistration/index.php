<!DOCTYPE HTML>
<html>
	<head>
		<meta charset="UTF-8">
		<title> Event Registration </title>
		<style>
			.error {color: #FF0000;}
		</style>
	</head>
	<body>
		<?php
		
		$timezone = date_default_timezone_set('America/New_York');
		
		require_once __DIR__.'\.\Persistence\PersistenceEventRegistration.php';
		require_once __DIR__.'\.\Model\RegistrationManager.php';
		require_once __DIR__.'\.\Model\Participant.php';
		require_once __DIR__.'\.\Model\Event.php';
		
		session_start();
		
		//Retrieve the data from the model
		$pm = new PersistenceEventRegistration();
		$rm = $pm->loadDataFromStore();
		
		echo"<form action='Register.php' method='post'>";
		
		echo"<p>Name? <select name='participantspinner'>";
		foreach ($rm->getParticipants() as $participant){
			echo"<option>" . $participant->getName() . "</option>";
		}
		echo"</select><span class='error'>";
		if(isset($_SESSION['errorRegisterParticipant']) && !empty($_SESSION['errorRegisterParticipant'])){
			echo " * " . $_SESSION["errorRegisterParticipant"];
		}
		echo "</span></p>";
		
		echo "<p>Event? <select name='eventspinner'>";
		foreach ($rm->getEvents() as $event){
			echo "<option>" . $event->getName() . "</option>";
		}
		echo"</select><span class='error'>";
		if (isset($_SESSION['errorRegisterEvent']) && !empty($_SESSION['errorRegisterEvent'])){
			echo " * " . $_SESSION["errorRegisterEvent"];
		}
		echo "</span></p>";
				
		echo "<p><input type ='submit' value='Register' /></p>";
		
		echo "</form>";
		?>
		
		<form action ="AddParticipant.php" method = "post"> 
			<p>Participant Name? <input type ="text" name="participant_name" />
			<span class ="error">
			<?php
			if (isset($_SESSION['errorParticipantName']) && !empty($_SESSION['errorParticipantName'])){
				echo " * " . $_SESSION["errorParticipantName"];
			}
			?>
			</span></p>
			<p><input type= "submit" value="Add Participant"/></p>
		</form>
		
		<form action="AddEvent.php" method = "post" >
			<p>Event Name? <input type= "text" name = "event_name"/>
			<span class ="error">
			<?php
			if (isset($_SESSION['errorEventName']) && !empty($_SESSION['errorEventName'])){
				echo " * " . $_SESSION["errorEventName"];
			}
			?>
			</span></p>
			
			<p>Date? <input type= "date" name="event_date" value=" <?php echo date('Y-m-d');?>" />
			<span class ="error">
			<?php
			if (isset($_SESSION['errorEventDate']) && !empty($_SESSION['errorEventDate'])){
				echo " * " . $_SESSION["errorEventDate"];
			}
			?>
			</span></p>
			
			<p>Start time? <input type ="time" name="starttime" value="<?php echo date('H:i');?>"/>
			<span class ="error">
			<?php
			if (isset($_SESSION['errorEventStartTime']) && !empty($_SESSION['errorEventStartTime'])){
				echo " * " . $_SESSION["errorEventStartTime"];
			}
			?>
			</span></p>
			
			<p>End time? <input type ="time" name="endtime" value="<?php echo date('H:i');?>"/>
			<span class ="error">
			<?php
			if (isset($_SESSION['errorEventEndTime']) && !empty($_SESSION['errorEventEndTime'])){
				echo " * " . $_SESSION["errorEventEndTime"];
			}
			?>
			</span></p>
			<p><input type= "submit" value="Add Event"/></p>
		
		</form>
	</body>
</html>