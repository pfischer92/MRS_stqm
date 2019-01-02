package ch.fhnw.swc.mrs.data;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
//import org.apache.commons.csv.CSVParser;

import ch.fhnw.swc.mrs.api.MRSServices;
import ch.fhnw.swc.mrs.model.Movie;
import ch.fhnw.swc.mrs.model.PriceCategory;
import ch.fhnw.swc.mrs.model.Rental;
import ch.fhnw.swc.mrs.model.User;

/**
 * A simple implementation of the MRS Services.
 */
public class SimpleMRSServices implements MRSServices {

    private Map<UUID, Movie> movies = new HashMap<>();
    private Map<UUID, User> users = new HashMap<>();
    private Map<UUID, Rental> rentalList = new HashMap<>();

	@Override
	public Movie createMovie(String aTitle, LocalDate aReleaseDate, String aPriceCategory, int anAgeRating) {
		try {
			PriceCategory pc = PriceCategory.getPriceCategoryFromId(aPriceCategory);
			Movie m = new Movie(aTitle, aReleaseDate, pc, anAgeRating);
	        UUID id = UUID.randomUUID();
	        m.setId(id);
	        movies.put(id, m);
			return m;
		} catch (Exception e) {
			return null;
		}
	}    

    @Override
    public Collection<Movie> getAllMovies() {
        return movies.values();
    }

    @Override
    public Collection<Movie> getAllMovies(boolean rented) {
        Collection<Movie> result = new ArrayList<>();
        for (Movie m : movies.values()) {
            if (rented == m.isRented()) {
                result.add(m);
            }
        }
        return result;
    }

    @Override
    public Movie getMovieById(UUID id) {
        return movies.get(id);
    }

    @Override
    public boolean updateMovie(Movie movie) {
        movies.put(movie.getId(), movie);
        return true;
    }

    @Override
    public boolean deleteMovie(UUID id) {
        return movies.remove(id) != null;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(UUID id) {
        return users.get(id);
    }

    @Override
    public User getUserByName(String name) {
        for (User u : users.values()) {
            if (u.getName().equals(name)) {
                return u;
            }
        }
        return null;
    }

	@Override
	public User createUser(String aName, String aFirstName, LocalDate aBirthdate) {
		try {
			User u = new User(aName, aFirstName, aBirthdate);
            UUID id = UUID.randomUUID();
            u.setId(id);
            users.put(id, u);
            return u;			
		} catch (Exception e) {
			return null;
		}
	}

    @Override
    public boolean updateUser(User u) {
    	User user = u;
        users.put(user.getId(), user);
        return true;
    }

    @Override
    public boolean deleteUser(UUID id) {
        return users.remove(id) != null;
    }

    @Override
    public Collection<Rental> getAllRentals() {
        return rentalList.values();
    }

    @Override
    public boolean createRental(UUID userId, UUID movieId, LocalDate d) {
        User u = users.get(userId);
        Movie m = movies.get(movieId);
        
        if (u != null && m != null && !m.isRented() && !d.isAfter(LocalDate.now())) {
            Rental r = new Rental(u, m, d);
            UUID id = UUID.randomUUID();
            r.setId(id);
            rentalList.put(id, r);
            return true;
        }
        return false;
    }

    @Override
    public boolean returnRental(UUID id) {
        Rental r = rentalList.get(id);
        r.getMovie().setRented(false);
        boolean result = r.getUser().getRentals().remove(r);
        rentalList.remove(id);
        return result;
    }

    /**
     * Initialize the "server component".
     */
    public void init() {
        readMovies();
        readUsers();
        readRentals();
    }

    private void readMovies() {
    	InputStream instream = getClass().getResourceAsStream("/data/movies.csv");
        try (Reader in = new InputStreamReader(instream, StandardCharsets.UTF_8)) {
            Iterable<CSVRecord> movieList = CSVFormat.EXCEL.withFirstRecordAsHeader().withHeader(MovieHeaders.class)
                    .withDelimiter(';').parse(in);
            for (CSVRecord m : movieList) {
                UUID id = UUID.fromString(m.get(MovieHeaders.ID));
                String title = m.get(MovieHeaders.Title);
                LocalDate releaseDate = LocalDate.parse(m.get(MovieHeaders.ReleaseDate));
                PriceCategory pc = PriceCategory.getPriceCategoryFromId(m.get(MovieHeaders.PriceCategory));
                boolean isRented = Boolean.parseBoolean(m.get(MovieHeaders.isRented));
                int ageRating = Integer.parseInt(m.get(MovieHeaders.AgeRating));
                Movie movie = new Movie(title, releaseDate, pc, ageRating);
                movie.setId(id);
                movie.setRented(isRented);
                movies.put(id, movie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readUsers() {
    	InputStream instream = getClass().getResourceAsStream("/data/users.csv");
        try (Reader in = new InputStreamReader(instream, StandardCharsets.UTF_8)) {
            Iterable<CSVRecord> usersList = CSVFormat.EXCEL.withFirstRecordAsHeader().withHeader(UserHeaders.class)
                    .withDelimiter(';').parse(in);
            for (CSVRecord u : usersList) {
                UUID id = UUID.fromString(u.get(UserHeaders.ID));
                String surname = u.get(UserHeaders.Surname);
                String firstname = u.get(UserHeaders.FirstName);
                LocalDate birthdate = LocalDate.parse(u.get(UserHeaders.Birthdate));
                User user = new User(surname, firstname, birthdate);
                user.setId(id);
                users.put(id, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readRentals() {
    	InputStream instream = getClass().getResourceAsStream("/data/rentals.csv");
        try (Reader in = new InputStreamReader(instream, StandardCharsets.UTF_8)) {
            Iterable<CSVRecord> rentals = CSVFormat.EXCEL.withFirstRecordAsHeader().withHeader(RentalHeaders.class)
                    .withDelimiter(';').parse(in);
            for (CSVRecord r : rentals) {
                UUID id = UUID.fromString(r.get(RentalHeaders.ID));
                LocalDate rentaldate = LocalDate.parse(r.get(RentalHeaders.RentalDate));
                UUID userId = UUID.fromString(r.get(RentalHeaders.UserID));
                UUID movieId = UUID.fromString(r.get(RentalHeaders.MovieID));
                User u = users.get(userId);
                Movie m = movies.get(movieId);
                Rental rental = new Rental(u, m, rentaldate);
                rental.setId(id);
                rentalList.put(id, rental);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    enum MovieHeaders {
        ID, Title, ReleaseDate, PriceCategory, AgeRating, isRented
    }

    enum UserHeaders {
        ID, Surname, FirstName, Birthdate
    }

    enum RentalHeaders {
        ID, RentalDate, UserID, MovieID
    }

}
