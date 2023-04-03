package src.interfaces;

import src.network.requests.Request;


public interface Command {
    /** executes the command */
    boolean execute(String[] args);
    boolean execute(Request request);
    /** prints the command description */
    String getInfo();
}
