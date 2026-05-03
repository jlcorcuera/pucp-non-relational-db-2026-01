package pe.edu.pucp.msc.inf.auction;

import java.util.Random;

public class BidderThread extends Thread {

    private final Long SLEEP_TIME_IN_MILI = 1000L;
    private final MyAuction auction;
    private final int MIN_BID_VALUE = 0;
    private final int MAX_BID_VALUE = 1000;

    public BidderThread(String name, MyAuction auction){
        super(name);
        this.auction = auction;
    }

    public void run() {
        Random random = new Random();
        while (auction.inProgress()){
            float bid = MIN_BID_VALUE + random.nextFloat() * (MAX_BID_VALUE - MIN_BID_VALUE);
            String answer = auction.bid(this.getName(), bid);
            System.out.println(answer);
            try {
                sleep(SLEEP_TIME_IN_MILI);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
