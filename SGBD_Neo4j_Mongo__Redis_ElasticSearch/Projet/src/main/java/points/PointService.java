package points;

/**
 * Business service for sociability points management.
 *
 * This service coordinates point persistence and caching.
 * Each point attribution is linked to a meeting.
 */
public final class PointService {

    /**
     * Persistent repository for points.
     */
    private final PointRepositorySQL repo;
    
    /**
     * Cache layer for aggregated totals.
     */
    private final PointsCacheRedis cache;

    /**
     * Constructor.
     *
     * @param repo SQL repository for points
     * @param cache Redis cache for aggregated totals
     */
    public PointService(PointRepositorySQL repo, PointsCacheRedis cache) {
        this.repo = repo;
        this.cache = cache;
    }

    /**
     * Grants points to a user for a given meeting and invalidates the cache.
     *
     * @param userId identifier of the user
     * @param meetingId identifier of the meeting
     * @param amount number of points granted
     */
    public void addPoints(long userId, long meetingId, int amount) {
        repo.addPoints(userId, meetingId, amount);
        cache.invalidate(userId);
    }

    /**
     * Retrieves the total number of points for a user, using cache if available.
     *
     * @param userId identifier of the user
     * @return total number of points
     */
    public int getPoints(long userId) {

        Integer cached = cache.getCachedTotal(userId);
        if (cached != null) {
            return cached;
        }

        int total = repo.getTotalPoints(userId);
        cache.cacheTotal(userId, total);
        return total;
    }

    /**
     * Resets point data in both the repository and the cache for demo purposes.
     */
    public void resetDemo() {
        repo.reset();
        cache.invalidateAll();
    }
}