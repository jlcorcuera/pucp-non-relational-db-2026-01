package pe.edu.pucp.msc.inf.auction;

import pe.edu.pucp.msc.inf.connectors.RedisConnectionManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Transaction;

public class MyAuction {

    private static final String MY_APP_NS = "auction";
    private final int durationInSec;
    private final int productId;
    private final float initialPrice;
    private final String auctionInProgressKeyNS;
    private final String auctionCurrentBidKeyNS;
    private final String auctionThreadNameKeyNS;

    public MyAuction(int durationInSec, int productId, float initialPrice){
        this.durationInSec = durationInSec;
        this.productId = productId;
        this.initialPrice = initialPrice;

        this.auctionInProgressKeyNS = MY_APP_NS + ":" + productId;
        this.auctionCurrentBidKeyNS = MY_APP_NS + ":" + productId + ":current-bid";
        this.auctionThreadNameKeyNS = MY_APP_NS + ":" + productId + ":thread-name";
    }

    public void startAuction(){
        try(Jedis jedis = RedisConnectionManager.getInstance().getConnection()){
            Transaction transaction = jedis.multi();
            transaction.set(auctionInProgressKeyNS, "IN PROGRESS");
            System.out.println("In Progress: " + auctionInProgressKeyNS);
            transaction.expire(auctionInProgressKeyNS, durationInSec);
            transaction.set(auctionCurrentBidKeyNS, initialPrice + "");
            transaction.set(auctionThreadNameKeyNS, "Unknown");
            transaction.exec();
        }
    }

    public String bid(String threadName, float bid){
        try (Jedis jedis = RedisConnectionManager.getInstance().getConnection()) {
            if (jedis.exists(auctionInProgressKeyNS)){
                float currentBid = Float.parseFloat(jedis.get(auctionCurrentBidKeyNS));
                if (currentBid < bid){
                    jedis.watch(auctionCurrentBidKeyNS, auctionThreadNameKeyNS);
                    Transaction transaction = jedis.multi();
                    transaction.set(auctionCurrentBidKeyNS, bid + "");
                    transaction.set(auctionThreadNameKeyNS, threadName);
                    if (transaction.exec() == null){
                        return bid(threadName, bid);
                    }
                    return "Bid accepted " + threadName + " " + bid;
                }
                return "Bid rejected " + threadName;
            }
            return "The bid is over";
        }
    }

    public boolean inProgress(){
        try (Jedis jedis = RedisConnectionManager.getInstance().getConnection()) {
            return jedis.exists(auctionInProgressKeyNS);
        }
    }



    public void waitForWinner(){
        try (Jedis jedis = RedisConnectionManager.getInstance().getConnection()) {
                jedis.psubscribe(new JedisPubSub() {
                @Override
                public void onPMessage(String pattern, String channel, String message) {

                    if (message.equals(auctionInProgressKeyNS)){
                        try (Jedis winnerJedis = RedisConnectionManager.getInstance().getConnection()) {
                            String winner = winnerJedis.get(auctionThreadNameKeyNS);
                            String bid = winnerJedis.get(auctionCurrentBidKeyNS);
                            System.out.println("WINNER: " + winner + ", bid: " + bid);
                            System.exit(0);
                        }
                    }

                }
            } , "__keyevent@0__:expired");
        }
    }

}
