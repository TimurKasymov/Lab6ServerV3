package src.network.responses;

import src.utils.Commands;

public class HelpResponse extends Response{

    public String helps;

    public HelpResponse(String error) {
        super(Commands.HELP, error);
    }
}
