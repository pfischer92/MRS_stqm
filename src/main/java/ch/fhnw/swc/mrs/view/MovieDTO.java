package ch.fhnw.swc.mrs.view;

import java.time.LocalDate;
import java.util.UUID;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MovieDTO {
	/** Unique identifier for this movie. */
	final StringProperty id;
	
	/** Indicates whether this movie is rented. */
	final BooleanProperty rented;
	
	/** This movies title. */
	final StringProperty title;
	
	/** The release date of this movie. */
	final ObjectProperty<LocalDate> releaseDate;
	
	/** The minimum age to be allowed to rent this movie. */
	final IntegerProperty ageRating;
	
	/** The price category of this movie. */
	final StringProperty priceCategory;
	
	/**
	 * @param anId
	 *            unique identification of movie.
	 * @param isRented
	 *            whether this movie is rented.
	 * @param anAgeRating
	 *            the minimum age to rent this movie.
	 * @param aTitle
	 *            the movie's title
	 * @param aReleaseDate
	 *            the movie's release date.
	 * @param aPriceCategory
	 *            the price category for renting this movie.
	 */
	public MovieDTO(UUID anId, boolean isRented, int anAgeRating, String aTitle, LocalDate aReleaseDate,
			String aPriceCategory) {
		id = new SimpleStringProperty(anId.toString());
		rented = new SimpleBooleanProperty(isRented);
		ageRating = new SimpleIntegerProperty(anAgeRating);
		title = new SimpleStringProperty(aTitle);
		releaseDate = new SimpleObjectProperty<LocalDate>(aReleaseDate);
		priceCategory = new SimpleStringProperty(aPriceCategory);
	}

}
