package src.interfaces;

import org.apache.commons.lang3.tuple.Pair;
import src.network.Request;
import src.utils.Argument;

import java.util.List;


public interface Command {
    /** executes the command */
    boolean execute(String[] args);
    boolean execute(Request request);
    /** prints the command description */
    String getInfo();
    List<Pair<Argument, Integer>> getRequiredArguments();
}
