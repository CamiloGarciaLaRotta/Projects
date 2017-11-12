package group10.EventRegistration.Application;

import group10.EventRegistration.Model.RegistrationManager;
import group10.EventRegistration.Persistence.PersistenceXStream;
import group10.EventRegistration.View.EventRegistrationPage;
import group10.EventRegistration.View.ParticipantPage;

public class EventRegistration {
	
	private static String fileName = "output/eventRegistration.xml";
	
	public static void main(String[] args) {
	    final RegistrationManager rm = PersistenceXStream.initializeModelManager(fileName);

	    // start UI
	    java.awt.EventQueue.invokeLater(new Runnable() {
	        public void run() {
	            new EventRegistrationPage(rm).setVisible(true);
	        }
	    });

	}
}