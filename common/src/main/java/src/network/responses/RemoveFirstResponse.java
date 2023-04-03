package src.network.responses;

import src.utils.Commands;

public class RemoveFirstResponse extends Response{
    public RemoveFirstResponse(String error) {
        super(Commands.REMOVE_FIRST, error);
    }
}
