package security;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Simple Redis-based rate-limiting service (demo purpose).
 *
 * Uses an incrementing counter with an expiration (time window) per (action, userId).
 * Example:
 * - key = "limit:{action}:{userId}"
 * - INCR increments request count
 * - EXPIRE defines the time window (in seconds)
 */
public class SecurityService {
    
    /**
     * Connection pool for Redis operations.
     */
    private final JedisPool pool;

    /**
     * Creates the service and initializes a Redis connection pool.
     *
     * @param host Redis host
     * @param port Redis port
     */
    public SecurityService(String host, int port) {
        this.pool = new JedisPool(host, port);
    }

    /** * Checks whether the user is allowed to perform the given action under the rate limit.
     * * This implementation uses a "Fixed Window Counter" algorithm via Redis:
     * 1. Increments the key for the user/action.
     * 2. Sets an expiration if it's the first request in the window.
     * 3. Blocks any request exceeding the threshold.
     *
     * Window: 10 seconds
     * Limit: 1 request within the window
     *
     * @param action action name
     * @param userId user identifier
     * @return true if allowed, false if blocked by the rate limit
     */
    public boolean isAllowed(String action, long userId) {
        try (Jedis jedis = pool.getResource()) {
            String key = "limit:" + action + ":" + userId;
            long count = jedis.incr(key);
            
            // Set expiration on first hit of the window
            if (count == 1) jedis.expire(key, 10);
            
            return count <= 1;
        } catch (Exception e) {
            // Demo-friendly fallback: if Redis is down, allow the action instead of failing
            System.err.println("Redis unavailable (rate limit disabled): " + e.getMessage());
            return true;
        }
    }

}