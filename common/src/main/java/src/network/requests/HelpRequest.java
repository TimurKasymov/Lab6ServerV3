package src.network.requests;

import src.utils.Commands;

public class HelpRequest extends Request{

    public HelpRequest() {
        super(Commands.HELP);
    }
}
