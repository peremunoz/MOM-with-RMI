import exceptions.EMomError;

public interface TopicListenerInterface extends java.rmi.Remote {
    void onTopicMessage(String topicName, Message message) throws java.rmi.RemoteException, EMomError;
    void onTopicClosed(String topicName) throws java.rmi.RemoteException;
}
