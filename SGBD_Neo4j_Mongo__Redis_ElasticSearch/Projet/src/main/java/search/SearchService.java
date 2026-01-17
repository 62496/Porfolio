package search;

import java.io.IOException;
import java.util.List;

/**
 * Business service dedicated to search functionalities.
 *
 * This class centralizes all logic related to searching and indexing
 * data in Elasticsearch.
 *
 * It acts as an orchestrator between:
 * - MongoDB (source for user interests)
 * - Elasticsearch (full-text search engine)
 *
 * The service contains no direct persistence logic:
 * all operations pass through specialized repositories.
 */
public final class SearchService {

    /** Repository for user indexing and profile search. */
    private final UserSearchRepository userSearchRepo;

    /** Repository for meeting indexing and search. */
    private final MeetingSearchRepository meetingSearchRepo;

    /** Repository for interest-only indexing and search. */
    private final InterestSearchRepository interestSearchRepo;

    /**
     * Constructor for the search service.
     *
     * @param meetingSearchRepo  Elasticsearch repository for meetings
     * @param userSearchRepo     Elasticsearch repository for users
     * @param interestSearchRepo Elasticsearch repository for interests
     */
    public SearchService(
            MeetingSearchRepository meetingSearchRepo,
            UserSearchRepository userSearchRepo,
            InterestSearchRepository interestSearchRepo
    ) {
        this.meetingSearchRepo = meetingSearchRepo;
        this.userSearchRepo = userSearchRepo;
        this.interestSearchRepo = interestSearchRepo;
    }

    /**
     * Indexes a meeting in Elasticsearch for fuzzy searching.
     *
     * @param meetingId unique meeting identifier
     * @param interest  theme or interest associated with the meeting
     * @param userA     name of first participant
     * @param userB     name of second participant
     */
    public void indexMeeting(long meetingId, String interest, String userA, String userB) {
        meetingSearchRepo.indexMeeting(meetingId, interest, userA, userB);
    }

    /**
     * Performs a fuzzy search on meeting interests.
     *
     * @param input search text entered by the user
     * @return list of interests matching the search
     * @throws IOException if communication with Elasticsearch fails
     */
    public List<String> fuzzySearchMeetings(String input) throws IOException {
        return meetingSearchRepo.fuzzySearchMeetings(input);
    }

    /**
     * Indexes a user with their interests in Elasticsearch.
     *
     * @param userId    user identifier
     * @param username  username
     * @param interests list of user interests
     */
    public void indexUser(long userId, String username, List<String> interests) {
        userSearchRepo.indexUser(userId, username, interests);
    }

    /**
     * Searches for users based on a text query matching username or interests.
     *
     * @param query search text
     * @return list of matching usernames
     */
    public List<String> searchUsers(String query) {
        return userSearchRepo.searchUsers(query);
    }
        
    /**
     * Indexes a list of interests.
     * @param interests list of interest strings
     */
    public void indexInterest(List<String> interests) {
        for (String interest : interests) {
            interestSearchRepo.indexInterest(interest);
        }
    }

    /**
     * Searches for interests using fuzzy logic.
     * @param input search term
     * @return list of matching interest names
     * @throws IOException if communication fails
     */
    public List<String> fuzzySearchInterest(String input) throws IOException {
        return interestSearchRepo.fuzzySearchInterest(input);
    }

    /**
     * Recommends users based on shared interests using textual similarity.
     *
     * @param userId    identifier of the current user
     * @param interests list of interests of the current user
     * @return list of recommended user identifiers
     */
    public List<Long> recommendUsersByInterests(
            long userId,
            List<String> interests
    ) {
        return userSearchRepo.getRecommendationsByInterests(userId, interests);
    }

    /**
     * Resets indices for both users and meetings.
     */
    public void resetIndex() {
        userSearchRepo.resetIndex();
        meetingSearchRepo.resetIndex();
    }
}