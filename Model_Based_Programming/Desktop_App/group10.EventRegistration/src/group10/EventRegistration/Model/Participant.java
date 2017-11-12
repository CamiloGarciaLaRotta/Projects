/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.24.0-dab6b48 modeling language!*/

package group10.EventRegistration.Model;

// line 3 "../../../group10.EventRegistration.ump"
public class Participant
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Participant Attributes
  private String name;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Participant(String aName)
  {
    name = aName;
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setName(String aName)
  {
    boolean wasSet = false;
    name = aName;
    wasSet = true;
    return wasSet;
  }

  public String getName()
  {
    return name;
  }

  public void delete()
  {}


  public String toString()
  {
    String outputString = "";
    return super.toString() + "["+
            "name" + ":" + getName()+ "]"
     + outputString;
  }
}