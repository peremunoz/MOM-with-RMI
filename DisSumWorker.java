import exceptions.EMomError;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DisSumWorker implements TopicListenerInterface {

    private static String HOST = "localhost";
    private static MOM mom;

    public static void main(String[] args) {

        try {
            System.out.println("Worker client starting...");

            MsgQ_Init(args);

            System.out.println("MOM server found ✓");

            System.out.println("Subscribing to work topic...");

            subscribeToWorkTopic();

            System.out.println("Subscribed ✓");

            System.out.println("Work topic and Result queue created ✓");

            System.out.println("Waiting for work...");

        } catch (Exception e) {
            System.out.println("Error → " + e.getMessage());
        }
    }

    private static void MsgQ_Init(String[] args) throws RemoteException, NotBoundException {
        if (args.length > 0) {
            HOST = args[0];
        }

        // Get the RMI registry
        Registry registry = LocateRegistry.getRegistry(HOST);

        // Look up the remote object
        mom = (MOM) registry.lookup("MOM");
    }

    private static void subscribeToWorkTopic() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onTopicMessage(Message message) throws RemoteException {

    }

    @Override
    public void onTopicClosed(String topicName) throws RemoteException {

    }
}
