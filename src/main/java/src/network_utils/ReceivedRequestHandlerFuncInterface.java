package src.network_utils;

@FunctionalInterface
public interface ReceivedRequestHandlerFuncInterface {
    void receivedRequestHandler(ReadResults result);
}
