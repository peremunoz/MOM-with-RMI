public interface TopicListenerInterface extends java.rmi.Remote {
    void onTopicMessage(Message message) throws java.rmi.RemoteException;
    void onTopicClosed(String topicName) throws java.rmi.RemoteException;
}
