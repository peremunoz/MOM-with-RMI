import exceptions.EMomError;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Vector;

public class MOMServant implements MOM {

    // The message queues
    private final Hashtable<String, Vector<Message>> msgQueues = new Hashtable<>();

    // The topics
    private final Hashtable<String, EPublishMode> topics = new Hashtable<>();

    // The topic listeners
    private final Hashtable<String, Vector<TopicListenerInterface>> topicListeners = new Hashtable<>();

    // Constructor. Create the default Log topic for monitoring the MOM system.
    public MOMServant() throws EMomError {
        try {
            MsgQ_CreateTopic("Log", EPublishMode.BROADCAST);
        } catch (EMomError | RemoteException e) {
            throw new EMomError("Error creating the Log topic: " + e.getMessage());
        }
    }

    @Override
    public void MsgQ_CreateQueue(String msgQname) throws EMomError, RemoteException {
        if (msgQueues.containsKey(msgQname) || topics.containsKey(msgQname)) {
            throw new EMomError("Queue already exists");
        }
        Log("Creating queue " + msgQname);
        msgQueues.put(msgQname, new Vector<>());
    }

    @Override
    public void MsgQ_CloseQueue(String msgQname) throws EMomError, RemoteException {
        if (!msgQueues.containsKey(msgQname)) {
            throw new EMomError("Queue does not exist");
        }
        if (msgQueues.get(msgQname).size() > 0) {
            throw new EMomError("Queue is not empty");
        }
        Log("Closing queue " + msgQname);
        msgQueues.remove(msgQname);
    }

    @Override
    public void MsgQ_SendMessage(String msgQname, String message, int type) throws EMomError, RemoteException {
        if (!msgQueues.containsKey(msgQname)) {
            throw new EMomError("Queue does not exist or has been closed");
        }
        Log("Sending message to queue " + msgQname);
        msgQueues.get(msgQname).add(new Message(message, type));
    }

    @Override
    public String MsgQ_ReceiveMessage(String msgQname, int type) throws EMomError, RemoteException {
        if (!msgQueues.containsKey(msgQname)) {
            throw new EMomError("Queue does not exist or has been closed");
        }
        Log("Receiving message from queue " + msgQname);
        Vector<Message> messages = msgQueues.get(msgQname);
        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            if (msg.type() == type || type == 0) {
                messages.remove(i);
                Log("Received message from queue " + msgQname + ": " + msg.message());
                return msg.message();
            }
        }
        Log("No message of type " + type + " found in queue " + msgQname);
        return null;
    }

    @Override
    public void MsgQ_CreateTopic(String topicName, EPublishMode mode) throws EMomError, RemoteException {
        if (msgQueues.containsKey(topicName) || topics.containsKey(topicName)) {
            throw new EMomError("Topic already exists");
        }
        Log("Creating topic " + topicName);
        topics.put(topicName, mode);
        // Add the topic to the topic listeners
        topicListeners.put(topicName, new Vector<>());
    }

    @Override
    public void MsgQ_CloseTopic(String topicName) throws EMomError, RemoteException {
        if (!topics.containsKey(topicName)) {
            throw new EMomError("Topic does not exist");
        }
        Log("Closing topic " + topicName);
        notifyTopicClosing(topicName);
        topics.remove(topicName);
    }

    @Override
    public void MsgQ_Publish(String topic, String message, int type) throws EMomError, RemoteException {
        if (!topics.containsKey(topic)) {
            throw new EMomError("Topic does not exist or has been closed");
        }
        if (topicListeners.get(topic).size() == 0) {
            throw new EMomError("No listeners for this topic");
        }

        Message msg = new Message(message, type);
        Log("Publishing message to topic " + topic + ": " + msg.message());

        try {
            if (topics.get(topic) == EPublishMode.BROADCAST) {
                notifyTopicBroadcast(topic, msg);
            } else {
                notifyTopicRoundRobin(topic, msg);
            }
        } catch (EMomError e) {
            throw new EMomError("Error notifying topic listeners because: " + e.getMessage());
        }
    }

    @Override
    public void MsgQ_Subscribe(String topic, TopicListenerInterface listener) throws EMomError, RemoteException {
        if (!topics.containsKey(topic)) {
            throw new EMomError("Topic does not exist or has been closed");
        }
        Log("Subscribing to topic " + topic);
        topicListeners.get(topic).add(listener);
    }

    // Auxiliary methods
    private void notifyTopicClosing(String topicName) {
        for (TopicListenerInterface listener : topicListeners.get(topicName)) {
            try {
                Log("Notifying listener " + listener + " that topic " + topicName + " is closing");
                listener.onTopicClosed(topicName);
            } catch (RemoteException | EMomError e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyTopicBroadcast(String topicName, Message msg) throws EMomError {
        for (TopicListenerInterface listener : topicListeners.get(topicName)) {
            try {
                Log("Notifying listener " + listener + " of message " + msg.message() + " in topic " + topicName + " (broadcast)");
                listener.onTopicMessage(msg);
            } catch (RemoteException e) {
                throw new EMomError("Error notifying topic listeners in broadcast mode");
            }
        }
    }

    private void notifyTopicRoundRobin(String topicName, Message msg) throws EMomError {
        Vector<TopicListenerInterface> listeners = topicListeners.get(topicName);
        TopicListenerInterface listenerToSend = listeners.get(0);
        listeners.remove(0);
        listeners.add(listenerToSend);
        try {
            Log("Notifying listener " + listenerToSend + " of message " + msg.message() + " in topic " + topicName + " in round robin mode");
            listenerToSend.onTopicMessage(msg);
        } catch (RemoteException e) {
            throw new EMomError("Error notifying topic listeners in round robin mode");
        }
    }

    private void Log(String message) throws EMomError, RemoteException {
        MsgQ_Publish("Log", message, 0);
    }
}
