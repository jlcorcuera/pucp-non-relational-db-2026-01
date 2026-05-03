package pe.edu.pucp.msc.inf.ratelimiting;

import pe.edu.pucp.msc.inf.connectors.RedisConnectionManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;
import java.util.Calendar;

public class MyAPI {

    private static final MyAPI instance = new MyAPI();
    private static final String MY_APP_NS = "rate-limiting";
    private static final Integer ratePerMinute = 100;

    private MyAPI(){
    }

    public static MyAPI getInstance(){
        return instance;
    }

    private String getMinuteKeyInNS(int minute){
        return MY_APP_NS + ":minute:" + minute;
    }

    public void call(String threadName){
        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        try (Jedis jedis = RedisConnectionManager.getInstance().getConnection()) {
            String minuteKeyInNS = getMinuteKeyInNS(currentMinute);
            /*
                SETNX: Set key to hold string value if key does not exist.
                In that case, it is equal to SET.
                When key already holds a value, no operation is performed.
                SETNX is short for "SET if Not eXists".
                Integer reply, specifically:
                    1 if the key was set
                    0 if the key was not set
                Specification: https://redis.io/commands/setnx/
             */
            int currentSeconds = Calendar.getInstance().get(Calendar.SECOND);
            int remainingSeconds = 60 - currentSeconds;
            SetParams params = SetParams.setParams().nx().ex(remainingSeconds);
            String result = jedis.set(minuteKeyInNS, "0", params);

            if (!"OK".equals(result)) {
                long currentCounter = jedis.incr(minuteKeyInNS);
                if (currentCounter <= ratePerMinute){
                    System.out.println("Minute " + currentMinute + ", currentCounter: " + currentCounter + ", HTTP 200 " + threadName);
                } else {
                    System.out.println("Minute " + currentMinute + ", currentCounter: " + currentCounter + ", HTTP 429 " + threadName);
                }
                return ;
            }
            call(threadName);
        }
    }

}
