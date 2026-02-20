package recommendation;

import java.util.List;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import static org.neo4j.driver.Values.parameters;

import app.DbConfig;

/**
 * Neo4j repository dedicated to advanced recommendations.
 *
 * Goal:
 * - store user interests as graph edges (:User)-[:HAS_INTEREST]->(:Interest)
 * - compute recommendations using graph traversal (friends-of-friends)
 * and interest overlap (common interests).
 *
 * This repository contains only persistence/query logic:
 * no business decisions (limits, fallback, formatting).
 */
public final class RecommendationRepositoryNeo4j implements AutoCloseable {

    /** * Neo4j driver (session factory) used to communicate with the database. 
     */
    private final Driver driver;

    /**
     * Creates a new Neo4j repository using application DbConfig.
     * Assumes DbConfig exposes Neo4j connection info.
     *
     * @param cfg the database configuration object
     */
    public RecommendationRepositoryNeo4j(DbConfig cfg) {
        this.driver = GraphDatabase.driver(
                cfg.neo4jUri(),
                AuthTokens.basic(cfg.neo4jUser(), cfg.neo4jPassword())
        );
    }

    /**
     * Ensures that a User node exists in the graph.
     *
     * @param userId user id
     */
    public void ensureUserNode(long userId) {
        String cypher = """
                MERGE (:User {id: $userId})
                """;
        try (Session session = driver.session()) {
            session.executeWrite(tx -> {
                tx.run(cypher, parameters("userId", userId)).consume();
                return null;
            });
        }
    }

    /**
     * Replaces the list of interests for a given user in Neo4j.
     *
     * Why "replace" instead of "add"?
     * - keeps Neo4j consistent with MongoDB (source of truth for interests)
     * - simplifies the model (no need to track removed interests separately)
     *
     * @param userId    user id
     * @param interests list of interests (strings)
     */
    public void replaceUserInterests(long userId, List<String> interests) {
        String cypher = """
                MERGE (u:User {id: $userId})
                WITH u
                OPTIONAL MATCH (u)-[r:HAS_INTEREST]->(:Interest)
                DELETE r
                WITH u
                UNWIND $interests AS name
                MERGE (i:Interest {name: name})
                MERGE (u)-[:HAS_INTEREST]->(i)
                """;

        try (Session session = driver.session()) {
           session.executeWrite(tx -> {
                tx.run(cypher, parameters(
                        "userId", userId,
                        "interests", interests
                )).consume();
                return null;
            });
        }
    }

    /**
     * Advanced recommendation logic using Cypher.
     * - friends-of-friends (distance 2 on MET relationships)
     * - excludes self
     * - excludes users already directly connected by MET
     * - scores candidates based on number of common interests
     *
     * Returns a ranked list of recommended user ids.
     *
     * @param userId current user
     * @param limit  max number of results
     * @return list of recommended user ids
     */
    public List<Long> recommendFriendsOfFriendsWithInterests(long userId, int limit) {
        String cypher = """
                MATCH (me:User {id: $userId})
                MATCH (me)-[:MET]->(:User)-[:MET]->(cand:User)
                WHERE cand.id <> $userId
                  AND NOT (me)-[:MET]->(cand)
                OPTIONAL MATCH (me)-[:HAS_INTEREST]->(i:Interest)<-[:HAS_INTEREST]-(cand)
                WITH cand, count(i) AS commonInterests
                RETURN cand.id AS id
                ORDER BY commonInterests DESC, id ASC
                LIMIT $limit
                """;

        try (Session session = driver.session()) {
            return session.executeRead(tx ->
                    tx.run(cypher, parameters("userId", userId, "limit", limit))
                            .list(r -> r.get("id").asLong())
            );
        }
    }

    /**
     * Closes the Neo4j driver connection.
     */
    @Override
    public void close() {
        driver.close();
    }
}