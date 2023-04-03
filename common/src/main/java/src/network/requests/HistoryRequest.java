package src.network.requests;

import src.utils.Commands;

public class HistoryRequest extends Request{
    public HistoryRequest() {
        super(Commands.HISTORY);

    }
}
