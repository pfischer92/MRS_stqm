package ch.fhnw.swc.mrs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.fhnw.swc.mrs.api.MovieRentalException;

public class AgeRatingTest {
	final LocalDate today = LocalDate.now();
	final String title = "aTitle";
    PriceCategory pc = RegularPriceCategory.getInstance();
	
	final String name = "aName";
	final String firstname = "aFistName";
	
	@DisplayName("Test Movie constructor with parameter ageRating.")
	@Test
	public void testMovieCtor() {
		int legalAgeRatings[] = new int[] {0, 1, 10, 17, 18};
		int illegalAgeRatings[] = new int[] {Integer.MIN_VALUE, -20, -1, 19, 2646, Integer.MAX_VALUE};
		
		
		for(int ar: legalAgeRatings) {
			new Movie(title, today, pc, ar);
		}
		
		for(int ar: illegalAgeRatings) {
			Throwable t = assertThrows(IllegalArgumentException.class, () -> new Movie(title, today, pc, ar));
			assertEquals(Movie.EXC_AGE_RATING, t.getMessage());
		}
	}
	
	@DisplayName("Test User constructor with parameter birthdate")
	@Test
	public void testUserCtor() {
		LocalDate old120 = today.minusYears(120); 
		LocalDate legalBirthdates[] = new LocalDate[] 
				{today, today.minusDays(1), today.minusYears(4), old120.plusDays(1), old120}; 
		LocalDate illegalBirthdates[] = new LocalDate[]
				{today.plusDays(1), today.plusYears(34), old120.minusDays(1), old120.minusYears(5)};
		
		for(LocalDate d: legalBirthdates) {
			new User(name, firstname, d);
		}
		
		for(LocalDate d: illegalBirthdates) {
			Throwable t = assertThrows(IllegalArgumentException.class, () -> new User(name, firstname, d));
			assertEquals(User.ILLEGAL_BIRTHDATE, t.getMessage());
		}
	}
	
	@DisplayName("new born user renting movie with no age rating")
	@Test
	public void testRentalCtor1() {
		Movie m = new Movie(title, today, pc, 0);
		User u = new User(name, firstname, today);
		
		new Rental(u, m, today);
	}

	@DisplayName("new born user renting a movie with age rating")
	@Test
	public void testRentalCtor2() {
		Movie m = new Movie(title, today, pc, 5);
		User u = new User(name, firstname, today);
		
		Throwable t = assertThrows(MovieRentalException.class, () -> new Rental(u, m, today));
		assertEquals(Rental.EXC_UNDER_AGE, t.getMessage());
	}
	
	@DisplayName("18 year old renting adult movie")
	@Test
	public void testRentalCtor3() {
		Movie m = new Movie(title, today, pc, 18);
		User u = new User(name, firstname, today.minusYears(18));
		
		new Rental(u, m, today);
	}

	@DisplayName("17 year old renting adult movie")
	@Test
	public void testRentalCtor4() {
		Movie m = new Movie(title, today, pc, 18);
		User u = new User(name, firstname, today.minusYears(18).plusDays(1));
		
		Throwable t = assertThrows(MovieRentalException.class, () -> new Rental(u, m, today));
		assertEquals(Rental.EXC_UNDER_AGE, t.getMessage());
	}

}
