package src.network.responses;

import src.utils.Commands;

public class UndoResponse extends Response {
    public UndoResponse(String error) {
        super(Commands.UNDO_COMMANDS, error);
    }
}
