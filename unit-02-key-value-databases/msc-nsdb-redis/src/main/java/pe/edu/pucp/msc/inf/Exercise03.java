package pe.edu.pucp.msc.inf;

import pe.edu.pucp.msc.inf.auction.BidderThread;
import pe.edu.pucp.msc.inf.auction.MyAuction;
import pe.edu.pucp.msc.inf.connectors.RedisConnectionManager;

public class Exercise03 {

    public static void main(String[] args){
        int durationInSecs = 10;
        int productId = 1000;
        float initialPrice = 100;
        MyAuction auction = new MyAuction(durationInSecs, productId, initialPrice);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                RedisConnectionManager.getInstance().closePool();
            }
        }, "Shutdown-thread"));


        int numberOfBidders = 10;
        BidderThread[] bidders = new BidderThread[numberOfBidders];
        //initializing threads
        for(int i = 0; i < numberOfBidders; i++){
            bidders[i] = new BidderThread("Bidder " + (i + 1), auction);
        }
        auction.startAuction();
        //firing threads
        for(int i = 0; i < numberOfBidders; i++){
            bidders[i].start();
        }
        auction.waitForWinner();
    }

}
