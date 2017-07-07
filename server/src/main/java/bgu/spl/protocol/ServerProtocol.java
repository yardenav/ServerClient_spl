package bgu.spl.protocol;

import bgu.spl.ProtocolCallback;

/**
 * A protocol that describes the behabiour of the server.
 */
public interface ServerProtocol<T> {

    /**
     * processes a message
     * @param msg the message to process
     * @return the reply that should be sent to the client, or null if no reply needed
     */
    void processMessage(T msg, ProtocolCallback<T> callback);

    /**
     * detetmine whether the given message is the termination message
     * @param msg the message to examine
     * @return true if the message is the termination message, false otherwise
     */
    boolean isEnd(T msg);

}
