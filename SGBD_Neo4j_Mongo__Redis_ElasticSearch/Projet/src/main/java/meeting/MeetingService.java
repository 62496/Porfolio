package meeting;

import java.io.IOException;
import java.util.List;

import points.PointService;
import search.SearchService;
import user.UserService;

/**
 * Business service for meeting management.
 *
 * This class contains the business logic related to meetings
 * and coordinates multiple persistence layers.
 */
public final class MeetingService {

    /**
     * SQL repository for persistent storage.
     */
    private final MeetingRepositorySql sql;
    
    /**
     * Neo4j repository for graph-based relationships.
     */
    private final MeetingRepositoryNeo4j neo4j;
    
    /**
     * Service used to manage user points.
     */
    private final PointService pointService;
    
    /**
     * Service used to retrieve user information.
     */
    private final UserService userService;
    
    /**
     * Service used to index meetings for search.
     */
    private final SearchService searchService;

    /**
     * Constructor.
     *
     * @param sql SQL repository
     * @param neo4j Neo4j repository
     * @param pointService point service
     * @param userService user service
     * @param searchService search service
     */
    public MeetingService(MeetingRepositorySql sql,
                          MeetingRepositoryNeo4j neo4j,
                          PointService pointService,
                          UserService userService, 
                          SearchService searchService) {
        this.sql = sql;
        this.neo4j = neo4j;
        this.pointService = pointService;
        this.userService = userService;
        this.searchService = searchService;
    }

    /**
     * Resets meeting-related data for demo purposes.
     */
    public void resetDemo() {
        sql.resetDemo();
        neo4j.reset();
    }

    /**
     * Simulates a meeting between two users.
     * This coordinates SQL storage, point calculation, graph recording, and indexing.
     *
     * @param userA first user identifier
     * @param userB second user identifier
     * @param interest meeting interest
     * @return meeting identifier
     * @throws IOException if search indexing fails
     */
    public Long simulateMeeting(long userA, long userB, String interest) throws IOException {
        
        long meetingId = sql.createMeeting(userA, userB, interest);

        String nameA = userService.findUsernameById(userA);
        String nameB = userService.findUsernameById(userB);
        List<String> interestsA = userService.listUserInterests(userA);
        List<String> interestsB = userService.listUserInterests(userB);

        // Calculate points based on common interests
        int points = computePointsFromCommonInterests(interestsA, interestsB);

        pointService.addPoints(userA, meetingId, points);
        pointService.addPoints(userB, meetingId, points);

        neo4j.recordMeeting(userA, userB, interest, nameA, nameB);
        
        searchService.indexMeeting(meetingId, interest, nameA, nameB);

        return meetingId;
    }

    /**
     * Lists all meetings for a given user.
     *
     * @param userId user identifier
     * @return list of meetings
     */
    public List<String> listMeetingsForUser(long userId) {
        return sql.findMeetingsForUser(userId);
    }

    /**
     * Retrieves user recommendations based on graph relationships.
     *
     * @param userId user identifier
     * @return list of recommended user names
     */
    public List<String> getRecommendations(long userId) {
        return neo4j.suggestFriendsNames(userId);
    }

    /**
     * Computes reward points based on the number of shared interests.
     * * @param interestsA list of interests for user A
     * @param interestsB list of interests for user B
     * @return the number of points to award
     */
    private int computePointsFromCommonInterests(List<String> interestsA, List<String> interestsB) {

        if (interestsA == null || interestsB == null) {
            return 1;
        }

        long commonCount = interestsA.stream()
                .filter(interestsB::contains)
                .count();

        if (commonCount == 0) return 1;
        if (commonCount <= 3) return 3;
        if (commonCount <= 6) return 6;
        return 10;
    }
}