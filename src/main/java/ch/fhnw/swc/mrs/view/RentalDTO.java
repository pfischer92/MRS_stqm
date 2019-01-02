package ch.fhnw.swc.mrs.view;

import java.time.LocalDate;
import java.util.UUID;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class RentalDTO {
	/** Unique identifier of this rental. */
	final ReadOnlyStringProperty id;
	/** The title of the movie that was rented. */
	final ReadOnlyStringProperty title;
	/** The renter's name. */
	final ReadOnlyStringProperty userName;
	/** The renter's first name. */
	final ReadOnlyStringProperty userFirstName;
	/** The date this rental started. */
	final ReadOnlyObjectProperty<LocalDate> rentalDate;
	/** The duration of this rental in days. */
	final ReadOnlyIntegerProperty rentalDays;
	/** The fee due for this rental. */
	final ReadOnlyDoubleProperty rentalFee;

	/**
	 * Constructs a rental of a movie to a user at a given date for a certain number of days.
	 * 
	 * @param anId unique identifier of this rental.
	 * @param aName name of user who is renting aMovie.
	 * @param aFirstName first name of user who is renting aMovie.
	 * @param aTitle title of movie that is rented.
	 * @param aRentalDate date of start of this rental.
	 * @param rentaldays duration of this rental.
	 * @param aFee rental fee.
	 */
	public RentalDTO(UUID anId, String aName, String aFirstName, 
	                String aTitle, LocalDate aRentalDate, 
	                int rentaldays, double aFee) {
	    id = new SimpleStringProperty(anId.toString());
	    title = new SimpleStringProperty(aTitle);
	    userName = new SimpleStringProperty(aName);
	    userFirstName = new SimpleStringProperty(aFirstName);
	    rentalDate = new SimpleObjectProperty<>(aRentalDate);
	    rentalDays = new SimpleIntegerProperty(rentaldays);
	    rentalFee = new SimpleDoubleProperty(aFee);
	}

}
