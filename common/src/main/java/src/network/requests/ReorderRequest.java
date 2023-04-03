package src.network.requests;

import src.utils.Commands;

public class ReorderRequest extends Request{

    public ReorderRequest() {
        super(Commands.REORDER);
    }
}
