package pe.edu.pucp.msc.inf;


import pe.edu.pucp.msc.inf.connectors.RedisConnectionManager;
import pe.edu.pucp.msc.inf.ratelimiting.RequestThread;

public class Exercise02 {
    public static void main(String [] args) throws InterruptedException {

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                RedisConnectionManager.getInstance().closePool();
            }
        }, "Shutdown-thread"));

        int numberOfClients = 10;
        RequestThread[] clients = new RequestThread[numberOfClients];
        //initializing threads
        for(int i = 0; i < numberOfClients; i++){
            clients[i] = new RequestThread("Client " + (i + 1));
        }
        //firing threads
        for(int i = 0; i < numberOfClients; i++){
            clients[i].start();
        }
        //waiting for them to finish
        for(int i = 0; i < numberOfClients; i++){
            clients[i].join();
        }
    }
}
