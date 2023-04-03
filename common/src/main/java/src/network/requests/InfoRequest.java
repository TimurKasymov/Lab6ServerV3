package src.network.requests;

import src.utils.Commands;

public class InfoRequest extends Request{
    public InfoRequest() {
        super(Commands.INFO);
    }
}
