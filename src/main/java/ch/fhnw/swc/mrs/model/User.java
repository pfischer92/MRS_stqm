package ch.fhnw.swc.mrs.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import ch.fhnw.swc.mrs.api.MovieRentalException;

/**
 * Represents the client of a movie store.
 * 
 */
public class User {

	/** Maximum number of films a user may rent. */
	public static final int MAX_RENTABLE_MOVIES = 3;
	/** Maximum name length. */
	public static final int MAX_NAME_LENGTH = 40;
	/** Exception text: illegal date of birth used. */
	public static final String ILLEGAL_BIRTHDATE = "illegal date of birth";
	/** Exception text: invalid name. */
	public static final String ILLEGAL_NAME = "invalid name value";
	/** Exception text: id of this User has not been set. */
	public static final String NO_ID = "id of this User has not been set";
	/** Exception text: missing name (null value). */
	public static final String MISSING_NAME = "missing name (null value)";
	/** Exception text: illegal change of user's id. */
	public static final String ILLEGAL_ID_CHANGE = "illegal change of user's id";

    /** Flag indicating whether object has been initialized. */
    private boolean initialized = false;

    /** Unique identification for this user object. */
    private UUID id;
    /** The user's family name. */
    private String name = "Unnamed";
    /** The user's first name. */
    private String firstname = "Unnamed";
    /** The user's date of birth is used to check age ratings. */
    private LocalDate birthdate;

    /**
     * A list of rentals of the user.
     */
    private List<Rental> rentals = new LinkedList<Rental>();

    /**
     * Create a new user with the given name information.
     * 
     * @param aName the user's family name.
     * @param aFirstName the user's first name.
     * @param aBirthdate must not be null or in the future.
     * @throws IllegalArgumentException The name must neither be <code>null</code>.
     * @throws MovieRentalException If the name is empty ("") or longer than MAX_NAME_LENGTH characters.
     */
    public User(String aName, String aFirstName, LocalDate aBirthdate) {
    	initializeUser(aName, aFirstName, aBirthdate);
    }
    
    public User(UUID anID, String aName, String aFirstName, LocalDate aBirthdate) {
    	initializeUser(aName, aFirstName, aBirthdate);
    	setId(anID);
    }

    /**
     * Create a new user with the given name information.
     * 
     * @param aName the user's family name.
     * @param aFirstName the user's first name.
     * @param aBirthdate must not be null or in the future.
     * @throws IllegalArgumentException The name must neither be <code>null</code>.
     * @throws MovieRentalException If the name is empty ("") or longer than MAX_NAME_LENGTH characters.
     */
    public User(String aName, String aFirstName, String aBirthdate) {
    	LocalDate birthDate = LocalDate.parse(aBirthdate, DateTimeFormatter.ISO_DATE);
    	initializeUser(aName, aFirstName, birthDate);
    }
    
    private void initializeUser(String aName, String aFirstName, LocalDate aBirthdate) {
        setName(aName);
        setFirstName(aFirstName);
        setBirthdate(aBirthdate);    	
    }

    /**
     * Checks if date of birth is valid.
     * 
     * @param aBirthdate must not be null or in the future.
     */
    private void checkBirthdate(LocalDate aBirthdate) {
        LocalDate now = LocalDate.now();
        if (now.isBefore(aBirthdate) || now.minusYears(120).isAfter(aBirthdate)) {
            throw new IllegalArgumentException(ILLEGAL_BIRTHDATE);
        }
    }

    /**
     * Checks if name is valid.
     * 
     * @param aName the name of the user.
     */
    private void checkName(String aName) {
        if (aName != null) {
            if ((aName.length() == 0) || (aName.length() > MAX_NAME_LENGTH)) {
                throw new MovieRentalException(ILLEGAL_NAME);
            }
        } else {
            throw new IllegalArgumentException(MISSING_NAME);
        }
    }

    /**
     * @return The user's unique identification number.
     * @throws IllegalStateException when trying to retrieve id before it was set.
     */
    public UUID getId() {
        if (initialized) {
            return id;
        } else {
            throw new IllegalStateException(NO_ID);
        }
    }

    /**
     * @param anID set the user's unique identification number.
     * @throws IllegalStateException when trying to re-set id.
     */
    public void setId(UUID anID) {
        if (initialized) {
            throw new IllegalStateException(ILLEGAL_ID_CHANGE);
        } else {
            initialized = true;
            id = anID;
        }
    }

    /**
     * @return get a list of the user's rentals.
     */
    public List<Rental> getRentals() {
        return rentals;
    }

    /**
     * @param someRentals set the user's rentals.
     */
    public void setRentals(List<Rental> someRentals) {
        this.rentals = someRentals;
    }

    /**
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param aName set the user's family name.
     * @throws NullPointerException The name must neither be <code>null</code>.
     * @throws MovieRentalException If the name is emtpy ("") or longer than 40 characters.
     */
    public void setName(String aName) {
        checkName(aName);
        name = aName;
    }

    /**
     * @return get the user's first name.
     */
    public String getFirstName() {
        return firstname;
    }

    /**
     * @param aFirstName set the user's family name.
     * @throws NullPointerException The first name must not be <code>null</code>.
     * @throws MovieRentalException If the name is emtpy ("") or longer than 40 characters.
     */
    public void setFirstName(String aFirstName) {
        checkName(aFirstName);
        firstname = aFirstName;
    }

    /**
     * @return user's birth date.
     */
    public LocalDate getBirthdate() {
        return birthdate;
    }

    /**
     * Set a users date of birth.
     * 
     * @param aBirthdate must not be in the future.
     */
    public void setBirthdate(LocalDate aBirthdate) {
        checkBirthdate(aBirthdate);
        birthdate = aBirthdate;
    }

    /**
     * Calculate the total charge the user has to pay for all his/her rentals.
     * 
     * @return the total charge.
     */
    public double getCharge() {
        double result = 0.0d;
        for (Rental rental : rentals) {
            result += rental.getMovie().getPriceCategory().getCharge(rental.getRentalDays());
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        boolean result = this == o;
        if (!result) {
            if (o instanceof User) {
                User other = (User) o;
                result = getId() == other.getId();
                result &= getName().equals(other.getName());
                result &= getFirstName().equals(other.getFirstName());
                result &= getBirthdate().equals(other.getBirthdate());
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = (initialized) ? getId().hashCode() : 0;
        result = 19 * result + getName().hashCode();
        result = 19 * result + getFirstName().hashCode();
        return result;
    }

    /**
     * check if user has rentals.
     * 
     * @return true if found
     */
    public boolean hasRentals() {
        return !rentals.isEmpty();
    }

    /**
     * add a new rental to the user.
     * 
     * @param rental the rental
     * @return number of rentals of the user
     */
    public int addRental(Rental rental) {
        rentals.add(rental);
        return rentals.size();
    }
}
