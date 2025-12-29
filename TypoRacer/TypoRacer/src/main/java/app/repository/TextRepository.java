package app.repository;

import app.dto.Texte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Couche de service permettant d'accéder aux textes via un cache et DAO.
 */
public class TextRepository {

    private final TextDao textDao;
    private final Map<Integer, Texte> TextCache;

    public TextRepository() {
        Connection connection = ConnectionManager.getConnection();
        this.textDao = new TextDao(connection);
        this.TextCache = new ConcurrentHashMap<>();
        loadCache();
    }
    /**
     * Récupère la liste de tous les titres de texte.
     */
    public List<String> getAllTitres() throws SQLException {
        return textDao.getAllTitres();
    }

    /**
     * Récupère tous les textes depuis le DAO.
     */
    public List<Texte> getTextes() throws SQLException {
        return textDao.findAll();
    }
    /**
     * Charge le cache à partir de la base.
     */
    private void loadCache() {
        textDao.findAll().forEach(
                texte -> TextCache.put(texte.id(), texte));
    }
    /**
     * Ajoute un texte à la base et au cache.
     */
    public void addTexte(Texte texte) throws SQLException {
        textDao.insert(texte);         // Insère en base
        TextCache.put(texte.id(), texte); // Met à jour le cache
    }

}
