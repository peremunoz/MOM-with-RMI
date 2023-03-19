import exceptions.EMomError;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MOMLog implements TopicListenerInterface {

    private static String HOST = "localhost";

    public static void main(String[] args) throws RemoteException, NotBoundException, EMomError {

        System.out.println("Starting MOM Log...");

        if (args.length > 0) {
            HOST = args[0];
        }

        // Get the RMI registry
        Registry registry = LocateRegistry.getRegistry(HOST);

        // Look up the remote object
        MOM mom = (MOM) registry.lookup("MOM");

        System.out.println("MOM server found");
        System.out.println("Creating listener and subscribing to Log topic...");

        // Create a listener and subscribe to the Log topic
        MOMLog log = new MOMLog();
        TopicListenerInterface logStub = (TopicListenerInterface) UnicastRemoteObject.exportObject(log, 0);
        mom.MsgQ_Subscribe("Log", logStub);

        System.out.println("MOM Log up and listening to system events!\n");
        System.out.println(" · [Log System] · ");
    }

    @Override
    public void onTopicMessage(Message message) throws RemoteException {
        System.out.println(message.message());
    }

    @Override
    public void onTopicClosed(String topicName) throws RemoteException {}
}
