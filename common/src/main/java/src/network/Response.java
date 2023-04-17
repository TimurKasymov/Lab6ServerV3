package src.network;

import org.apache.commons.lang3.tuple.Pair;
import src.utils.Argument;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Response  implements Serializable {

    public MessageType messageType;
    public HashMap<String, List<Pair<Argument, Integer>>> commandRequirements;
    public String serverResponseToCommand;

    public Response(String messageForClient) {
        serverResponseToCommand = messageForClient;
    }

    public Response() {
    }

    public void setCommandRequirements(HashMap<String, List<Pair<Argument, Integer>>> commandRequirements){
        this.commandRequirements = commandRequirements;
    }
}
