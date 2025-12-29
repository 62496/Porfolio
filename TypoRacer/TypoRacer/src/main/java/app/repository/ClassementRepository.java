package app.repository;

import app.dto.Classement;
import app.dto.User;
import app.model.Difficulty;
import app.model.Mode;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Couche de service entre les contrôleurs et le DAO Classement.
 * Fournit un accès simple aux classements et aux scores.
 */
public class ClassementRepository {
    private final ClassementDao classementDao;
    private Map<Integer,String > classementCache;

    public ClassementRepository( ) {
        Connection connection = ConnectionManager.getConnection();
        this.classementDao = new ClassementDao(connection);
        this.classementCache = new ConcurrentHashMap<>();
    }
    /**
     * Récupère le classement pour un texte, un mode et une difficulté.
     */
    public List<Classement> getClassement(Mode mode, int texteId, Difficulty difficulty){
        return classementDao.getClassement(mode,texteId,difficulty);
    }
    /**
     * Récupère le record de score d’un utilisateur.
     */
    public int getRecord(Mode mode, int id,int texteId,Difficulty difficulty){
        return classementDao.getRecord(mode, id,texteId,difficulty);
    }
    /**
     * Ajoute un score à la base.
     */
    public boolean addScore(Mode mode, Difficulty difficulty, User user,int score,int textId){;
        return classementDao.addScore(mode,difficulty,user,score,textId);
    }
}