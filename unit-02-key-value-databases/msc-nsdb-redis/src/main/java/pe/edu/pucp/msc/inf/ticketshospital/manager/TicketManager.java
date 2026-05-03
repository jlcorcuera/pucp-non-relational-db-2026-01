package pe.edu.pucp.msc.inf.ticketshospital.manager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.List;

public class TicketManager {

    private static final JedisPool pool;
    private static final String REDIS_HOST = "localhost";
    private static final Integer REDIS_PORT = 6379;
    private static final String COUNTER_KEY_NS = "hospital-tickets:counter";
    private static final Integer totalNumberOfTickets = 250;

    static {
        pool = new JedisPool(REDIS_HOST, REDIS_PORT);
        try (Jedis jedis = pool.getResource()) {
            jedis.set(COUNTER_KEY_NS, 0 + "");
        }
    }

    public static int getCurrentTicketNumber(){
        try (Jedis jedis = pool.getResource()) {
            return Integer.parseInt(jedis.get(COUNTER_KEY_NS));
        }
    }

    private TicketManager(){
    }

    public static long getTicketNumber(){
        try (Jedis jedis = pool.getResource()) {
            long ticketNumber = jedis.incr(COUNTER_KEY_NS);
            if (ticketNumber <= totalNumberOfTickets) {
                return ticketNumber;
            }
            return -1;
        }
    }

    public static void closePool(){
        if (!pool.isClosed()){
            pool.close();
        }
    }

}
