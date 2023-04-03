package src.network.requests;

import src.utils.Commands;

public class UndoRequest extends Request {
    public int number;
    public UndoRequest(int number) {
        super(Commands.UNDO_COMMANDS);
        this.number = number;
    }
}
