package user;

import java.util.List;

import recommendation.RecommendationService;
import search.SearchService;


/**
 * Business service for user management.
 *
 * Coordinates cross-database operations:
 * - SQL for identity
 * - MongoDB for interests
 * - Elasticsearch/Neo4j for secondary projections
 */
public final class UserService {

    /** SQL repository for structured user identity. */
    private final UserRepositorySql userSql;

    /** MongoDB repository for document-based user profiles. */
    private final UserInterestsRepositoryMongo interestsMongo;
    
    /** Service for indexing data into Elasticsearch. */
    private final SearchService searchService;
    
    /** Service for synchronizing graph data into Neo4j. */
    private final RecommendationService recommendationService;

    /**
     * Constructor with dependency injection.
     *
     * @param userSql SQL repository
     * @param interestsMongo MongoDB repository
     * @param searchService Search service
     * @param recommendationService Recommendation service
     */
    public UserService(UserRepositorySql userSql,
                    UserInterestsRepositoryMongo interestsMongo, 
                    SearchService searchService, 
                    RecommendationService recommendationService) {
        this.userSql = userSql;
        this.interestsMongo = interestsMongo;
        this.searchService = searchService;
        this.recommendationService = recommendationService;
    }

    /**
     * Resets user data across SQL and MongoDB for demo purposes.
     */
    public void resetDemo() {
        userSql.resetDemo();
        interestsMongo.reset();
    }

    /**
     * Creates a user and initializes their presence in search and recommendation engines.
     *
     * @param username the chosen username
     * @return the unique user ID
     */
    public long createUser(String username) {
        long id = userSql.createUser(username);
        searchService.indexUser(id, username, List.of());
        recommendationService.syncUserInterestsToNeo4j(id, List.of());
        return id;
    }

    /**
     * Finds a username by ID.
     * @param userId user identifier
     * @return the username string
     */
    public String findUsernameById(long userId ){
        return userSql.findUsernameById(userId);
    }

    /**
     * Adds a list of interests to a user and synchronizes all downstream systems.
     *
     * @param userId    user identifier
     * @param interests list of new interests to add
     */
    public void addInterests(long userId, List<String> interests) {
        List<String> existingInterests = interestsMongo.listUserInterests(userId);
        for (String i : interests) {
            if (existingInterests.contains(i)) {
                continue;
            }
            interestsMongo.addInterest(userId, i);
        }
        
        // Sync secondary systems with the updated full list of interests
        List<String> allUserInterests = listUserInterests(userId);
        searchService.indexUser(userId, findUsernameById(userId), allUserInterests);
        searchService.indexInterest(interests);
        recommendationService.syncUserInterestsToNeo4j(userId, allUserInterests);
    }

    /**
     * Searches for a user ID by username.
     * @param username search criteria
     * @return user ID or null
     */
    public Long findUserIdByUsername(String username) {
        return userSql.findUserIdByUsername(username);
    }

    /**
     * Lists every unique interest stored in the system.
     * @return list of interests
     */
    public List<String> listAllInterests() {
        return interestsMongo.listAllInterests();
    }

    /**
     * Lists interests for a specific user from MongoDB.
     * @param userId user identifier
     * @return list of strings
     */
    public List<String> listUserInterests(long userId) {
        return interestsMongo.listUserInterests(userId);
    }

    /**
     * Lists all existing user IDs.
     * @return list of IDs
     */
    public List<Long> listUsers() {
        return userSql.findAllUserIds();
    }

    /**
     * Returns a human-readable list of users (ID - Username).
     * @return list of formatted strings
     */
    public List<String> listUsersReadable() {
        return userSql.findAllUserIds().stream()
                .map(id -> id + " - " + userSql.findUsernameById(id))
                .toList();
    }

    /**
     * Verification of user existence.
     * @param id user ID
     * @return true if user exists in SQL
     */
    public boolean existsUser(long id) {
        return userSql.existsById(id);
    }
}