package src.interfaces;

import java.io.File;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public interface CollectionCustom<TEntity> {
    /** returns the LinkedList of TEntity */
    LinkedList<TEntity> get();
    void add(TEntity entity);

}
