package src.network.responses;

import src.utils.Commands;

public class UpdateByIdResponse extends Response {

    public UpdateByIdResponse(String error) {
        super(Commands.UPDATE_BY_ID, error);
    }
}
