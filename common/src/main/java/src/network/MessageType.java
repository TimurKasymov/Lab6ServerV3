package src.network;

import src.utils.Commands;

public enum MessageType {
    ALL_AVAILABLE_COMMANDS(null),
    ADD(Commands.ADD),
    HELP(Commands.HELP),
    INFO(Commands.INFO),
    SHOW(Commands.SHOW),
    REMOVE_BY_ID(Commands.REMOVE_BY_ID),
    CLEAR(Commands.CLEAR),
    LOAD_COLLECTION(Commands.LOAD_COLLECTION),
    EXECUTE_SCRIPT(Commands.EXECUTE_SCRIPT),
    FILTER_BY_MANUFACTURE_COST(Commands.FILTER_BY_MANUFACTURE_COST),
    HISTORY(Commands.HISTORY),
    REORDER(Commands.REORDER),
    UPDATE_BY_ID(Commands.UPDATE_BY_ID),
    SAVE(Commands.SAVE),
    REMOVE_FIRST(Commands.REMOVE_FIRST),

    PRINT_UNIQUE_UNIT_OF_MEASURE(Commands.PRINT_UNIQUE_UNIT_OF_MEASURE),
    FILTER_GREATER_THAN_PRICE(Commands.FILTER_GREATER_THAN_PRICE),
    UNDO_COMMANDS(Commands.UNDO_COMMANDS),
    SIGNUP(Commands.SIGNAUP),
    LOGIN(Commands.LOGIN),
    COMMAND_RESULT(null);
    private String command;

    private MessageType(String command) {
        this.command = command;
    }

    public String getCommandDesc() {
        return command;
    }
}
