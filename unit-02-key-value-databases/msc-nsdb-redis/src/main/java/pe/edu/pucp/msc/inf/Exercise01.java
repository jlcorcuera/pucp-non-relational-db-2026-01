package pe.edu.pucp.msc.inf;


import pe.edu.pucp.msc.inf.ticketshospital.manager.TicketManager;
import pe.edu.pucp.msc.inf.ticketshospital.thread.PatientThread;

public class Exercise01 {

    public static void main(String [] args) throws InterruptedException {

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                TicketManager.closePool();
            }
        }, "Shutdown-thread"));

        int numPatients = 10;
        System.out.println("Start quantity of tickets: " + TicketManager.getCurrentTicketNumber());
        PatientThread[] patients = new PatientThread[numPatients];
        //initializing threads
        for(int i = 0; i < numPatients; i++){
            patients[i] = new PatientThread("Patient " + (i + 1));
        }
        //firing threads
        for(int i = 0; i < numPatients; i++){
            patients[i].start();
        }
        //waiting for them to finish
        for(int i = 0; i < numPatients; i++){
            patients[i].join();
        }
        System.out.println("Final quantity of tickets: " + TicketManager.getCurrentTicketNumber());
    }
}
