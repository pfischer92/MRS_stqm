package ch.fhnw.swc.mrs.view;

import java.time.LocalDate;
import java.util.UUID;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Java FX controller class for Rentals.
 */
public class RentalController extends AbstractController {
    @FXML
    private TableView<RentalDTO> rentalTable;
    @FXML
    private TableColumn<RentalDTO, String> idColumn;
    @FXML
    private TableColumn<RentalDTO, Number> rentalDaysColumn;
    @FXML
    private TableColumn<RentalDTO, LocalDate> rentalDateColumn;
    @FXML
    private TableColumn<RentalDTO, String> surnameColumn;
    @FXML
    private TableColumn<RentalDTO, String> firstNameColumn;
    @FXML
    private TableColumn<RentalDTO, String> titleColumn;
    @FXML
    private TableColumn<RentalDTO, Number> rentalFeeColumn;
    @FXML
    private Button deleteButton;

    private ObservableList<RentalDTO> rentalList = FXCollections.observableArrayList();

    @Override
    public void reload() {
        rentalList.clear();
        for (RentalDTO r : Conversions.convertRentalList(getBackend().getAllRentals())) {
            rentalList.add(r);
        }
        rentalTable.setItems(rentalList);
    }

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Initialize the movie table.
        idColumn.setCellValueFactory(cellData -> cellData.getValue().id);
        rentalDaysColumn.setCellValueFactory(cellData -> cellData.getValue().rentalDays);
        rentalDateColumn.setCellValueFactory(cellData -> cellData.getValue().rentalDate);
        surnameColumn.setCellValueFactory(cellData -> cellData.getValue().userName);
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().userFirstName);
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().title);
        rentalFeeColumn.setCellValueFactory(cellData -> cellData.getValue().rentalFee);

        rentalTable.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleSelectionChange(oldValue, newValue));

        deleteButton.setDisable(true);
    }

    private Object handleSelectionChange(RentalDTO oldValue, RentalDTO newValue) {
        deleteButton.setDisable(newValue == null);
        return null;
    }

    @FXML
    private void handleDelete() {
        deleteButton.setDisable(true);
        RentalDTO r = rentalTable.getSelectionModel().getSelectedItem();
        if (getBackend().returnRental(UUID.fromString(r.id.get()))) {
            rentalTable.getItems().remove(r);
            rentalTable.getSelectionModel().clearSelection();
        }
    }

}
