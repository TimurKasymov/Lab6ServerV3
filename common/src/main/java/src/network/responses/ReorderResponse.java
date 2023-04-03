package src.network.responses;

import src.utils.Commands;

public class ReorderResponse extends Response{

    public ReorderResponse(String error) {
        super(Commands.REORDER, error);
    }
}
