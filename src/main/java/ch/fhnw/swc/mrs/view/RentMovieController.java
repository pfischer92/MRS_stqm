package ch.fhnw.swc.mrs.view;

import java.time.LocalDate;
import java.util.UUID;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/** Java FX controller class for rent movies tab. */
public class RentMovieController extends AbstractController {
    @FXML
    private TableView<MovieDTO> availableMoviesTable;
    @FXML
    private TableColumn<MovieDTO, String> idColumn;
    @FXML
    private TableColumn<MovieDTO, String> titleColumn;
    @FXML
    private TableColumn<MovieDTO, LocalDate> releaseDateColumn;
    @FXML
    private TableColumn<MovieDTO, String> priceCategoryColumn;
    @FXML
    private TextField idField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField firstnameField;
    @FXML
    private DatePicker birthdatePicker;
    @FXML
    private DatePicker rentalDatePicker;
    @FXML
    private CheckBox newUser;
    @FXML
    private Button getUserButton;
    @FXML
    private Button clearAllButton;
    @FXML
    private Button saveButton;

    private ObservableList<MovieDTO> rentMovieList = FXCollections.observableArrayList();
    private UserDTO found = null;

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Initialize the movie table.
        idColumn.setCellValueFactory(cellData -> cellData.getValue().id);
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().title);
        releaseDateColumn.setCellValueFactory(cellData -> cellData.getValue().releaseDate);
        priceCategoryColumn.setCellValueFactory(cellData -> cellData.getValue().priceCategory);
        availableMoviesTable.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleSelectionChange(oldValue, newValue));
    }

    @Override
    public void reload() {
        rentMovieList.clear();
        for (MovieDTO m : Conversions.convertMovieList(getBackend().getAllMovies(false))) {
            rentMovieList.add(m);
        }
        availableMoviesTable.setItems(rentMovieList);
    }

    @FXML
    private void handleNewUser() {
        if (newUser.isSelected()) {
            setNewUserEnabling();
        } else {
            setReadyEnabling();
        }
        clearAllFields();
        surnameField.requestFocus();
    }

    private void setReadyEnabling() {
        newUser.setDisable(false);
        getUserButton.setDisable(false);
        saveButton.setDisable(true);
        idField.setDisable(false);
        surnameField.setDisable(false);
        firstnameField.setDisable(true);
        birthdatePicker.setDisable(true);
        rentalDatePicker.setDisable(true);
    }

    private void setNewUserEnabling() {
        newUser.setDisable(false);
        getUserButton.setDisable(true);
        saveButton.setDisable(false);
        idField.setDisable(true);
        surnameField.setDisable(false);
        firstnameField.setDisable(false);
        birthdatePicker.setDisable(false);
        rentalDatePicker.setDisable(false);
    }

    private void clearAllFields() {
        surnameField.clear();
        firstnameField.clear();
        idField.clear();
        rentalDatePicker.setValue(null);
        birthdatePicker.setValue(null);
    }

    @FXML
    private void handleClearAll() {
        newUser.setSelected(false);
        setReadyEnabling();
        clearAllFields();
    }

    @FXML
    private void handleGetUser() {
        String username = surnameField.getText();
        String idstring = idField.getText();
        try {
            UUID id = UUID.fromString(idstring);
            found = Conversions.convert(getBackend().getUserById(id));
        } catch (IllegalArgumentException e) {
            found = Conversions.convert(getBackend().getUserByName(username));
        }
        if (found != null) {
            idField.setText(found.id.get());
            surnameField.setText(found.name.get());
            firstnameField.setText(found.firstName.get());
            birthdatePicker.setValue(found.birthdate.get());
            rentalDatePicker.setValue(LocalDate.now());
            idField.setDisable(true);
            surnameField.setDisable(true);
        } else {
            idField.setText(null);
            surnameField.setText(null);
            firstnameField.setText(null);
            surnameField.requestFocus();
        }
    }

    @FXML
    private void handleSave() {
    	MovieDTO m = availableMoviesTable.getSelectionModel().getSelectedItem();
        reload();
        try {
        	UUID uid = UUID.fromString(found.id.get());
        	UUID mid = UUID.fromString(m.id.get());
        	getBackend().createRental(uid, mid, LocalDate.now());
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.showAndWait();        
        }
        reload();
        handleClearAll();
    }

    @FXML
    private void enterPressed() {
        if (newUser.isSelected() && !saveButton.isDisabled()) { // enter means save
            handleSave();
        } else { // enter means get User
            handleGetUser();
        }
    }

    private void handleSelectionChange(MovieDTO oldMovie, MovieDTO newMovie) {
        saveButton.setDisable(newMovie == null);
    }

}
