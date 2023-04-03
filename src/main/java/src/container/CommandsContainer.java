package src.container;

import java.util.List;

public class CommandsContainer {
    private static List<String> commandNames;
    public static void setCommands(List<String> commands){
        commandNames = commands;
    }

    public static boolean contains(String supposedlyCommand){
        return commandNames.contains(supposedlyCommand.trim().toLowerCase());
    }
}
