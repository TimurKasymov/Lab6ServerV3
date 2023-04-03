package src.network.responses;

import src.utils.Commands;

public class LoadFileResponse extends Response {
    public boolean successfully;
    public LoadFileResponse(boolean successfully) {
        super(Commands.LOAD_COLLECTION, null);
        this.successfully =successfully;
    }
}
