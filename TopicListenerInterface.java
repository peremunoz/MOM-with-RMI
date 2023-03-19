public interface TopicListenerInterface extends java.rmi.Remote {
    void onTopicMessage(String topicName, Message message) throws java.rmi.RemoteException;
    void onTopicClosed(String topicName) throws java.rmi.RemoteException;
}
