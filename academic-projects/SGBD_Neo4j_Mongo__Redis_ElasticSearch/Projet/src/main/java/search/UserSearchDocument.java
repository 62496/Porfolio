package search;

import java.util.List;

/**
 * Elasticsearch document model for user search.
 *
 * This record represents the data indexed in Elasticsearch for a user:
 * - userId: unique identifier
 * - username: display name / login
 * - interests: list of user interests
 *
 * @param userId    user identifier
 * @param username  username
 * @param interests interests associated with the user
 */
public record UserSearchDocument(
        long userId,
        String username,
        List<String> interests
) {}