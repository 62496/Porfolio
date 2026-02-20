package search;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;

/**
 * Elasticsearch repository for indexing and searching meetings.
 *
 * This class is responsible only for Elasticsearch access:
 * - indexing meeting documents (meetingId + interest)
 * - fuzzy search on the meeting interest field
 */
public class MeetingSearchRepository {
    
    /** The Elasticsearch client used for operations. */
    private final ElasticsearchClient client;
    
    /** The index name for meeting storage. */
    private static final String INDEX = "meetings_search";

    /**
     * Creates the repository with an already-configured Elasticsearch client.
     *
     * @param client Elasticsearch client
     */
    public MeetingSearchRepository(ElasticsearchClient client) {
        this.client = client;
    }

    /**
     * Indexes a meeting document with participants and interest details.
     * @param meetingId unique identifier of the meeting
     * @param interest  the theme of the meeting
     * @param userA     first user's name
     * @param userB     second user's name
     */
    public void indexMeeting(long meetingId, String interest, String userA, String userB) {
        try {
            client.index(i -> i
                .index(INDEX)
                .id(String.valueOf(meetingId))
                .document(new MeetingSearchDocument(meetingId, userA, userB, interest))
            );
        } catch (Exception e) {
            System.err.println("Meeting indexing skipped");
        }
    }

    /**
     * Performs a fuzzy search across interest and participant name fields.
     * @param query search text
     * @return a list of formatted meeting summary strings
     * @throws IOException if the request fails
     */
    public List<String> fuzzySearchMeetings(String query) throws IOException {
        SearchResponse<Map> response = client.search(s -> s
            .index(INDEX)
            .query(q -> q
                .multiMatch(m -> m
                    .fields("interest", "userA", "userB")
                    .query(query)
                    .fuzziness("AUTO")
                )
            ), Map.class);

        return response.hits().hits().stream()
            .map(h ->
                "Interest: " + h.source().get("interest") +
                " | " + h.source().get("userA") +
                " - " + h.source().get("userB")
            )
            .toList();
    }

    /**
     * Deletes the meeting index from Elasticsearch.
     */
    public void resetIndex() {
        try {
            client.indices().delete(d -> d.index(INDEX));
        } catch (Exception e) {
            // Ignore
        }
    }
}