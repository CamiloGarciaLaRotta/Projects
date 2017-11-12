package group10.EventRegistration.View;

import java.awt.Color;
import java.awt.HeadlessException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import group10.EventRegistration.Controller.EventRegistrationController;
import group10.EventRegistration.Controller.InvalidInputException;
import group10.EventRegistration.Model.RegistrationManager;

public class ParticipantPage extends JFrame {

	private static final long serialVersionUID = -3813819647258555349L;
	
	private JTextField participantNameTextField;
	private JLabel participantNameLabel;
	private JButton addParticipantButton;	
	
	private String error = null;
	private JLabel errorMessage;
	
	private RegistrationManager rm;

	// Creates new form EventRegistrationPage
	public ParticipantPage(RegistrationManager rm) {
		this.rm = rm;
		initComponents();
	}
	
	// initialize the graphical elements layout
	private void initComponents() {
		// elements for participant
		participantNameTextField = new JTextField();
		participantNameLabel = new JLabel();
		addParticipantButton = new JButton();
		
	    // elements for error message
	    errorMessage = new JLabel();
	    errorMessage.setForeground(Color.RED);
		
		// global settings and listeners
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Event Registration");
		participantNameLabel.setText("Name: ");
		participantNameTextField.setText("e.g. James McGill");
		addParticipantButton.setText("Add Participant");
		
		addParticipantButton.addActionListener(new java.awt.event.ActionListener() {
		    public void actionPerformed(java.awt.event.ActionEvent evt) {
		        addParticipantButtonActionPerformed();
		    }
		});

		
		// layout
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
	    layout.setHorizontalGroup(
	            layout.createParallelGroup()
	            .addComponent(errorMessage)
	            .addGroup(layout.createSequentialGroup()
	            .addComponent(participantNameLabel)
	            .addGroup(layout.createParallelGroup()
	                .addComponent(participantNameTextField, 200, 200, 400)
	                .addComponent(addParticipantButton))
	            ));
		
		layout.linkSize(SwingConstants.HORIZONTAL, 
						new java.awt.Component[] {
								addParticipantButton,
								participantNameTextField
						}
				);
		
	    layout.setVerticalGroup(
	            layout.createSequentialGroup()
	            .addComponent(errorMessage)
	            .addGroup(layout.createParallelGroup()
	                .addComponent(participantNameLabel)
	                .addComponent(participantNameTextField))
	            .addComponent(addParticipantButton)
	            );
		
		pack();
	}
	
	// refresh the view after model change
	public void refreshData() {
		// error check
		errorMessage.setText(error);
		if (error == null || error.length() == 0) {
			// participant
			participantNameTextField.setText("");
		}

		pack();
	}
	
	private void addParticipantButtonActionPerformed() {
		// create and call the controller
		EventRegistrationController erc = new EventRegistrationController(rm);
		error = null;
		try {
			erc.createParticipant(participantNameTextField.getText());
		} catch(InvalidInputException e) {
			error = e.getMessage();
		}
		
		// update visuals
		refreshData();
	}
}
