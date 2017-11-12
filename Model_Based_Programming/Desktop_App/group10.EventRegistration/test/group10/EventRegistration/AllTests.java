package group10.EventRegistration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import group10.EventRegistration.Controller.TestEventRegistrationController;
import group10.EventRegistration.Persistence.TestPersistence;

@RunWith(Suite.class)
@SuiteClasses({ TestEventRegistrationController.class, TestPersistence.class})
public class AllTests {
}
