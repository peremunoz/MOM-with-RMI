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

import static java.lang.System.exit;

public class DisSumWorker implements TopicListenerInterface {

    private static String HOST = "localhost";
    private static MOM mom;
    private static int tasksCompleted = 0;

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
        //mom.MsgQ_Subscribe("Log", listenerStub);
        mom.MsgQ_Subscribe("Work", listenerStub);
    }

    private static void processLogMessage(Message message) {
        System.out.println("Log → " + message.message());
    }

    private static void processWorkMessage(Message message) throws EMomError, RemoteException {
        String[] workIntervals = message.message().split("-");
        long start = Long.parseLong(workIntervals[0]);
        long end = Long.parseLong(workIntervals[1]);
        System.out.println("Received work → " + start + " - " + end);

        long result = computeWork(start, end);
        sendResult(result);
        System.out.println("Sent result to master → " + result);
    }

    private static long computeWork(long start, long end) {
        long sum = 0;
        for (long i = start; i <= end; i++) {
            if (isPrime(i)) {
                sum += i;
            }
        }
        tasksCompleted++;
        return sum;
    }

    private static void sendResult(long result) throws EMomError, RemoteException {
        Message message = new Message(String.valueOf(result), 1);
        mom.MsgQ_SendMessage("Results", message.message(), message.type());
    }

    private static boolean isPrime(long num) {
        if (num <= 1) {
            return false;
        }
        for (int i = 2; num <= Math.sqrt(num); i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onTopicMessage(String topicName, Message message) throws RemoteException, EMomError {
        if (topicName.equals("Log")) {
            processLogMessage(message);
        } else if (topicName.equals("Work")) {
            processWorkMessage(message);
        } else {
            System.out.println("Unknown topic: " + topicName);
        }
    }

    @Override
    public void onTopicClosed(String topicName) throws RemoteException {
        System.out.println(topicName + " topic closed");

        if (topicName.equals("Log")) {
            System.out.println("Number of tasks completed: " + tasksCompleted);
            exit(0);
        }
    }
}
