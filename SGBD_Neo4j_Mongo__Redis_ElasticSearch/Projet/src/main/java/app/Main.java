package app;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import meeting.MeetingRepositoryNeo4j;
import meeting.MeetingRepositorySql;
import meeting.MeetingService;
import points.PointRepositorySQL;
import points.PointService;
import points.PointsCacheRedis;
import recommendation.RecommendationRepositoryNeo4j;
import recommendation.RecommendationService;
import search.InterestSearchRepository;
import search.MeetingSearchRepository;
import search.SearchService;
import search.UserSearchRepository;
import security.SecurityService;
import user.UserInterestsRepositoryMongo;
import user.UserRepositorySql;
import user.UserService;

/**
 * CLI entry point for the project demo.
 *
 * Responsibilities:
 * - build and wire repositories/services (manual dependency injection)
 * - provide a text-based menu to interact with the system
 *
 * Note: this class is intentionally simple and is used as a demo interface.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        DbConfig cfg = new DbConfig();

        // Manual configuration of Elasticsearch client
        RestClient restClient = RestClient.builder(
                HttpHost.create(cfg.elasticsearchUrl())
        ).build();

        ElasticsearchTransport transport =
                new RestClientTransport(restClient, new JacksonJsonpMapper());

        ElasticsearchClient esClient = new ElasticsearchClient(transport);

        // Repositories initialization
        MeetingSearchRepository meetingSearchRepo = new MeetingSearchRepository(esClient);
        UserSearchRepository userSearchRepo = new UserSearchRepository(esClient);
        UserInterestsRepositoryMongo mongo = new UserInterestsRepositoryMongo(cfg);
        MeetingRepositoryNeo4j neo4j = new MeetingRepositoryNeo4j(cfg);
        MeetingRepositorySql meetingRepositorySql = new MeetingRepositorySql(cfg);
        RecommendationRepositoryNeo4j recommendationRepo = new RecommendationRepositoryNeo4j(cfg);
        InterestSearchRepository interestSearchRepo = new InterestSearchRepository(esClient);
        PointsCacheRedis pointsCache = new PointsCacheRedis(cfg.redisHost(), cfg.redisPort());

        // Services initialization
        SecurityService security = new SecurityService(cfg.redisHost(), cfg.redisPort());
        UserRepositorySql userSql = new UserRepositorySql(cfg);
        PointRepositorySQL pointSql = new PointRepositorySQL(cfg);
        PointService pointService = new PointService(pointSql, pointsCache);
        RecommendationService recommendationService = new RecommendationService(recommendationRepo);
        SearchService searchService = new SearchService(meetingSearchRepo, userSearchRepo, interestSearchRepo);

        UserService userService = new UserService(userSql, mongo, searchService, recommendationService);
        MeetingService meetingService = new MeetingService(
                meetingRepositorySql, neo4j, pointService, userService, searchService
        );

        Scanner sc = new Scanner(System.in);
        boolean running = true;

        // Interaction loop
        while (running) {
            printMenu();
            System.out.print("> ");
            String choice = sc.nextLine();

            try {
                switch (choice) {
                    case "1" -> createUser(sc, userService, searchService);
                    case "2" -> listUsers(userService);
                    case "3" -> addInterests(sc, userService, searchService);
                    case "4" -> listInterests(userService);
                    case "5" -> simulateMeeting(sc, userService, meetingService, searchService);
                    case "6" -> listMeetingsForUser(sc, userService, meetingService);
                    case "7" -> showPoints(sc, userService, pointService);
                    case "8" -> searchUsers(sc, searchService);
                    case "9" -> searchInterest(sc, searchService);
                    case "10" -> recommendations(sc, userService, meetingService);
                    case "11" -> recommendUsersByInterests(sc, userService, searchService);
                    case "0" -> running = false;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        System.out.println("Program finished.");
    }

    /**
     * Prints the main CLI menu.
     */
    private static void printMenu() {
        System.out.println("""
        ================================
        HUMAN MEETINGS SYSTEM
        ================================
        1. Create a user
        2. List users
        3. Add interests to a user
        4. List existing interests
        5. Simulate a meeting
        6. View meetings for a user
        7. Show points for a user
        8. Search users by username or interest (ElasticSearch)
        9. Search interests meeting (ElasticSearch)
        10. Meeting recommendations based on friends
        11. Meeting recommendations based on interest
        0. Quit
        """);
    }

    /**
     * CLI action: create a user.
     * @param sc scanner used to read user input
     * @param userService user service
     * @param searchService search service to index the new user
     */
    private static void createUser(Scanner sc, UserService userService, SearchService searchService) {
        System.out.print("Username: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        // Prevent duplicates
        Long existingId = userService.findUserIdByUsername(name);
        if (existingId != null) {
            System.out.println("Username already exists. Existing id: " + existingId);
            return;
        }

        long id = userService.createUser(name);
        System.out.println("User created with id " + id);

        // Index user for search (Elasticsearch)
        searchService.indexUser(id, userService.findUsernameById(id), userService.listUserInterests(id));
    }

    /**
     * CLI action: add interests to a user.
     * @param sc scanner
     * @param userService service to update interests
     * @param searchService service to re-index the user
     */
    private static void addInterests(Scanner sc, UserService userService, SearchService searchService) {
        long id = readLong(sc, "User id: ");

        if (!userService.existsUser(id)) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("Current user interests list: ");
        listUserInterests(id, userService);

        String raw = readNonEmpty(sc, "Interests (comma separated): ");
        List<String> interests =
                List.of(raw.split(","))
                        .stream()
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();

        userService.addInterests(id, interests);

        // Re-index user because interests changed
        searchService.indexUser(id, userService.findUsernameById(id), userService.listUserInterests(id));

        System.out.println("Interests added.");
    }

    /**
     * CLI action: simulate a meeting between two users.
     */
    private static void simulateMeeting(Scanner sc, UserService userService, MeetingService meetingService, SearchService searchService) throws Exception {
        System.out.println("Available user ids:");
        for (Long id : userService.listUsers()) {
            System.out.println("- " + id);
        }

        long a = readLong(sc, "User A id: ");
        long b = readLong(sc, "User B id: ");

        if (!userService.existsUser(a) || !userService.existsUser(b)) {
            System.out.println("User A or User B not found.");
            return;
        }
        if (a == b) {
            System.out.println("A meeting requires two different users.");
            return;
        }

        var interests = userService.listAllInterests();
        if (!interests.isEmpty()) {
            System.out.println("Existing interests:");
            for (String inte : interests) {
                System.out.println("- " + inte);
            }
        }

        String interest = readNonEmpty(sc, "Meeting interest: ");

        Long meetingId = meetingService.simulateMeeting(a, b, interest);
        searchService.indexMeeting(meetingId, interest, userService.findUsernameById(a), userService.findUsernameById(b));
        System.out.println("Meeting saved.");
    }

    /** CLI action: list meetings for a given user. */
    private static void listMeetingsForUser(Scanner sc, UserService userService, MeetingService meetingService) {
        long uid = readLong(sc, "User id: ");

        if (!userService.existsUser(uid)) {
            System.out.println("User not found.");
            return;
        }

        meetingService.listMeetingsForUser(uid).forEach(System.out::println);
    }

    /** CLI action: show total points for a user. */
    private static void showPoints(Scanner sc, UserService userService, PointService pointService) {
        long id = readLong(sc, "User id: ");

        if (!userService.existsUser(id)) {
            System.out.println("User not found.");
            return;
        }

        int points = pointService.getPoints(id);
        System.out.println("Total points: " + points);
    }

    /** CLI action: search users via Elasticsearch. */
    private static void searchUsers(Scanner sc, SearchService searchService) {
        String q = readNonEmpty(sc, "Search: ");
        System.out.println("Results: " + searchService.searchUsers(q));
    }

    /** CLI action: fuzzy search interests via Elasticsearch. */
    private static void searchInterest(Scanner sc, SearchService searchService) throws IOException {
        String q = readNonEmpty(sc, "Search: ");
        System.out.println("Results: " + searchService.fuzzySearchInterest(q));
    }

    /** CLI action: show recommendations based on graph traversal. */
    private static void recommendations(Scanner sc, UserService userService, MeetingService meetingService) {
        long id = readLong(sc, "User id: ");

        if (!userService.existsUser(id)) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("Suggestions: " + meetingService.getRecommendations(id));
    }

    /** CLI action: recommend users based on interest overlap. */
    private static void recommendUsersByInterests(Scanner sc, UserService userService, SearchService searchService) {
        long id = readLong(sc, "User id: ");

        if (!userService.existsUser(id)) {
            System.out.println("User not found.");
            return;
        }

        List<String> interests = userService.listUserInterests(id);
        List<Long> recommended = searchService.recommendUsersByInterests(id, interests);

        System.out.println("Recommendations: " + recommended);
    }

    /** CLI action: list all users. */
    private static void listUsers(UserService userService) {
        System.out.println("Existing users:");
        userService.listUsersReadable().forEach(System.out::println);
    }

    /** CLI action: list all existing interests. */
    private static void listInterests(UserService userService) {
        System.out.println("Existing interests:");
        for (String i : userService.listAllInterests()) {
            System.out.println("- " + i);
        }
    }

    /** Helper to list interests for a specific user ID. */
    private static void listUserInterests(Long uid, UserService userService) {
        for (String i : userService.listUserInterests(uid)) {
            System.out.println("- " + i);
        }
    }

    // SAFE INPUT HELPERS

    private static long readLong(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readNonEmpty(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Empty value not allowed.");
        }
    }
}