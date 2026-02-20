package recommendation;

import java.util.List;

/**
 * Business service for advanced recommendations.
 *
 * Responsibilities:
 * - synchronize user interests into Neo4j (graph projection)
 * - compute advanced recommendations (friends-of-friends + interest overlap)
 *
 * Important:
 * - MongoDB remains the source of truth for interests.
 * - Neo4j stores a derived graph structure for traversal/analytics.
 */
public final class RecommendationService {

    /** * Neo4j repository for recommendation queries and interest graph. 
     */
    private final RecommendationRepositoryNeo4j neo4j;


    /**
     * Constructor.
     *
     * @param neo4j Neo4j recommendation repository
     */
    public RecommendationService(RecommendationRepositoryNeo4j neo4j) {
        this.neo4j = neo4j;
    }

    /**
     * Ensures the user exists in Neo4j and synchronizes interests from MongoDB.
     *
     * This method should be called:
     * - after user creation (to create the node)
     * - after interests update (to reflect new interests)
     *
     * @param userId    user id
     * @param interests current list of interests to synchronize
     */
    public void syncUserInterestsToNeo4j(long userId, List<String> interests) {
        neo4j.ensureUserNode(userId);
        neo4j.replaceUserInterests(userId, interests);
    }

    /**
     * Returns advanced recommendations based on graph traversal and common interests.
     * Uses a "friend-of-a-friend" pattern scored by shared interest nodes.
     *
     * @param userId current user id
     * @param limit  max number of results
     * @return list of recommended user ids, or an empty list if limit is invalid
     */
    public List<Long> advancedGraphRecommendations(long userId, int limit) {
        if (limit <= 0) {
            return List.of();
        }
        return neo4j.recommendFriendsOfFriendsWithInterests(userId, limit);
    }
}