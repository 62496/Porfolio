package search;

import java.util.ArrayList;
import java.util.List;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

/**
 * Elasticsearch repository for indexing and searching users.
 *
 * Responsibilities:
 * - index a user document (id, username, interests)
 * - search users by username and/or interests using fuzzy matching
 */
public class UserSearchRepository {

    /** The Elasticsearch client used for operations. */
    private final ElasticsearchClient client;
    
    /** The index name for user storage. */
    private static final String INDEX = "users_search";

    /**
     * Creates the repository with an already-configured Elasticsearch client.
     *
     * @param client Elasticsearch client
     */
    public UserSearchRepository(ElasticsearchClient client) {
        this.client = client;
    }

    /**
     * Indexes (creates or overwrites) a user document in Elasticsearch.
     *
     * @param id        user identifier
     * @param username  username
     * @param interests list of interests for the user
     */
    public void indexUser(long id, String username, List<String> interests) {
        try {
            client.index(i -> i
                    .index(INDEX)
                    .id(String.valueOf(id))
                    .document(new UserSearchDocument(id, username, interests))
            );
        } catch (Exception e) {
            System.err.println("Elastic index user failed");
        }
    }

    /**
     * Searches users matching the query against username and interests fields.
     *
     * @param query user input query
     * @return list of matching usernames
     */
    public List<String> searchUsers(String query) {
        try {
            SearchResponse<UserSearchDocument> response =
                    client.search(s -> s
                                    .index(INDEX)
                                    .query(q -> q
                                            .multiMatch(m -> m
                                                    .fields("username", "interests")
                                                    .query(query)
                                                    .fuzziness("AUTO")
                                            )
                                    ),
                            UserSearchDocument.class
                    );

            List<String> usernames = new ArrayList<>();
            for (Hit<UserSearchDocument> hit : response.hits().hits()) {
                usernames.add(hit.source().username());
            }
            return usernames;

        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Returns user recommendations based on shared interests using a boolean query.
     *
     * @param userId    current user id (to exclude from results)
     * @param interests current user interests
     * @return list of recommended user ids
     */
    public List<Long> getRecommendationsByInterests(long userId, List<String> interests) {

        if (interests == null || interests.isEmpty()) {
            return List.of();
        }

        try {
            SearchResponse<UserSearchDocument> response =
                client.search(s -> s
                    .index(INDEX)
                    .query(q -> q
                        .bool(b -> {

                            // Exclude current user
                            b.mustNot(mn -> mn
                                .term(t -> t.field("userId").value(userId))
                            );

                            // Fuzzy match EACH interest independently
                            for (String interest : interests) {
                                b.should(sh -> sh
                                    .match(m -> m
                                        .field("interests")
                                        .query(interest)
                                        .fuzziness("AUTO")
                                    )
                                );
                            }

                            b.minimumShouldMatch("1");
                            return b;
                        })
                    ),
                    UserSearchDocument.class
                );

            List<Long> result = new ArrayList<>();
            for (Hit<UserSearchDocument> hit : response.hits().hits()) {
                result.add(hit.source().userId());
            }

            return result;

        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Clears all user documents from the index.
     */
    public void resetIndex() {
        try {
            client.deleteByQuery(d -> d
                    .index(INDEX)
                    .query(q -> q
                            .matchAll(m -> m)
                    )
            );
        } catch (Exception e) {
            System.err.println("Elastic reset index failed");
        }
    }
}