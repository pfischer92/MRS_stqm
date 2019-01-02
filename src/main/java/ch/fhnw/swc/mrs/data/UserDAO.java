package ch.fhnw.swc.mrs.data;

import java.util.List;
import java.util.UUID;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import ch.fhnw.swc.mrs.model.User;

/**
 * Data Access Object that provides access to the underlying database. Use this
 * DAO to access User related data.
 */
public class UserDAO {

	/** SQL statement to delete user. */
	private static final String DELETE_SQL = "DELETE FROM clients WHERE id = :id";
	/** SQL statement to create user. */
	private static final String INSERT_SQL 
		= "INSERT INTO clients ( id, firstname, name, birthdate ) VALUES ( :id, :firstname, :name, :birthdate )";
	/** SQL statement to update user. */
	private static final String UPDATE_SQL 
		= "UPDATE clients SET firstname = :firstname, name = :name, birthdate = :birthdate WHERE id = :id";
	/** SQL statement to get user by id. */
	private static final String GET_BY_ID_SQL 
	= "SELECT id, firstname, name, birthdate FROM clients WHERE id = :id";
	/** SQL statement to get user by name. */
	private static final String GET_BY_NAME_SQL 
		= "SELECT id, firstname, name, birthdate FROM clients WHERE name = :name";
	/** SQL statement to get all users. */
	private static final String GET_ALL_SQL 
//	= "SELECT id, firstname, name, birthdate FROM clients";
	= "SELECT * FROM clients";

	private Sql2o sql2o;

	public UserDAO(Sql2o sql2o) {
		this.sql2o = sql2o;
	}

	/**
	 * Retrieve a user by his/her identification.
	 * 
	 * @param id
	 *            the unique identification of the user object to retrieve.
	 * @return the user with the given identification or <code>null</code> if none
	 *         found.
	 */
	public User getById(UUID id) {
		try (Connection conn = sql2o.open()) {
			return conn.createQuery(GET_BY_ID_SQL).addParameter("id", id).executeAndFetchFirst(User.class);
		}
	}

	/**
	 * Retrieve all users stored in this system.
	 * 
	 * @return a list of all users.
	 */
	public List<User> getAll() {
		try (Connection conn = sql2o.open()) {
			return conn.createQuery(GET_ALL_SQL)
					.executeAndFetch(User.class);
		}
	}

	/**
	 * Persist a User object. Use this method either when storing a new User object
	 * or for updating an existing one.
	 * 
	 * @param user
	 *            the object to persist.
	 */
	public void saveOrUpdate(User user) {
		try (Connection conn = sql2o.beginTransaction()) {
			User u = conn.createQuery(GET_BY_ID_SQL)
					.addParameter("id", user.getId())
					.executeAndFetchFirst(User.class);
			String sql = (u == null) ? INSERT_SQL : UPDATE_SQL;
			conn.createQuery(sql)
				.addParameter("id", user.getId())
				.addParameter("firstname", user.getFirstName())
				.addParameter("name", user.getName())
				.addParameter("birthdate", user.getBirthdate())
				.executeUpdate();
			conn.commit();
		}
	}

	/**
	 * Remove a user from the database. After this operation the user does not exist
	 * any more in the database. Make sure to dispose the object too!
	 * 
	 * @param id
	 *            the User to remove.
	 */
	public void delete(UUID id) {
		try (Connection conn = sql2o.open()) {
			conn.createQuery(DELETE_SQL).addParameter("id", id).executeUpdate();
		}
	}

	/**
	 * Retrieve a user by his/her name. Use the family name to retrieve a list of
	 * all users with that name. Note this method does not support wildcards!
	 * 
	 * @param name
	 *            the family name of the users to retrieve.
	 * @return a list of users with the given name.
	 */
	public List<User> getByName(String name) {
		try (Connection conn = sql2o.open()) {
			return conn.createQuery(GET_BY_NAME_SQL)
					.addParameter("name", name).executeAndFetch(User.class);
		}
	};
}
