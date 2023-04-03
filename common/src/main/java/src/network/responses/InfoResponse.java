package src.network.responses;

import src.utils.Commands;

public class InfoResponse extends Response{

    public String info;
    public InfoResponse(String error) {
        super(Commands.INFO, error);
    }
}
