import exceptions.EMomError;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class DisSumWorker implements TopicListenerInterface {

    private static String HOST = "localhost";
    private static MOM mom;

    public static void main(String[] args) {

        try {
            System.out.println("Worker client starting...");

            MsgQ_Init(args);

            System.out.println("MOM server found ✓");
            System.out.println("Created and subscribed listener for receiving work ✓");

            System.out.println("Waiting for work...");

        } catch (Exception e) {
            System.out.println("Error → " + e.getMessage());
        }
    }

    private static void MsgQ_Init(String[] args) throws RemoteException, NotBoundException, EMomError {
        if (args.length > 0) {
            HOST = args[0];
        }

        // Get the RMI registry
        Registry registry = LocateRegistry.getRegistry(HOST);

        // Look up the remote object
        mom = (MOM) registry.lookup("MOM");

        // Create a new listener for receiving work
        DisSumWorker listener = new DisSumWorker();
        TopicListenerInterface listenerStub = (TopicListenerInterface) UnicastRemoteObject.exportObject(listener, 0);

        // Subscribe it to Work and Log topic
        mom.MsgQ_Subscribe("Log", listenerStub);
        mom.MsgQ_Subscribe("Work", listenerStub);
    }

    @Override
    public void onTopicMessage(String topicName, Message message) throws RemoteException {

    }

    @Override
    public void onTopicClosed(String topicName) throws RemoteException {

    }
}
