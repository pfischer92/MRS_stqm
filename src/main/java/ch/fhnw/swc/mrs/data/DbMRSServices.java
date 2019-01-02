package ch.fhnw.swc.mrs.data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.sql2o.Sql2o;

import ch.fhnw.swc.mrs.api.MRSServices;
import ch.fhnw.swc.mrs.model.Movie;
import ch.fhnw.swc.mrs.model.PriceCategory;
import ch.fhnw.swc.mrs.model.Rental;
import ch.fhnw.swc.mrs.model.User;

public class DbMRSServices implements MRSServices {
//	the connection string to the in memory database
	private static final String DB_CONNECTION = "jdbc:postgresql://localhost:4567/mrs";

	// the connection string to the database 
//    private static final String DB_CONNECTION = "jdbc:hsqldb:hsql://localhost/mrs";
	static private Sql2o sql2o = new Sql2o(DB_CONNECTION, "mrs", "mrs");

	
	private MovieDAO getMovieDAO() { 
		return new MovieDAO(sql2o);
	}

	private UserDAO getUserDAO() { 
		return new UserDAO(sql2o);
	}

	private RentalDAO getRentalDAO() { 
		return new RentalDAO(sql2o);
	}

	@Override
	public Movie createMovie(String aTitle, LocalDate aReleaseDate, String aPriceCategory, int anAgeRating) {
        try {
			PriceCategory pc = PriceCategory.getPriceCategoryFromId(aPriceCategory);
			Movie m = new Movie(aTitle, aReleaseDate, pc, anAgeRating);
            getMovieDAO().saveOrUpdate(m);
            return m;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}

	@Override
	public List<Movie> getAllMovies() {
		return getMovieDAO().getAll();
	}

	@Override
	public List<Movie> getAllMovies(boolean rented) {
        return getMovieDAO().getAll(rented);
	}

	@Override
	public Movie getMovieById(UUID id) {
	    return getMovieDAO().getById(id);
	}

	@Override
	public boolean updateMovie(Movie movie) {
	    try {
	        getMovieDAO().saveOrUpdate(movie);
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	@Override
	public boolean deleteMovie(UUID id) {
        try {
            getMovieDAO().delete(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
	}

	@Override
	public List<User> getAllUsers() {
	    return getUserDAO().getAll();
	}

	@Override
	public User getUserById(UUID id) {
	    return getUserDAO().getById(id);
	}

	@Override
	public User getUserByName(String name) {
	    List<User> users = getUserDAO().getByName(name);
		return users.size() == 0 ? null : users.get(0);
	}

	@Override
	public User createUser(String aName, String aFirstName, LocalDate aBirthdate) {
	    try {
			User u = new User(aName, aFirstName, aBirthdate);
	        getUserDAO().saveOrUpdate(u);
	        return u;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	@Override
	public boolean updateUser(User user) {
        try {
            getUserDAO().saveOrUpdate(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
	}

	@Override
	public boolean deleteUser(UUID id) {
        try {
            getUserDAO().delete(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
	}

	@Override
	public List<Rental> getAllRentals() {
		return getRentalDAO().getAll();
	}

	@Override
	public boolean createRental(UUID userId, UUID movieId, LocalDate d) {
		// TODO: transaction is missing
        User u = getUserDAO().getById(userId);
        Movie m = getMovieDAO().getById(movieId);

        if (u != null && m != null && !m.isRented() && !d.isAfter(LocalDate.now())) {
	        getRentalDAO().create(userId, movieId, d);
	        m.setRented(true);
	        getMovieDAO().saveOrUpdate(m);
	        return true;
	    }
	    return false;
	}

	@Override
	public boolean returnRental(UUID id) {
      	RentalDAO rdao = getRentalDAO();
      	Rental r = rdao.getById(id);
        Movie m = r.getMovie();
        m.setRented(false);
        getMovieDAO().saveOrUpdate(m);
        rdao.delete(id);
        return r != null;
	}
	
	@Override
	public void init() {
	    try {
 //           Database db = new HsqlDatabase();
 //           db.initDB(DB_CONNECTION);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
