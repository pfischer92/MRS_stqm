package ch.fhnw.swc.mrs.view;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.fhnw.swc.mrs.MovieRentalSystem;
import ch.fhnw.swc.mrs.model.Movie;
import ch.fhnw.swc.mrs.model.NewReleasePriceCategory;
import ch.fhnw.swc.mrs.model.PriceCategory;
import ch.fhnw.swc.mrs.model.Rental;
import ch.fhnw.swc.mrs.model.User;
import ch.fhnw.swc.mrs.view.Conversions;

class ConversionsTest {
	
	@BeforeAll
    private static void loadPriceCategories() throws Exception {
		URI uri = MovieRentalSystem.class.getClassLoader().getResource("data/pricecategories.config").toURI();
		try (Stream<String> stream = Files.lines(Paths.get(uri))) {
			stream.forEach(x -> {
				try {
					Class.forName(x);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			});
		}
    }

    private Movie createMovieMock(UUID id, boolean isrented, int releaseyear) {
		Movie movie = mock(Movie.class);
		PriceCategory pc = mock(PriceCategory.class);
		when(movie.getId()).thenReturn(id);
		when(movie.isRented()).thenReturn(isrented);
		when(movie.getTitle()).thenReturn("Movie" + id);
		when(movie.getReleaseDate()).thenReturn(LocalDate.of(releaseyear, 5, 3));
		when(movie.getAgeRating()).thenReturn(12);
		when(movie.getPriceCategory()).thenReturn(pc);
		when(pc.toString()).thenReturn("Normal");
		return movie;
	}
	
	private User createUserMock(UUID id, int yearofbirth) {
		User user = mock(User.class);
		when(user.getId()).thenReturn(id);
		when(user.getName()).thenReturn("Name" + id);
		when(user.getFirstName()).thenReturn("FirstName" + id);
		when(user.getBirthdate()).thenReturn(LocalDate.of(yearofbirth,  11, 6));
		return user;
	}
	
	private Rental createRentalMock(UUID id, boolean isrented, int releaseyear, int birthyear) {
		UUID uid = UUID.randomUUID();
		UUID mid = UUID.randomUUID();
		
		Rental rental = mock(Rental.class);
		Movie m = createMovieMock(mid, isrented, releaseyear);
		User u = createUserMock(uid, birthyear);
		
		when(rental.getMovie()).thenReturn(m);
		when(rental.getUser()).thenReturn(u);
		when(rental.getId()).thenReturn(id);
		when(rental.getRentalDate()).thenReturn(LocalDate.now().minusDays(5));
		when(rental.getRentalDays()).thenReturn(5);
		return rental;
	}

	@Test
	void testConvertMovie() {
		UUID id = UUID.randomUUID();
		Movie movie = createMovieMock(id, true, 1970);
		MovieDTO dto = Conversions.convert(movie);
		assertAll("Movie properties",
			() -> assertEquals(id.toString(), dto.id.get()),
			() -> assertEquals(movie.getTitle(), dto.title.get()),
			() -> assertEquals(movie.getPriceCategory().toString(), dto.priceCategory.get()),
			() -> assertEquals(movie.getAgeRating(), dto.ageRating.get()),
			() -> assertEquals(movie.getReleaseDate(), dto.releaseDate.get()),
			() -> assertEquals(movie.isRented(), dto.rented.get())
		);
	}

	@Test
	void testConvertMovieDTO() {
		LocalDate yesterday = LocalDate.now().minusDays(1);
		UUID uid = UUID.randomUUID();
		MovieDTO dto = new MovieDTO(uid, true, 12, "Titanic", yesterday, "New Release");
		Movie movie = Conversions.convert(dto);
		assertAll("Movie class",
			() -> assertEquals(uid, movie.getId()),
			() -> assertTrue(movie.isRented()),
			() -> assertEquals(12, movie.getAgeRating()),
			() -> assertEquals("Titanic", movie.getTitle()),
			() -> assertEquals(yesterday, movie.getReleaseDate()),
			() -> assertEquals(NewReleasePriceCategory.class, movie.getPriceCategory().getClass())
		);
	}

	@Test
	void testConvertMovieList() {
		UUID one = UUID.randomUUID();
		UUID two = UUID.randomUUID();
		UUID thr = UUID.randomUUID();

		Movie m1 = createMovieMock(one, true, 1980);
		Movie m2 = createMovieMock(two, false, 1990);
		Movie m3 = createMovieMock(thr, true, 2000);
		List<Movie> movies = new ArrayList<>(3);
		movies.add(m1); movies.add(m2); movies.add(m3);
		
		Collection<MovieDTO> dtos = Conversions.convertMovieList(movies);
		assertEquals(3, dtos.size());
		
		List<UUID>ids = new ArrayList<>(3);
		ids.add(one); ids.add(two); ids.add(thr);
		for (MovieDTO m: dtos) {
			UUID id = UUID.fromString(m.id.get());
			if (ids.contains(id)) {
				ids.remove(id);
			}
		}
		assertEquals(0, ids.size());
	}

	@Test
	void testConvertUser() {
		UUID uid = UUID.randomUUID();
		User user = createUserMock(uid, 2000);
		UserDTO dto = Conversions.convert(user);
		assertAll("User properties",
			() -> assertEquals(uid.toString(), dto.id.get()),
			() -> assertEquals(user.getName(), dto.name.get()),
			() -> assertEquals(user.getFirstName(), dto.firstName.get()),
			() -> assertEquals(user.getBirthdate(), dto.birthdate.get())
		);
	}

	@Test
	void testConvertUserDTO() {
		UUID id = UUID.randomUUID();
		LocalDate thrity_years_ago = LocalDate.now().minusYears(30);
		UserDTO dto = new UserDTO(id, "Doe", "John", thrity_years_ago);
		User user = Conversions.convert(dto);
		assertAll("Movie class",
			() -> assertEquals(id, user.getId()),
			() -> assertEquals("Doe", user.getName()),
			() -> assertEquals("John", user.getFirstName()),
			() -> assertEquals(thrity_years_ago, user.getBirthdate())
		);
	}

	@Test
	void testConvertUserList() {
		UUID one = UUID.randomUUID();
		UUID two = UUID.randomUUID();
		UUID thr = UUID.randomUUID();
		
		User u1 = createUserMock(one, 2001);
		User u2 = createUserMock(two, 2002);
		User u3 = createUserMock(thr, 2003);
		List<User> users = new ArrayList<>(3);
		users.add(u1); users.add(u2); users.add(u3);
		
		Collection<UserDTO> dtos = Conversions.convertUserList(users);
		assertEquals(3, dtos.size());
		
		List<UUID>ids = new ArrayList<>(3);
		ids.add(one); ids.add(two); ids.add(thr);
		for (UserDTO m: dtos) {
			UUID id = UUID.fromString(m.id.get());
			if (ids.contains(id)) {
				ids.remove(id);
			}
		}
		assertEquals(0, ids.size());
	}

	@Test
	void testConvertRental() {
		UUID rid = UUID.randomUUID();
		Rental rental = createRentalMock(rid, true, 1960, 1995);
		RentalDTO dto = Conversions.convert(rental);
		assertAll("Rental properties",
			() -> assertEquals(rid, UUID.fromString(dto.id.get())),
			() -> assertEquals(rental.getMovie().getTitle(), dto.title.get()),
			() -> assertEquals(rental.getUser().getName(), dto.userName.get()),
			() -> assertEquals(rental.getUser().getFirstName(), dto.userFirstName.get()),
			() -> assertEquals(rental.getRentalDate(), dto.rentalDate.get()),
			() -> assertEquals(rental.getRentalDays(), dto.rentalDays.get()),
			() -> assertEquals(rental.getRentalFee(), dto.rentalFee.get(), 0.00001)
		);
	}

	@Test
	void testConvertRentalList() {
		UUID one = UUID.randomUUID();
		UUID two = UUID.randomUUID();
		UUID thr = UUID.randomUUID();

		Rental u1 = createRentalMock(one, true, 1950, 2001);
		Rental u2 = createRentalMock(two, false, 1960, 2002);
		Rental u3 = createRentalMock(thr, true, 1970, 2003);
		List<Rental> rentals = new ArrayList<>(3);
		rentals.add(u1); rentals.add(u2); rentals.add(u3);
		
		Collection<RentalDTO> dtos = Conversions.convertRentalList(rentals);
		assertEquals(3, dtos.size());
		
		List<UUID>ids = new ArrayList<>(3);
		ids.add(one); ids.add(two); ids.add(thr);
		for (RentalDTO m: dtos) {
			UUID id = UUID.fromString(m.id.get());
			if (ids.contains(id)) {
				ids.remove(id);
			}
		}
		assertEquals(0, ids.size());
	}

}
