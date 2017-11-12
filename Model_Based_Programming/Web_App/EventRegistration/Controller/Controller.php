<?php
require_once __DIR__.'\..\Controller\InputValidator.php';
require_once __DIR__.'\..\Persistence\PersistenceEventRegistration.php';
require_once __DIR__.'\..\Model\RegistrationManager.php';
require_once __DIR__.'\..\Model\Participant.php';
require_once __DIR__.'\..\Model\Event.php';
require_once __DIR__.'\..\Model\Registration.php';


class Controller{
	public function __construct(){
	}
	public function createParticipant($participant_name){
		//1. Validate input
		$name = InputValidator::validate_input($participant_name);
		if($name==null || strlen($name) == 0){
			throw new Exception("Participant name cannot be empty!");
		} else {
			//2. Load all of the data
			$pm = new PersistenceEventRegistration();
			$rm = $pm->loadDataFromStore();
			
			//3. Add the new particpant
			$participant = new Participant($name);
			$rm->addParticipant($participant);
			
			//4. Write all the data
			$pm->writeDataToStore($rm);
		}
	}
	public function createEvent($event_name, $event_date, $starttime, $endtime){
		// 1. Validate input
		$name = InputValidator::validate_input ( $event_name );
		$event_date = InputValidator::validate_date( $event_date );
		$event_date = date('Y-m-d', strtotime($event_date));
		
		$error = "";
		if ($name == null || strlen ( $name ) == 0) {
			$error .= ("@1Event name cannot be empty! ");
		}
		if (strlen ( trim($event_date) ) == 0 || ! strtotime($event_date)) {
			$error .= ("@2Event date must be specified correctly (YYYY-MM-DD)! ");
		}
		if ($starttime == null || strlen ( trim($starttime) ) == 0 || ! strtotime ( $starttime )) {
			$error .= ("@3Event start time must be specified correctly (HH:MM)! ");
		}
		if ($endtime == null || strlen (trim($endtime) ) == 0 || ! strtotime ( $endtime )) {
			$error .= ("@4Event end time must be specified correctly (HH:MM)!");
		}
		
		if(($starttime != null && $endtime != null) && (strlen($starttime) > 0 && strlen($endtime) > 0) && strtotime($starttime) > strtotime($endtime)) {
			$error .= ("@5Event end time cannot be before event start time!");
		}
		
		
		if (strlen ( $error ) > 0) {
			throw new Exception ( $error );
		} else {
			// 2. Load all of the data
			$pm = new PersistenceEventRegistration ();
			$rm = $pm->loadDataFromStore ();
				
			// 3. Add the event
			$event = new Event ( $name, $event_date, $starttime, $endtime );
			$rm->addEvent ( $event );
				
			// 4. Write all of the data
			$pm->writeDataToStore ( $rm );
		}
	}
	
	public function register($aParticipant, $aEvent){
		//1.Load all of the data
		$pm = new PersistenceEventRegistration();
		$rm = $pm->loadDataFromStore();
		
		//2. Find the participant
		$myparticipant = NULL;
		foreach ($rm->getParticipants() as $participant){
			if(strcmp($participant->getName(), $aParticipant)==0){
				$myparticipant = $participant;
				break;
			}
		}
		//3. Find the event
		$myevent = NULL;
		foreach ($rm->getEvents() as $event){
			if(strcmp($event->getName(), $aEvent) ==0){
				$myevent = $event;
				break;
			}
		}
	
		//4. Register for the event
		$error = "";
		if ($myparticipant != NULL && $myevent != NULL){
			$myregistration = new Registration($myparticipant, $myevent);
			$rm->addRegistration($myregistration);
			$pm->writeDataToStore($rm);
		} else {
			if($myparticipant == NULL){
				$error .= "@1Participant ";
				if($aParticipant != NULL){
					$error .= $aParticipant;
				}
				$error .= " not found! ";
			}
			if ($myevent == NULL){
				$error .= "@2Event ";
				if ($aEvent != NULL){
					$error .= $aEvent;
				}
				$error .= " not found!";
			}
			throw new Exception(trim($error));
		}
	}
}

?>