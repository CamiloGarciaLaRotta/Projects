package group10.EventRegistration.Persistence;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import group10.EventRegistration.Model.Event;
import group10.EventRegistration.Model.Participant;
import group10.EventRegistration.Model.Registration;
import group10.EventRegistration.Model.RegistrationManager;

public class TestPersistence {
	
	private RegistrationManager rm;
	
	@Before
	public void setUp() throws Exception {
		//// create all domain objects
		
		rm = new RegistrationManager();
		
		Participant p1 = new Participant("Conte");
		Participant p2 = new Participant("Costa");
		
		Calendar c = Calendar.getInstance();
		c.set(2017, Calendar.JANUARY, 15,8,30,0);
		Date eventDate = new Date(c.getTimeInMillis());
		Time startTime = new Time(c.getTimeInMillis());
		c.set(2017, Calendar.JANUARY, 15,10,30,0);
		Time endTime = new Time(c.getTimeInMillis());
		Event e = new Event("Chelsea", eventDate, startTime, endTime);
		
		Registration r1 = new Registration(p1,e);
		Registration r2 = new Registration(p2,e);
		
		// manage registrations
		rm.addParticipant(p1);
		rm.addParticipant(p2);
		rm.addEvent(e);
		rm.addRegistration(r1);
		rm.addRegistration(r2);
	}

	@After
	public void tearDown() throws Exception {
		rm.delete();
	}

	@Test
	public void test() {
			  
		// initialize model file
	  	PersistenceXStream.initializeModelManager("output"+File.separator+"test.xml");
	    // save model that is loaded during test setup
	    if (!PersistenceXStream.saveToXMLwithXStream(rm))
	        fail("Could not save file.");
		
	    //// clear the model in memory
		rm.delete();
		assertEquals(0, rm.getParticipants().size());
		assertEquals(0, rm.getEvents().size());
		assertEquals(0, rm.getRegistrations().size());
		
		//// load the model
		rm = (RegistrationManager) PersistenceXStream.loadFromXMLwithXStream();
		if(rm == null) fail("Couldn't load file");
		
	    // check participants
	    assertEquals(2, rm.getParticipants().size());
	    assertEquals("Conte", rm.getParticipant(0).getName());
	    assertEquals("Costa", rm.getParticipant(1).getName());
	    
	    // check event
	    assertEquals(1, rm.getEvents().size());
	    assertEquals("Chelsea", rm.getEvent(0).getName());
	    Calendar c = Calendar.getInstance();
	    c.set(2017,Calendar.JANUARY,15,8,30,0);
	    Date eventDate = new Date(c.getTimeInMillis());
	    Time startTime = new Time(c.getTimeInMillis());
	    c.set(2017,Calendar.JANUARY,15,10,30,0);
	    Time endTime = new Time(c.getTimeInMillis());
	    assertEquals(eventDate.toString(), rm.getEvent(0).getEventDate().toString());
	    assertEquals(startTime.toString(), rm.getEvent(0).getStartTime().toString());
	    assertEquals(endTime.toString(), rm.getEvent(0).getEndTime().toString());
	    
	    // check registrations
	    assertEquals(2, rm.getRegistrations().size());
	    assertEquals(rm.getEvent(0), rm.getRegistration(0).getEvent());
	    assertEquals(rm.getParticipant(0), rm.getRegistration(0).getParticipant());
	    assertEquals(rm.getEvent(0), rm.getRegistration(1).getEvent());
	    assertEquals(rm.getParticipant(1), rm.getRegistration(1).getParticipant());
	    
	}
}
