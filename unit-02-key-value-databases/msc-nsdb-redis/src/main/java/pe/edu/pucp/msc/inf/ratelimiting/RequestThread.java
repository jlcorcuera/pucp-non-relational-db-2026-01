package pe.edu.pucp.msc.inf.ratelimiting;

public class RequestThread extends Thread {

    private final Integer numberOfIterations = 10*60;
    private final Long SLEEP_TIME_IN_MILI = 1000L;

    public RequestThread(String name){
        super(name);
    }

    public void run() {
        for(int i = 0; i < numberOfIterations; i++){
            try {
                MyAPI.getInstance().call(getName());
                sleep(SLEEP_TIME_IN_MILI);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
