package ch.fhnw.swc.mrs.gui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import ch.fhnw.swc.mrs.MovieRentalSystem;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

public class STRentMovieTab extends ApplicationTest {

	private FxRobot robot;
	private TextField idField;
	private TextField surnameField;
	private TextField firstnameField;
	private DatePicker birthDatePicker;
	private DatePicker rentalDatePicker;
	private CheckBox newUserCB;
	private Button getUserButton;
	private Button clearAllButton;
	private Button saveButton;

	@Override
	public void start(Stage stage) throws Exception {
		new MovieRentalSystem().start(stage);
	}

	@Override
	public void stop() {
	}

	@BeforeEach
	public void setUp() throws Exception {
		robot = new FxRobot();
		idField = (TextField) robot.lookup("#IdF").query();
		surnameField = (TextField) robot.lookup("#SurnameF").query();
		firstnameField = (TextField) robot.lookup("#FirstnameF").query();
		birthDatePicker = (DatePicker) robot.lookup("#BirthdateP").query();
		rentalDatePicker = (DatePicker) robot.lookup("#RentalDateP").query();
		
		newUserCB = (CheckBox) robot.lookup("#NewUserCB").query();
		getUserButton = (Button) robot.lookup("#GetUserB").query();
		
		clearAllButton = (Button) robot.lookup("#ClearAllB").query();
		saveButton = (Button) robot.lookup("#SaveB").query();
	}

	@AfterEach
	public void tearDown() throws Exception {
		FxToolkit.hideStage();
		release(new KeyCode[] {});
		release(new MouseButton[] {});
	}
	
	/**
	 * verify the DISABLED states of the various gui elements.
	 * @param id false = enabled, true = disabled.
	 * @param surname false = enabled, true = disabled.
	 * @param firstname false = enabled, true = disabled.
	 * @param birthDate false = enabled, true = disabled.
	 * @param rentalDate false = enabled, true = disabled.
	 * @param newUser false = enabled, true = disabled.
	 * @param getUser false = enabled, true = disabled.
	 * @param clearAll false = enabled, true = disabled.
	 * @param save false = enabled, true = disabled.
	 */
	private void verifyUIState(boolean id, boolean surname, boolean firstname, boolean birthDate, boolean rentalDate, 
							   boolean newUser, boolean getUser, boolean clearAll, boolean save) {
		assertAll("verify ui states", 
			() -> assertEquals(id, idField.isDisabled(), "idField disabling"),
			() -> assertEquals(surname, surnameField.isDisabled(), "surnameField disabling"),
			() -> assertEquals(firstname, firstnameField.isDisabled(), "firstnameField disabling"),
			() -> assertEquals(birthDate, birthDatePicker.isDisabled(), "birthdatePicker disabling"),
			() -> assertEquals(rentalDate, rentalDatePicker.isDisabled(), "rentalDatePicker disabling"),
			
			() -> assertEquals(newUser, newUserCB.isDisabled(), "newUser Checkbox disabling"),
			() -> assertEquals(getUser, getUserButton.isDisabled(), "getUserButton disabling"),
			
			() -> assertEquals(clearAll, clearAllButton.isDisabled(), "clearAllButton disabling"),
			() -> assertEquals(save, saveButton.isDisabled(), "saveButton disabling")
		);
	}
	
	@DisplayName("Task 2.a.a no movie selected")
	@Test
	void nothingSelected() throws Exception {
		verifyUIState(false, // idField is not disabled
					  false, // surnameField is not disabled
					  true,  // firstnameField is disabled
					  true,  // birthdatePicker is disabled
					  true,  // rentalDatePicker is disabled
					  false, // newUserCB is not disabled
					  false, // getUserButton is not disabled
					  false, // clearAllButton is not disabled
					  true   // saveButton is disabled
		);
	}

	@DisplayName("Task 2.a.b movie selected (save button is enabled).")
	@Test
	void selectMovie() throws Exception {
		clickOn("00000000-0000-0000-0000-000000000004");
		verifyUIState(false, // idField is not disabled
					  false, // surnameField is not disabled
					  true,  // firstnameField is disabled
					  true,  // birthdatePicker is disabled
					  true,  // rentalDatePicker is disabled
					  false, // newUserCB is not disabled
					  false, // getUserButton is not disabled
					  false, // clearAllButton is not disabled
					  false  // saveButton is not disabled
		);		
	}
	
	private void verifyUIContent(String id, String surname, String firstname, LocalDate birthdate, LocalDate rental) {
		assertAll("verify ui content",
			() -> assertEquals(id, idField.getText(), "idField text"),
			() -> assertEquals(surname, surnameField.getText(), "surnameField text"),
			() -> assertEquals(firstname, firstnameField.getText(), "firstnameField text"),
			() -> assertEquals(birthdate, birthDatePicker.getValue(), "birthdatePicker date"),
			() -> assertEquals(rental, rentalDatePicker.getValue(), "rentalDatePicker date")
		);
	}

	@DisplayName("Task 2.a.c user entered.")
	@Test
	void enterExistingUser() throws Exception {
		clickOn("#SurnameF");
		write("Locher");
		clickOn("#GetUserB");
		verifyUIState(true,  // idField is disabled
					  true,  // surnameField is disabled
					  true,  // firstnameField is disabled
					  true,  // birthdatePicker is disabled
					  true,  // rentalDatePicker is disabled
					  false, // newUserCB is not disabled
					  false, // getUserButton is not disabled
					  false, // clearAllButton is not disabled
					  true   // saveButton is disabled
		);
		verifyUIContent("20000000-0000-0000-0000-000000000002", "Locher", "Bernhard", 
						LocalDate.of(1998, 1, 1), LocalDate.now());
	}
	
	@DisplayName("Task 2.a.d user entered, and movie selected. Save button activated.")
	@Test
	void saveRental() throws Exception {
		enterExistingUser();
		clickOn("00000000-0000-0000-0000-000000000004");
		clickOn("#SaveB");
		nothingSelected();
		verifyUIContent("", "", "", null, null);
	}
	
	/** This tests moves the mouse over all UI elements we want to test.
	 * Uncomment @Disabled and watch the test!
	 * @throws Exception
	 */
	@Disabled
	@Test
	void moveMouseOverAllUIElementsToTest() throws Exception {
		clickOn("00000000-0000-0000-0000-000000000004");
		Thread.sleep(2000);
		clickOn("#IdF");
		Thread.sleep(2000);
		clickOn("#SurnameF");
		Thread.sleep(2000);
		clickOn("#FirstnameF");
		Thread.sleep(1000);
		clickOn("#BirthdateP");
		Thread.sleep(1000);
		clickOn("#RentalDateP");
		Thread.sleep(1000);
		clickOn("#NewUserCB");
		Thread.sleep(1000);
		clickOn("#GetUserB");
		Thread.sleep(1000);
		clickOn("#ClearAllB");
		Thread.sleep(1000);
		clickOn("#SaveB");
		Thread.sleep(2000);
		verifyUIState(false, // idField is not disabled
				  false, // surnameField is not disabled
				  true,  // firstnameField is disabled
				  true,  // birthdatePicker is disabled
				  true,  // rentalDatePicker is disabled
				  false, // newUserCB is not disabled
				  false, // getUserButton is not disabled
				  false, // clearAllButton is not disabled
				  true   // saveButton is disabled
		);
	}

	/**
	 * Initialize the price categories for the MRS application. Do this before the
	 * application is started for the first time.
	 * 
	 * @throws Exception
	 *             whenever something goes unexpected, fail this test case.
	 */
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

}
