package app;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
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
 * Spring configuration class responsible for Bean definitions and Dependency Injection.
 */
@Configuration
public class AppConfig {

    private final DbConfig cfg = new DbConfig();

    /**
     * Configures the Elasticsearch client using settings from DbConfig.
     * @return a configured ElasticsearchClient
     */
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        RestClient restClient = RestClient.builder(HttpHost.create(cfg.elasticsearchUrl())).build();
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }

    // Repositories
    @Bean public UserRepositorySql userRepositorySql() { return new UserRepositorySql(cfg); }
    @Bean public UserInterestsRepositoryMongo userInterestsRepositoryMongo() { return new UserInterestsRepositoryMongo(cfg); }
    @Bean public MeetingRepositorySql meetingRepositorySql() { return new MeetingRepositorySql(cfg); }
    @Bean public MeetingRepositoryNeo4j meetingRepositoryNeo4j() { return new MeetingRepositoryNeo4j(cfg); }
    @Bean public PointRepositorySQL pointRepositorySQL() { return new PointRepositorySQL(cfg); }
    @Bean public PointsCacheRedis pointsCacheRedis() { return new PointsCacheRedis(cfg.redisHost(), cfg.redisPort()); }
    @Bean public MeetingSearchRepository meetingSearchRepo(ElasticsearchClient client) { return new MeetingSearchRepository(client); }
    @Bean public UserSearchRepository userSearchRepo(ElasticsearchClient client) { return new UserSearchRepository(client); }
    @Bean public RecommendationRepositoryNeo4j recommendationRepositoryNeo4j() {
        return new RecommendationRepositoryNeo4j(cfg);
    }
    @Bean public InterestSearchRepository interestSearchRepo(ElasticsearchClient client) { return new InterestSearchRepository(client); }

    // Services
    @Bean public SecurityService securityService() { return new SecurityService(cfg.redisHost(), cfg.redisPort()); }
    @Bean public PointService pointService(PointRepositorySQL r, PointsCacheRedis c) { return new PointService(r, c); }

    @Bean public SearchService searchService(MeetingSearchRepository ms, UserSearchRepository us, InterestSearchRepository in) { 
        return new SearchService(ms, us, in); 
    }
    @Bean public RecommendationService recommendationService(RecommendationRepositoryNeo4j r, UserInterestsRepositoryMongo m) { 
        return new RecommendationService(r); 
    }
    @Bean
    public UserService userService(
        UserRepositorySql userRepo,
        UserInterestsRepositoryMongo interestsRepo,
        SearchService searchService,
        RecommendationService recommendationService
    ) {
        return new UserService(userRepo, interestsRepo, searchService, recommendationService);
    }    
    @Bean public MeetingService meetingService(MeetingRepositorySql s, MeetingRepositoryNeo4j n, PointService p, UserService u, SearchService searchService) {
        return new MeetingService(s, n, p, u, searchService);   
    }
}