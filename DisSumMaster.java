import exceptions.EMomError;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Vector;

import static java.lang.System.exit;

public class DisSumMaster {

    private static long M; // The top number to sum up to
    private static int N; // The number of workers
    private static String HOST = "localhost";
    private static MOM mom;

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Usage: java DisSumMaster <M> <N> [<server-ip>]");
            exit(1);
        }
        try {
            System.out.println("Master client starting...");

            MsgQ_Init(args);

            System.out.println("MOM server found ✓");

            setupQueues();

            System.out.println("Work topic and Result queue created ✓");

            Vector<Message> messageVector = prepareMessages();

            System.out.println("Messages prepared ✓");
            System.out.println("Press any key to start the computation...");
            System.in.read();

            System.out.println("Sending messages to MOM server...");
            sendMessages(messageVector);
            System.out.println("Messages sent ✓");

            System.out.println("Waiting for results...");
            long result = getResult();
            System.out.println("Work done ✓✓✓ → Final result: " + result);

            closeQueues();

            exit(0);

        } catch (Exception e) {
            System.out.println("Error → " + e.getMessage());
        }
    }

    private static void MsgQ_Init(String[] args) throws RemoteException, NotBoundException {
        // Save the program arguments
        M = Integer.parseInt(args[0]);
        N = Integer.parseInt(args[1]);
        if (args.length == 3) {
            HOST = args[2];
        }

        // Get the RMI registry
        Registry registry = LocateRegistry.getRegistry(HOST);

        // Look up the remote object
        mom = (MOM) registry.lookup("MOM");
    }

    private static void setupQueues() throws EMomError, RemoteException {
        // Create the "Work" topic for sending work to the workers
        mom.MsgQ_CreateTopic("Work", EPublishMode.ROUND_ROBIN);

        // Create the "Result" queue for receiving results from the workers
        mom.MsgQ_CreateQueue("Result");
    }

    private static Vector<Message> prepareMessages() {
        Vector<Message> messageVector = new Vector<>();
        int numberOfMessages = N;

        // Calculate the start and end numbers for each worker
        long start = 2;
        long end = M / numberOfMessages;
        for (int i = 0; i < numberOfMessages; i++) {
            // Create a message with the start and end numbers
            Message message = new Message(start+"-"+end, 1);
            messageVector.add(message);

            // Calculate the start and end numbers for the next worker
            start = end + 1;
            end = end + (M / numberOfMessages);

            if (i == numberOfMessages - 2) {
                end = M;
            }
        }
        return messageVector;
    }

    private static void sendMessages(Vector<Message> messageVector) throws EMomError, RemoteException {
        for (Message message : messageVector) {
            mom.MsgQ_Publish("Work", message.message(), message.type());
        }
    }

    private static long getResult() throws EMomError, RemoteException {
        long result = 0;
        int numberOfResults = N;
        int resultsReceived = 0;
        while (resultsReceived != numberOfResults) {
            String message = mom.MsgQ_ReceiveMessage("Result", 1);
            if (message != null) {
                result += Long.parseLong(message);
                resultsReceived++;
                System.out.println(resultsReceived + "/" + numberOfResults + " results received");
            }
        }
        return result;
    }

    private static void closeQueues() throws EMomError, RemoteException {
        mom.MsgQ_CloseQueue("Result");
        mom.MsgQ_CloseTopic("Work");
    }
}
