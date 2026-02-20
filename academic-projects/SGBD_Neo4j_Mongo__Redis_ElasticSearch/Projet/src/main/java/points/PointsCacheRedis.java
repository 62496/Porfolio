package points;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis-based cache for user point totals.
 *
 * This class provides a short-lived cache layer in front of the
 * relational database in order to reduce repeated aggregation queries.
 */
public final class PointsCacheRedis {

    /**
     * Connection pool for Redis.
     */
    private final JedisPool pool;
    
    /**
     * Time-to-live for cache entries in seconds.
     */
    private static final int TTL_SECONDS = 60;

    /**
     * Constructor.
     *
     * @param host Redis host
     * @param port Redis port
     */
    public PointsCacheRedis(String host, int port) {
        this.pool = new JedisPool(host, port);
    }

    /**
     * Retrieves the cached total number of points for a user.
     *
     * @param userId user identifier
     * @return cached total or null if not present
     */
    public Integer getCachedTotal(long userId) {
        try (Jedis jedis = pool.getResource()) {
            String value = jedis.get(key(userId));
            return value != null ? Integer.parseInt(value) : null;
        }
    }

    /**
     * Stores the total number of points for a user in the cache.
     *
     * @param userId user identifier
     * @param total total number of points
     */
    public void cacheTotal(long userId, int total) {
        try (Jedis jedis = pool.getResource()) {
            jedis.setex(key(userId), TTL_SECONDS, String.valueOf(total));
        }
    }

    /**
     * Invalidates the cached total for a user.
     *
     * @param userId user identifier
     */
    public void invalidate(long userId) {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(key(userId));
        }
    }

    /**
     * Invalidates the cached total for all users using a pattern match.
     */
    public void invalidateAll() {
        try (Jedis jedis = pool.getResource()) {
            String pattern = "points:total:*";
            for (String key : jedis.keys(pattern)) {
                jedis.del(key);
            }
        }
    }

    /**
     * Generates a Redis key for a specific user's point total.
     * * @param userId user identifier
     * @return formatted Redis key string
     */
    private String key(long userId) {
        return "points:total:" + userId;
    }
}