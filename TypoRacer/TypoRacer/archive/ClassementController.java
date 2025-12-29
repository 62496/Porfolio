package app.controller;


import app.model.Texte;
import app.model.User;
import app.repository.UserRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.Disabled;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Disabled
public class ClassementController {
    UserRepository userRepository = new UserRepository();

    @FXML
    private TableView<User,Integer> ClassementTable;

    @FXML
    private TableColumn<User> colUserName;

    @FXML
    private TableColumn<Integer> colScore;

    public void initialize() throws SQLException {
        colUserName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().name()));

        colScore.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().creator()));

        Map<String, Integer> Classement = new HashMap<>();
        Classement= userRepository.getClassement();
        ClassementTable.getItems().addAll(Textes);

        tableTextes.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        currentTexte = newSelection;
                        btnVoirTexte.setDisable(false);
                    }
                });
    }

}

