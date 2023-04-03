package src.interfaces;

import src.models.Product;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

public interface Loadable {

    /** loads the collection from the file */
    void load(File file) throws Exception;
    /** saves the collection LinkedList<Product> products to the file*/
    boolean save(List<Product> products, File file) throws Exception;
    /** gets the initialization time */
    ZonedDateTime getInitializationTime();

    /** returns loaded collection */
    LinkedList<Product> get();
}
