package src;

import src.loggerUtils.LoggerManager;
import src.models.Product;
import org.jetbrains.annotations.NotNull;
import src.exceptions.FileLoadingException;
import src.exceptions.NoAccessToFileException;
import src.interfaces.CollectionCustom;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

/*public class RealUndoManager {

    private static final String add = "add ";
    private static final String remove = "remove ";
    private static final String update = "update ";
    private static final String reverse = "reverse ";
    private static final String transaction_delimiter = "I_am_transaction_delimiter";
    private BufferedWriter commandsFileWriter;
    private Stack<Product> products;
    private Stack<String> commandsLogs;
    private CollectionCustom<Product> realProductCollection = null;
    private XmlFileHandler xmlFileHandler = null;
    private final File xmlFile;
    private final File commandsLogsFile;
    private Logger messageHandler;

    public RealUndoManager(File xmlFile, File commandsLogsFile, CollectionCustom<Product> productCollection) throws NoAccessToFileException, FileLoadingException {
        this.xmlFile = xmlFile;
        this.messageHandler = LoggerManager.getLogger(CommandManager.class);
        this.commandsLogsFile = commandsLogsFile;
        var lock = new ReentrantLock();
        try {
            xmlFileHandler = new XmlFileHandler();
            products = new Stack<>();
            commandsLogs = new Stack<>();
            this.realProductCollection = productCollection;
            lock.lock();
            if (xmlFile.exists()) {
                xmlFileHandler.load(xmlFile);
                var prods = xmlFileHandler.get();
                Collections.reverse(prods);
                for (var prod : prods) {
                    products.push(prod);
                }
            } else {
                var wasCreated = xmlFile.createNewFile();
                messageHandler.info("xml file for backing was created");
            }
            if (commandsLogsFile.exists()) {
                BufferedReader commandsFileReader = new BufferedReader(new FileReader(commandsLogsFile));
                var strCurrentLine = "";
                while ((strCurrentLine = commandsFileReader.readLine()) != null) {
                    commandsLogs.push(strCurrentLine);
                }
                Collections.reverse(commandsLogs);
            } else {
                var wasCreated = commandsLogsFile.createNewFile();
                messageHandler.info("logging txt file for backing was created");
            }

        } catch (Exception e) {
            messageHandler.info(e.getMessage());
        }
        finally {
            lock.unlock();
        }
    }

    public void startOrEndTransaction() {
        commandsLogs.push(transaction_delimiter);
    }

    public synchronized void saveLoggingFiles() {
        try {
            new FileWriter(commandsLogsFile, false).close();
            this.commandsFileWriter = new BufferedWriter(new FileWriter(commandsLogsFile));
            for (var str : commandsLogs) {
                commandsFileWriter.write(str);
                commandsFileWriter.newLine();
            }
            commandsFileWriter.flush();
            xmlFileHandler.save(products, xmlFile);
        } catch (Exception e) {
            messageHandler.info(e.getMessage());
        }
    }

    private synchronized void handleLogging(Product element, String command) {
        products.push(element);
        commandsLogs.push(command);
    }

    public synchronized void logAddCommand(long idOfAddedElInReal) {
        try {
            var toWrite = remove + idOfAddedElInReal;
            commandsLogs.push(toWrite);
            commandsFileWriter.write(toWrite);
            commandsFileWriter.newLine();
        } catch (Exception e) {
            messageHandler.info(e.getMessage());
        }

    }

    public synchronized void logRemoveCommand(Product element) {
        try {
            var foundReal = realProductCollection
                    .get()
                    .stream()
                    .filter(p -> Objects.equals(p.getId(), element.getId()))
                    .findFirst();
            if (foundReal.isEmpty())
                throw new Exception();
            var indexOfFoundInReal = realProductCollection
                    .get().indexOf(foundReal.get());
            handleLogging(element, add + indexOfFoundInReal);
        } catch (Exception e) {
            messageHandler.info(e.getMessage());
        }
    }

    public synchronized void logReorderCommand() {
        try {
            var toWrite = "reverse ";
            commandsLogs.push(toWrite);
            commandsFileWriter.write(toWrite);
        } catch (Exception e) {
            messageHandler.info(e.getMessage());

        }
    }

    public synchronized void logUpdateCommand(Product element) {
        handleLogging(element, update + element.getId());
    }

    public synchronized void undoCommands(int numberOfCommandsToUndo) {
        if(commandsLogs.isEmpty()){
            messageHandler.info("no src.commands to undo");
            return;
        }
        var transaction_delimiter_encountered = false;
        while (numberOfCommandsToUndo > 0 && !commandsLogs.isEmpty()) {
            var topCommand = commandsLogs.pop();
            if (topCommand.equals(transaction_delimiter)) {
                transaction_delimiter_encountered = !transaction_delimiter_encountered;
                if (!transaction_delimiter_encountered)
                    numberOfCommandsToUndo--;
                continue;
            }
            handleCommandToUndo(topCommand);
            if (!transaction_delimiter_encountered)
                numberOfCommandsToUndo--;
        }
    }

    private synchronized void handleCommandToUndo(@NotNull String commandToUndo) {
        var args = commandToUndo.split(" ");
        var argsToHandlers = Arrays.stream(args).skip(1).toList();
        switch (args[0] + " ") {
            case (add) -> handleAddCommand(argsToHandlers);
            case (update) -> handleUpdateCommand(argsToHandlers);
            case (remove) -> handleRemoveCommand(argsToHandlers);
            case (reverse) -> Collections.reverse(realProductCollection.get());
        }
    }

    private synchronized void handleAddCommand(List<String> args) {
        try {
            var id = Integer.parseInt(args.get(0));
            var present = products.pop();
            realProductCollection.get().add(id, present);
        } catch (Exception e) {
            messageHandler.info(e.getMessage());
        }
    }

    private void handleUpdateCommand(List<String> args) {
        try {
            var idInRealCol = Long.parseLong(args.get(0));
            var foundReal = realProductCollection
                    .get()
                    .stream()
                    .filter(p -> p.getId() == idInRealCol)
                    .findFirst();
            var foundInLogs = products.pop();
            if (foundReal.isEmpty())
                throw new Exception();
            var indexOfFoundInReal = realProductCollection
                    .get()
                    .indexOf(foundReal.get());
            realProductCollection.get().remove(foundReal.get());
            realProductCollection.get().add(indexOfFoundInReal, foundInLogs);

        } catch (Exception e) {
            messageHandler.info(e.getMessage());
        }
    }

    private void handleRemoveCommand(List<String> args) {
        try {
            var idInRealCol = Long.parseLong(args.get(0));
            var foundReal = realProductCollection
                    .get()
                    .stream()
                    .filter(p -> p.getId() == idInRealCol)
                    .findFirst();
            if (foundReal.isEmpty())
                throw new Exception();
            realProductCollection.get().remove(foundReal.get());

        } catch (Exception e) {
            messageHandler.info(e.getMessage());
        }
    }

}
 */
