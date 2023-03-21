package Clients;

import RMIObjects.MOM;
import RMIObjects.Message;
import RMIObjects.TopicListenerInterface;
import RMIObjects.exceptions.EMomError;

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

        MsgQ_Init();

        System.out.println("MOM Log up and listening to system events ✓\n");
        System.out.println(" ↓ [Log System] ↓ ");
    }

    private static void MsgQ_Init() throws RemoteException, NotBoundException, EMomError {
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
    }

    @Override
    public void onTopicMessage(String topicName, Message message) throws RemoteException {
        System.out.println(topicName + ": " + message.message());
    }

    @Override
    public void onTopicClosed(String topicName) throws RemoteException {}
}
