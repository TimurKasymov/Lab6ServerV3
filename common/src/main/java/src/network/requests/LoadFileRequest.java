package src.network.requests;

import src.utils.Commands;

public class LoadFileRequest extends Request {
    public String collectionFileName;
    public LoadFileRequest(String collectionFileName) {
        super(Commands.LOAD_COLLECTION);
        this.collectionFileName = collectionFileName;
    }
}
