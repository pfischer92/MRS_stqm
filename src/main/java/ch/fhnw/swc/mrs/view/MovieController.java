package ch.fhnw.swc.mrs.view;

import java.time.LocalDate;
import java.util.UUID;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Java FX controller class for Movies.
 */
public class MovieController extends AbstractController {
    @FXML
    private TableView<MovieDTO> movieTable;
    @FXML
    private TableColumn<MovieDTO, String> idColumn;
    @FXML
    private TableColumn<MovieDTO, String> titleColumn;
    @FXML
    private TableColumn<MovieDTO, LocalDate> releaseDateColumn;
    @FXML
    private TableColumn<MovieDTO, Number> ageRatingColumn;
    @FXML
    private TableColumn<MovieDTO, String> priceCategoryColumn;
    @FXML
    private GridPane grid;
    @FXML
    private TextField titleField;
    @FXML
    private DatePicker releaseDatePicker;
    @FXML
    private ComboBox<String> priceCategoryChooser;
    @FXML
    private ComboBox<Integer> ageRatingChooser;
    @FXML
    private Button cancelButton;
    @FXML
    private Button newButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button saveButton;

    private MovieDTO editing = null; // currently no movie is being edited.
    private ObservableList<MovieDTO> movieList = FXCollections.observableArrayList();

    @Override
    public void reload() {
        movieList.clear();
        for (MovieDTO m : Conversions.convertMovieList(getBackend().getAllMovies())) {
            movieList.add(m);
        }
        movieTable.setItems(movieList);
    }

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Initialize the movie table.
        idColumn.setCellValueFactory(cellData -> cellData.getValue().id);
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().title);
        releaseDateColumn.setCellValueFactory(cellData -> cellData.getValue().releaseDate);
        ageRatingColumn.setCellValueFactory(cellData -> cellData.getValue().ageRating);
        priceCategoryColumn.setCellValueFactory(cellData -> cellData.getValue().priceCategory);

        priceCategoryChooser.getItems().addAll("Regular", "Children", "New Release");

        ageRatingChooser.getItems().addAll(0, 6, 12, 14, 16, 18);

        movieTable.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleSelectionChange(oldValue, newValue));

        grid.setDisable(true);
        showMovieDetails(null);
    }

    private Object handleSelectionChange(MovieDTO oldValue, MovieDTO newValue) {
        handleCancel();
        if (newValue != null) {
            editButton.setDisable(false);
            deleteButton.setDisable(false);
        }
        return null;
    }

    private void showMovieDetails(MovieDTO movie) {
        if (movie != null) {
            // fill the labels with info from the MovieVM object
            titleField.setText(movie.title.get());
            releaseDatePicker.setValue(movie.releaseDate.get());
            priceCategoryChooser.setValue(movie.priceCategory.get()); // getPriceCategory());
            ageRatingChooser.setValue(movie.ageRating.get());
        } else {
            // clear the content and set default values
            titleField.setText("");
            releaseDatePicker.setValue(null);
            priceCategoryChooser.setValue(null);
            ageRatingChooser.setValue(null);
        }
    }

    @FXML
    private void handleCancel() {
        cancelButton.setDisable(true);
        newButton.setDisable(false);
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        saveButton.setDisable(true);
        showMovieDetails(null);
        titleField.setEditable(false);
        releaseDatePicker.setEditable(false);
        grid.setDisable(true);
        editing = null;
    }

    @FXML
    private void handleNew() {
        cancelButton.setDisable(false);
        newButton.setDisable(true);
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        saveButton.setDisable(false);
        showMovieDetails(null);
        titleField.setEditable(true);
        releaseDatePicker.setEditable(true);
        grid.setDisable(false);
        titleField.requestFocus();
        editing = null;
    }

    @FXML
    private void handleSave() {
        if (editing == null) {
            MovieDTO m = Conversions.convert(getBackend().
            		createMovie(titleField.getText(),
                    	  releaseDatePicker.getValue(), 
                    	  priceCategoryChooser.getValue(),
                    	  ageRatingChooser.getValue()));
            movieTable.getItems().add(m);
        } else {
            editing.title.set(titleField.getText());
            editing.releaseDate.set(releaseDatePicker.getValue());
            editing.priceCategory.set(priceCategoryChooser.getValue());
            editing.ageRating.set(ageRatingChooser.getValue());
            getBackend().updateMovie(Conversions.convert(editing));
        }
        handleCancel();
    }

    @FXML
    private void handleEdit() {
        cancelButton.setDisable(false);
        newButton.setDisable(true);
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        saveButton.setDisable(false);
        MovieDTO m = movieTable.getSelectionModel().getSelectedItem();
        showMovieDetails(m);
        titleField.setEditable(true);
        releaseDatePicker.setEditable(true);
        grid.setDisable(false);
        titleField.requestFocus();
        editing = m;
    }

    @FXML
    private void handleDelete() {
        cancelButton.setDisable(true);
        newButton.setDisable(false);
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        saveButton.setDisable(true);
        MovieDTO m = movieTable.getSelectionModel().getSelectedItem();
        if (getBackend().deleteMovie(UUID.fromString(m.id.get()))) {
            movieTable.getItems().remove(m);
            movieTable.getSelectionModel().clearSelection();
        }
        showMovieDetails(null);
        titleField.setEditable(false);
        releaseDatePicker.setEditable(false);
        grid.setDisable(true);
        editing = null;
    }

}
