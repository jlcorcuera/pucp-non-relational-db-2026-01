package pe.edu.pucp.msc.inf.connectors;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisConnectionManager {

    private static final RedisConnectionManager instance;

    private final String REDIS_HOST = "localhost";
    private final Integer REDIS_PORT = 6379;
    private final JedisPool pool;

    static {
        instance = new RedisConnectionManager();
    }

    public static RedisConnectionManager getInstance() {
        return instance;
    }

    private RedisConnectionManager() {
        this.pool = new JedisPool(REDIS_HOST, REDIS_PORT);
    }

    public Jedis getConnection() {
        return this.pool.getResource();
    }

    public static Jedis getConnectionStatic() {
        return getInstance().pool.getResource();
    }

    public void closePool(){
        if (!pool.isClosed()){
            pool.close();
        }
    }

}
