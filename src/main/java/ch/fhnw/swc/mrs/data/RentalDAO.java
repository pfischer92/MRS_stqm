package ch.fhnw.swc.mrs.data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import ch.fhnw.swc.mrs.model.Rental;
import ch.fhnw.swc.mrs.model.User;

/**
 * The Rental data object model class.
 * 
 */
public class RentalDAO {

    /** SQL statement to delete rental. */
    private static final String DELETE_SQL = "DELETE FROM rentals WHERE id = :id";
    /** SQL statement to create rental. */
    private static final String INSERT_SQL = "INSERT INTO rentals ( id, movieid, clientid, rentaldate )"
            + "  VALUES ( :id, :movieid, :clientid, :rentaldate )";
    /** select clause of queries. */
    private static final String SELECT_CLAUSE = "SELECT id, movieid, clientid, rentaldate FROM rentals ";
    /** SQL statement to get rental by id. */
    private static final String GET_BY_ID_SQL = SELECT_CLAUSE + " WHERE id = :id";
    /** SQL statement to get all rentals from a user. */
    private static final String GET_BY_USER_SQL = SELECT_CLAUSE + " WHERE clientid = :clientid";
    /** SQL statement to get all rentals. */
    private static final String GET_ALL_SQL = SELECT_CLAUSE;

	private Sql2o sql2o;

	public RentalDAO(Sql2o sql2o) {
		this.sql2o = sql2o;
	}

	/**
	 * Retrieve a rental by its identification.
	 * 
	 * @param id
	 *            the unique identification of the rental object to retrieve.
	 * @return the rental with the given identification or <code>null</code> if none
	 *         found.
	 */
	public Rental getById(UUID id) {
		try (Connection conn = sql2o.open()) {
			return conn.createQuery(GET_BY_ID_SQL).addParameter("id", id).executeAndFetchFirst(Rental.class);
		}
    }

	/**
	 * Retrieve all rentals stored in this system.
	 * 
	 * @return a list of all rentals.
	 */
	public List<Rental> getAll() {
		try (Connection conn = sql2o.open()) {
			return conn.createQuery(GET_ALL_SQL).executeScalarList(Rental.class);
		}
    }
    
    /**
     * Retrieve all rentals of a user.
     * @param user to retrieve rentals from.
     * @return all rentals of this user, possibly empty list.
     */
    public List<Rental> getRentalsByUser(User user) {
		try (Connection conn = sql2o.open()) {
			return conn.createQuery(GET_BY_USER_SQL).executeScalarList(Rental.class);
		}
    	
    }

    /**
     * @param rental none.
     */
    public void create(UUID userid, UUID movieid, LocalDate rentalDate) {
		try (Connection conn = sql2o.open()) {
			conn.createQuery(INSERT_SQL)
			.addParameter("id", UUID.randomUUID())
			.addParameter("movieid", movieid)
			.addParameter("clientid", userid)
			.addParameter("rentaldate", rentalDate)
			.executeUpdate();
		}    	
    }

	/**
	 * Remove a rental from the database. 
	 * 
	 * @param id
	 *            the Rental to remove.
	 */
	public void delete(UUID id) {
		try (Connection conn = sql2o.open()) {
			conn.createQuery(DELETE_SQL).addParameter("id", id).executeUpdate();
		}
    }
}
