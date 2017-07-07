package bgu.spl.protocol;

public interface ServerProtocolFactory<T> {
   AsyncServerProtocol<T> create();
}
