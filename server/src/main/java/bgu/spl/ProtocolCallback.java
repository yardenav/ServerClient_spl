package bgu.spl;


import java.io.IOException;

public interface ProtocolCallback<T> {
/**
* @param msg message to be sent
* @throws IOException if the message could not be sent , or if the
connection to this client has been closed .
*/
void sendMessage (T msg) throws java.io.IOException ;
}