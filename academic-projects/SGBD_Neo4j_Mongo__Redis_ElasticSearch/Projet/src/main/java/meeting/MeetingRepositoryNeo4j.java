package meeting;

import static org.neo4j.driver.Values.*;

import java.util.List;
import java.util.Map;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

import app.DbConfig;

/**
 * Neo4j repository.
 *
 * This class is responsible for accessing the Neo4j database.
 * It stores meetings between users as relationships in a graph structure.
 */
public final class MeetingRepositoryNeo4j implements AutoCloseable {

    /**
     * Neo4j driver used to open database sessions.
     */
    private final Driver driver;

    /**
     * Constructor.
     * Initializes the connection to Neo4j using the application configuration.
     *
     * @param cfg database configuration
     */
    public MeetingRepositoryNeo4j(DbConfig cfg) {
        this.driver = GraphDatabase.driver(
                cfg.neo4jUri(),
                AuthTokens.basic(cfg.neo4jUser(), cfg.neo4jPassword())
        );
    }

    /**
     * Removes all data from the Neo4j database.
     * Used only to provide a clean and reproducible demo.
     */
    public void reset() {
        try (Session session = driver.session()) {
            session.executeWrite(tx -> {
                tx.run("MATCH (n) DETACH DELETE n");
                return null;
            });
        }
    }

    /**
     * Records a meeting between two users.
     *
     * @param userA first user identifier
     * @param userB second user identifier
     * @param interest shared interest during the meeting
     * @param nameA name of the first user
     * @param nameB name of the second user
     */
    public void recordMeeting(long userA, long userB, String interest, String nameA, String nameB) {

        String cypher = """
                MERGE (a:User {id: $a}) SET a.name = $nameA
                MERGE (b:User {id: $b}) SET b.name = $nameB
                CREATE (a)-[:MET {interest: $t, ts: datetime()}]->(b)
                CREATE (b)-[:MET {interest: $t, ts: datetime()}]->(a)
                """;

        try (Session session = driver.session()) {
            session.executeWrite(tx -> {
                tx.run(
                        cypher,
                        parameters(
                                "a", userA,
                                "nameA", nameA,
                                "b", userB,
                                "nameB", nameB,
                                "t", interest
                        )
                );
                return null;
            });
        }
    }

    /**
     * Suggests potential friends based on the "friend of a friend" pattern.
     *
     * @param userId identifier of the user
     * @return list of suggested user names
     */
    public List<String> suggestFriendsNames(long userId) {
        String cypher = """
            MATCH (me:User {id: $id})-[:MET]-(friend)-[:MET]-(fof:User)
            WHERE NOT (me)-[:MET]-(fof) AND me <> fof
            RETURN DISTINCT fof.name AS suggestName
            """;
        try (Session session = driver.session()) {
            return session.executeRead(tx ->
                    tx.run(cypher, Map.of("id", userId))
                            .list(r -> r.get("suggestName").asString())
            );
        }
    }

    /**
     * Closes the Neo4j driver.
     */
    @Override
    public void close() {
        driver.close();
    }
}