package search;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;

/**
 * Elasticsearch repository for managing and searching standalone interests.
 */
public class InterestSearchRepository {

    /** The Elasticsearch client used for operations. */
    private final ElasticsearchClient client;
    
    /** The index name for interest storage. */
    private static final String INDEX = "interests_search";

    /**
     * Constructor.
     * @param client Elasticsearch client
     */
    public InterestSearchRepository(ElasticsearchClient client) {
        this.client = client;
    }

    /**
     * Indexes a single interest string as a document.
     * @param interest the interest name to index
     */
    public void indexInterest(String interest) {
        try {
            client.index(i -> i
                .index(INDEX)
                .id(interest)
                .document(Map.of("name", interest))
            );
        } catch (Exception e) {
            System.err.println("Interest indexing skipped");
        }
    }

    /**
     * Performs a fuzzy search on interest names.
     * @param query the search term
     * @return a list of matching interest names
     * @throws IOException if the request fails
     */
    public List<String> fuzzySearchInterest(String query) throws IOException {
        SearchResponse<Map> response = client.search(s -> s
            .index(INDEX)
            .query(q -> q
                .fuzzy(f -> f
                    .field("name")
                    .value(query)
                    .fuzziness("AUTO")
                )
            ), Map.class);

        return response.hits().hits().stream()
            .map(h -> (String) h.source().get("name"))
            .toList();
    }
}