public interface TopicListenerInterface extends java.rmi.Remote {
    void onTopicMessage(String message, int type) throws java.rmi.RemoteException;
    void onTopicClosed() throws java.rmi.RemoteException;
}
