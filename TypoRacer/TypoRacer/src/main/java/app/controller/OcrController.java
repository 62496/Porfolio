package app.controller;

import app.dto.Texte;
import app.model.OcrModel;
import app.service.TexteService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.sql.SQLException;


/**
 * Contrôleur de la vue Ocr.fxml.
 * Permet à l'utilisateur d'importer un fichier image ou PDF,
 * d'en extraire le texte via OCR, et de l'enregistrer dans la base.
 */
public class OcrController {

    @FXML private TextField txtTitre;
    @FXML private TextArea txtResultat;

    private final OcrModel ocrModel = new OcrModel();
    private TexteService texteService = new TexteService();
    private File imageChoisie;
    private String userName;

    public void setUserName(String userName) {
        this.userName = userName;
    }
    /**
     * Méthode appelée lors de l'appui sur le bouton "Choisir fichier".
     * Ouvre une boîte de dialogue pour sélectionner un fichier image ou PDF.
     */
    @FXML
    private void onChoisirImage() throws TesseractException, SQLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        imageChoisie = fileChooser.showOpenDialog(null);
        if (imageChoisie != null) {
            ocrModel.setFileToScan(imageChoisie.getAbsolutePath());
            onScanner();
        } else {
            showAlert("Aucune image sélectionnée.");
        }
    }
    /**
     * Méthode appelée lors du clic sur le bouton "Scanner".
     * Lance l’analyse OCR de l’image choisie via le modèle OcrModel.
     * Affiche le résultat dans la zone de texte `txtResultat`.
     *
     * @throws TesseractException si l’analyse OCR échoue
     */
    @FXML
    private void onScanner() throws TesseractException {
        if (imageChoisie == null) {
            txtResultat.setText("Veuillez d'abord choisir une image.");
            return;
        }
        String result = ocrModel.scan();
        txtResultat.setText(result);
    }
    /**
     * Méthode appelée lors du clic sur le bouton "Ajouter".
     * Valide les champs titre et texte, puis enregistre le texte en base si tout est conforme.
     *
     * @throws SQLException si l'enregistrement dans la base échoue
     */
    @FXML
    private void onAjout() throws SQLException {
        if (txtTitre.getText().trim().isEmpty()){
            showAlert("Veuillez entrer un titre !");
        }
        else if (!texteService.isValidTitle(txtTitre.getText().trim()) ) {
            showAlert("Ce nom est déjà utilisé !");
        }else if (!texteService.isValidText(txtResultat.getText().trim())){
            showAlert("Texte manquant ou trop court (min 100 caractères) !");
        }else {
            Texte texte = new Texte(0, txtTitre.getText(), txtResultat.getText(), userName);
            try {
                texteService.saveTexte(texte);
                txtResultat.setText("Texte enregistré avec succès !");
            } catch (SQLException e) {
                txtResultat.setText("Erreur : " + e.getMessage());
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
