package app;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import meeting.MeetingService;
import points.PointService;
import recommendation.RecommendationService;
import search.SearchService;
import security.SecurityService;
import user.UserService;

/**
 * REST controller for managing users, meetings, and recommendations.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class MeetingController {

    private final UserService userService;
    private final MeetingService meetingService;
    private final SearchService searchService;
    private final PointService pointService;
    private final SecurityService securityService;
    private final RecommendationService recommendationService;

    public MeetingController(UserService us, MeetingService ms, SearchService ss, PointService ps, SecurityService securityService,
        RecommendationService recommendationService
    ) {
        this.userService = us;
        this.meetingService = ms;
        this.searchService = ss;
        this.pointService = ps;
        this.securityService = securityService;
        this.recommendationService = recommendationService;
    }

    /** Resets all demo data in the databases. */
    @GetMapping("/reset")
    public void resetDemo() {
        meetingService.resetDemo();
        userService.resetDemo();
        pointService.resetDemo();
        searchService.resetIndex();
    }

    /** Creates a new user with a username. */
    @PostMapping("/users")
    public String createUser(@RequestParam String username) {
        long id = userService.createUser(username);
        return "User created with id: " + id;
    }

    /** Returns a list of all users. */
    @GetMapping("/users")
    public List<String> listUsers() {
        return userService.listUsersReadable();
    }

    /** Adds interests to a specific user. */
    @PostMapping("/users/{id}/interests")
    public ResponseEntity<String> addInterests(@PathVariable long id, @RequestBody List<String> interests) {
        if (!userService.existsUser(id)) {
            String msg = "User with id " + id + " does not exist";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
        userService.addInterests(id, interests);
        return ResponseEntity.ok("Interests added");
    }

    /** Simulates a meeting between two users with rate limiting. */
    @PostMapping("/meetings/simulate")
    public ResponseEntity<String> simulate(@RequestParam long userA, @RequestParam long userB, @RequestParam String interest) throws Exception {
        // Rate limiting: prevents meeting simulation spam
        String rateLimitKey = "simulateMeeting:" + userA;

        if (!securityService.isAllowed(rateLimitKey, userB)) {
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many meeting simulations. Please wait a moment.");
        }
        if (!userService.existsUser(userA) || !userService.existsUser(userB)) {
            String msg = "User with id " + userA + " or " + userB + " does not exist";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
        Long meetingId = meetingService.simulateMeeting(userA, userB, interest);
        return ResponseEntity.ok("Meeting saved with ID: " + meetingId);
    }

    /** Returns the total points of a user. */
    @GetMapping("/users/{id}/points")
    public int getPoints(@PathVariable long id) {
        return pointService.getPoints(id);
    }

    /** Searches for users via Elasticsearch. */
    @GetMapping("/search/users")
    public List<String> search(@RequestParam String q) {
        return searchService.searchUsers(q);
    }

    /** Gets graph-based recommendations from Neo4j. */
    @GetMapping("/recommendations/graph/{id}")
    public List<String> getGraphRecs(@PathVariable long id) {
        return meetingService.getRecommendations(id);
    }

    /** Gets interest-based recommendations from Elasticsearch. */
    @GetMapping("/recommendations/interests/{id}")
    public List<Long> getInterestRecs(@PathVariable long id) {
        return searchService.recommendUsersByInterests(id, userService.listUserInterests(id));
    }

    /** Searches for interests via Elasticsearch (fuzzy). */
    @GetMapping("/search/interests")
    public List<String> searchInterests(@RequestParam String q) throws Exception {
        return searchService.fuzzySearchInterest(q);
    }

    /** Searches for meetings via Elasticsearch (fuzzy). */
    @GetMapping("/search/meetings")
        public ResponseEntity<List<String>> searchMeetings(@RequestParam String q) throws Exception {
            return ResponseEntity.ok(searchService.fuzzySearchMeetings(q));
    }

    /** Lists meetings for a specific user. */
    @GetMapping("/users/{id}/meetings")
    public List<String> getUserMeetings(@PathVariable Long id) {
        return meetingService.listMeetingsForUser(id);
    }

    /** Lists all unique interests in the system. */
    @GetMapping("/interests")
    public List<String> listAllInterests() {
        return userService.listAllInterests();
    }

    /** Advanced recommendations combining graph traversal and interest overlap. */
    @GetMapping("/recommendations/graph/advanced/{id}")
    public List<Long> getAdvancedGraphRecs(@PathVariable long id) {
        return recommendationService.advancedGraphRecommendations(id, 10);
    }

    /** Lists interests for a specific user. */
    @GetMapping("/users/{id}/interests")
    public List<String> listUserInterests(@PathVariable long id) {
        return userService.listUserInterests(id);
    }
}