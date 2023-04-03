package src.network.responses;

import src.utils.Commands;

public class ShowResponse extends Response{

    public ShowResponse(String error) {
        super(Commands.SHOW, error);
    }
}
