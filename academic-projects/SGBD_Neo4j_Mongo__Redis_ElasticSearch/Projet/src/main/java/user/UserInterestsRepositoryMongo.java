package user;

import app.DbConfig;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB repository for user profile data.
 *
 * Stores user interests in the "user_profiles" collection.
 * Each profile document typically contains:
 * - userId
 * - interests: array of strings
 */
public final class UserInterestsRepositoryMongo implements AutoCloseable {

    /** The MongoDB client instance. */
    private final MongoClient client;

    /** The specific collection storing user profile documents. */
    private final MongoCollection<Document> col;

    /**
     * Creates the repository and connects to MongoDB using {@link DbConfig}.
     *
     * @param cfg application database configuration
     */
    public UserInterestsRepositoryMongo(DbConfig cfg) {
        this.client = MongoClients.create(cfg.mongoUri());
        MongoDatabase db = client.getDatabase(cfg.mongoDb());
        this.col = db.getCollection("user_profiles");
    }

    /**
     * Adds an interest to the given user profile.
     *
     * Uses upsert to create the document if it does not exist yet.
     * Uses addToSet to avoid duplicates in the interests array.
     *
     * @param userId   user identifier
     * @param interest interest to add
     */
    public void addInterest(long userId, String interest) {
        col.updateOne(
                Filters.eq("userId", userId),
                Updates.addToSet("interests", interest),
                new UpdateOptions().upsert(true)
        );
    }

    /**
     * Fetches the raw MongoDB profile document for a user.
     *
     * @param userId user identifier
     * @return profile document, or null if not found
     */
    public Document getProfile(long userId) {
        return col.find(Filters.eq("userId", userId)).first();
    }

    /**
     * Lists all distinct interests across the entire user base.
     *
     * @return list of unique interests strings
     */
    public List<String> listAllInterests() {
        return col.distinct("interests", String.class).into(new ArrayList<>());
    }

    /**
     * Lists all interests associated with a specific user.
     *
     * @param userId user identifier
     * @return list of interests (empty list if the user has no profile or no interests)
     */
    public List<String> listUserInterests(long userId) {
        Document doc = col.find(Filters.eq("userId", userId)).first();

        if (doc == null || !doc.containsKey("interests")) {
            return List.of();
        }

        return doc.getList("interests", String.class);
    }

    /**
     * Drops the user_profiles collection.
     * Used for resetting the environment during demos.
     */
    public void reset() {
        col.drop();
    }


    /**
     * Closes the MongoDB client connection.
     */
    @Override
    public void close() {
        client.close();
    }
}