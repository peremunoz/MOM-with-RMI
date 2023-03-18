import exceptions.*;

import java.rmi.RemoteException;

public interface MOM extends java.rmi.Remote {
    void MsgQ_CreateQueue(String msgQname) throws EMomError, RemoteException;
    void MsgQ_CloseQueue(String msgQname) throws EMomError, RemoteException;
    void MsgQ_SendMessage(String msgQname, String message, int type) throws EMomError, RemoteException;
    String MsgQ_ReceiveMessage(String msgQname, int type) throws EMomError, RemoteException;
    void MsgQ_CreateTopic(String topicName, EPublishMode mode) throws EMomError, RemoteException;
    void MsgQ_CloseTopic(String topicName) throws EMomError, RemoteException;
    void MsgQ_Publish(String topic, String message, int type) throws EMomError, RemoteException;
    void MsgQ_Subscribe(String topic, TopicListenerInterface listener) throws EMomError, RemoteException;
}
