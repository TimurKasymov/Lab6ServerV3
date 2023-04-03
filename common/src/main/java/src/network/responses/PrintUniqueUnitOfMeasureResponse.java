package src.network.responses;

import src.utils.Commands;

public class PrintUniqueUnitOfMeasureResponse extends Response {
    public PrintUniqueUnitOfMeasureResponse(String error) {
        super(Commands.PRINT_UNIQUE_UNIT_OF_MEASURE, error);
    }
}
