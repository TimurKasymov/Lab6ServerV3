package src.network.responses;

import src.utils.Commands;

public class HistoryResponse extends Response{

    public String history;
    public HistoryResponse(String error) {
        super(Commands.HISTORY, error);

    }
}
