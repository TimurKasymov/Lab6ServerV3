package src.network.requests;

import src.utils.Commands;

public class ShowRequest extends Request{

    public ShowRequest() {
        super(Commands.SHOW);
    }
}
