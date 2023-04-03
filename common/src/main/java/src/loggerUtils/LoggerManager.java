package src.loggerUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerManager {
    public static <T> Logger getLogger(Class<T> loggingClass){
        return LoggerFactory.getLogger(loggingClass);
    }
}
