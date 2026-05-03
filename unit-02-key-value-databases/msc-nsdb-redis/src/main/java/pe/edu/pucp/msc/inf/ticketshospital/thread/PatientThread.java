package pe.edu.pucp.msc.inf.ticketshospital.thread;

import pe.edu.pucp.msc.inf.ticketshospital.manager.TicketManager;

import java.util.Random;

public class PatientThread extends Thread {

    private final Integer numberOfIterations = 60;
    private final Long SLEEP_TIME_IN_MILI = 1000L;

    public PatientThread(String name){
        super(name);
    }

    public void run() {
        Random random = new Random();
        for(int i = 0; i < numberOfIterations; i++){
            try {
                long ticketNumber = TicketManager.getTicketNumber();
                if (ticketNumber != -1){
                    System.out.println("Ticket number: " + ticketNumber + ", " + getName());
                } else {
                    System.out.println("No tickets left, sorry " + getName());
                    break;
                }
                sleep(SLEEP_TIME_IN_MILI);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
