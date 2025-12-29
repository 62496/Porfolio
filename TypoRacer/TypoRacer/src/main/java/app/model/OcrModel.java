package app.model;

import app.Main;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
/**
 * Modèle responsable de l'extraction OCR du texte à partir d'une image ou d'un fichier PDF.
 * Utilise la bibliothèque Tess4J pour interfacer avec le moteur Tesseract.
 */
public class OcrModel {
    private Tesseract tesseract;
    private Path fileToScan;

    /**
     * Initialise le modèle OCR et l'instance de Tesseract.
     */
    public OcrModel() {
        this.tesseract = new Tesseract();
    }


    /**
     * Lance l'analyse OCR du fichier précédemment défini par {@link #setFileToScan(String)}.
     * Configure dynamiquement le chemin du dossier tessdata selon le système d'exploitation.
     *
     * @return Le texte extrait si réussi, ou un message d'erreur sinon.
     * @throws TesseractException si l'extraction OCR échoue.
     */
    public String scan() throws TesseractException {
        ClassLoader classLoader = Main.class.getClassLoader();

        if (fileToScan == null) {
            return "Erreur : aucune image sélectionnée.";
        }
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                //  Pour Windows
                //  Chemin d’installation Tesseract
                String dataDirectory = "src/main/resources/data";
                System.setProperty("TESSDATA_PREFIX", dataDirectory);
                tesseract.setDatapath(dataDirectory);
            } else if (os.contains("mac")) {
                //  Pour macOS
                String tessdataPath = classLoader.getResource("data").getPath();

                System.setProperty("jna.library.path", "/usr/local/Cellar/tesseract/5.5.0_1/lib");
                tesseract.setDatapath(tessdataPath);

                System.setProperty("TESSDATA_PREFIX", tessdataPath);
            } else {
                return "OS non supporté pour l'instant.";
            }

            tesseract.setLanguage("fra");

            System.out.println(" Image en cours de scan : " + fileToScan);
            return tesseract.doOCR(fileToScan.toFile());

        } catch (TesseractException e) {
            e.printStackTrace();
            return "Erreur d'extraction OCR";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Erreur de configuration OCR";
        }

    }
    /**
     * Définit le fichier à scanner.
     *
     * @param imageName Chemin absolu ou relatif du fichier à analyser.
     */
    public void setFileToScan(String imageName) {
        this.fileToScan = Paths.get(imageName);//ok
    }


}
