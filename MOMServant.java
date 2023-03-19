import exceptions.EMomError;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Vector;

public class MOMServant implements MOM {
    private Hashtable<String, Vector<Message>> msgQueues = new Hashtable<>();
    private Hashtable<String, Hashtable<EPublishMode, Vector<Message>>> topicsQueues = new Hashtable<>();

    @Override
    public void MsgQ_CreateQueue(String msgQname) throws EMomError, RemoteException {
        if (msgQueues.containsKey(msgQname) || topicsQueues.containsKey(msgQname)) {
            throw new EMomError("Queue already exists");
        }
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

        msgQueues.remove(msgQname);
    }

    @Override
    public void MsgQ_SendMessage(String msgQname, String message, int type) throws EMomError, RemoteException {
        if (!msgQueues.containsKey(msgQname)) {
            throw new EMomError("Queue does not exist or has been closed");
        }

        msgQueues.get(msgQname).add(new Message(message, type));
    }

    @Override
    public String MsgQ_ReceiveMessage(String msgQname, int type) throws EMomError, RemoteException {
        if (!msgQueues.containsKey(msgQname)) {
            throw new EMomError("Queue does not exist or has been closed");
        }

        Vector<Message> messages = msgQueues.get(msgQname);
        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            if (msg.type() == type || type == 0) {
                messages.remove(i);
                return msg.message();
            }
        }
        return null;
    }

    @Override
    public void MsgQ_CreateTopic(String topicName, EPublishMode mode) throws EMomError, RemoteException {
        if (msgQueues.containsKey(topicName) || topicsQueues.containsKey(topicName)) {
            throw new EMomError("Topic already exists");
        }
        topicsQueues.put(topicName, new Hashtable<>());
        topicsQueues.get(topicName).put(mode, new Vector<>());
    }

    @Override
    public void MsgQ_CloseTopic(String topicName) throws EMomError, RemoteException {
        if (!topicsQueues.containsKey(topicName)) {
            throw new EMomError("Topic does not exist");
        }
        if (topicsQueues.get(topicName).size() > 0) {
            throw new EMomError("Topic is not empty");
        }
        notifyTopicClosing(topicName);
        topicsQueues.remove(topicName);
    }

    @Override
    public void MsgQ_Publish(String topic, String message, int type) throws EMomError, RemoteException {
        if (!topicsQueues.containsKey(topic)) {
            throw new EMomError("Topic does not exist or has been closed");
        }

        // TODO: send message to all subscribers using the topic listener interface
    }

    @Override
    public void MsgQ_Subscribe(String topic, TopicListenerInterface listener) throws EMomError, RemoteException {
        if (!topicsQueues.containsKey(topic)) {
            throw new EMomError("Topic does not exist or has been closed");
        }
    }

    // Auxiliary methods
    private void notifyTopicClosing(String topicName) {
        // TODO: notify all subscribers that the topic is closing
    }
}
