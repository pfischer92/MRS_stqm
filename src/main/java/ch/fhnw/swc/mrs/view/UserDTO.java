package ch.fhnw.swc.mrs.view;

import java.time.LocalDate;
import java.util.UUID;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserDTO {

    /** An identification number unique to each user. */
    final StringProperty id;

    /** The user's family name. */
    final StringProperty name;

    /** The user's first name. */
    final StringProperty firstName;

    /** The user's date of birth is used to check age ratings. */
    final ObjectProperty<LocalDate> birthdate;

    /**
     * Create a new user with the given name information.
     * 
     * @param anId unique user identification.
     * @param aName the user's family name.
     * @param aFirstName the user's first name.
     * @param aBirthdate the user's date of birth.
     */
    public UserDTO(UUID anId, String aName, String aFirstName, LocalDate aBirthdate) {
        id = new SimpleStringProperty(anId.toString());
        name = new SimpleStringProperty(aName);
        firstName = new SimpleStringProperty(aFirstName);
        birthdate = new SimpleObjectProperty<LocalDate>(aBirthdate);
    }

}
