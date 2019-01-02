package ch.fhnw.swc.mrs.view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import ch.fhnw.swc.mrs.model.Movie;
import ch.fhnw.swc.mrs.model.PriceCategory;
import ch.fhnw.swc.mrs.model.Rental;
import ch.fhnw.swc.mrs.model.User;

public final class Conversions {
    static MovieDTO convert(Movie m) {
        UUID id = m.getId();
        boolean isRented = m.isRented();
        int ageRating = m.getAgeRating(); // TODO replace with value from backend.
        String title = m.getTitle();
        LocalDate releaseDate = m.getReleaseDate();
        String priceCategory = m.getPriceCategory().toString();
        return new MovieDTO(id, isRented, ageRating, title, releaseDate, priceCategory);
    }

    static Movie convert(MovieDTO movie) {
        UUID id = UUID.fromString(movie.id.get());
        boolean isRented = movie.rented.get();
        int ageRating = movie.ageRating.get();
        String title = movie.title.get();
        LocalDate releaseDate = movie.releaseDate.get();
        PriceCategory pc = PriceCategory.getPriceCategoryFromId(movie.priceCategory.get());
        Movie m = new Movie(title, releaseDate, pc, ageRating);
        m.setId(id);
        m.setRented(isRented);
        m.setAgeRating(ageRating);
        return m;
    }

    static Collection<MovieDTO> convertMovieList(Collection<Movie> movies) {
        Collection<MovieDTO> movs = new ArrayList<>(movies.size());
        for (Movie m : movies) {
            movs.add(convert(m));
        }
        return movs;
    }

    static User convert(UserDTO user) {
        UUID id = UUID.fromString(user.id.get());
        String name = user.name.get();
        String firstName = user.firstName.get();
        LocalDate birthdate = user.birthdate.get();
        User u = new User(name, firstName, birthdate);
        u.setId(id);
        return u;
    }

    static UserDTO convert(User u) {
        UUID id = u.getId();
        String name = u.getName();
        String firstName = u.getFirstName();
        LocalDate birthdate = u.getBirthdate();
        return new UserDTO(id, name, firstName, birthdate);
    }

    static Collection<UserDTO> convertUserList(Collection<User> users) {
        Collection<UserDTO> us = new ArrayList<>(users.size());
        for (User u : users) {
            us.add(convert(u));
        }
        return us;
    }

    static RentalDTO convert(Rental r) {
        UUID id = r.getId();
        String name = r.getUser().getName();
        String firstname = r.getUser().getFirstName();
        String title = r.getMovie().getTitle();
        LocalDate rentaldate = r.getRentalDate();
        int rentaldays = (int) r.getRentalDays();
        double fee = r.getRentalFee();
        return new RentalDTO(id, name, firstname, title, rentaldate, rentaldays, fee);
    }

    static Collection<RentalDTO> convertRentalList(Collection<Rental> rentals) {
        Collection<RentalDTO> rtls = new ArrayList<>(rentals.size());
        for (Rental r : rentals) {
            rtls.add(convert(r));
        }
        return rtls;
    }
    
    // prevent instantiation
    private Conversions() { }
}
