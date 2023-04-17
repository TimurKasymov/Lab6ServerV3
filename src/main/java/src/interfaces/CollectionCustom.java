package src.interfaces;

import java.io.File;
import java.time.LocalDateTime;
import java.util.LinkedList;

public interface CollectionCustom<TEntity> {
    /** validates the list of items*/
    boolean validateData();
    /** returns the LinkedList of TEntity */
    LinkedList<TEntity> get();
    /** returns the initialization time of the collection */
    LocalDateTime getInitializationTime();
    /** returns the type of element in the collection */
    Class getElementType();
    /** saves the collection to file */
    void save();
    /** loads */
    boolean load(File pathToFile);
    File getLoadedFile();

}
