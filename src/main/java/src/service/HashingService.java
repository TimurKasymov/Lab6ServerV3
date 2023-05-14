package src.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import src.loggerUtils.LoggerManager;

public class HashingService {
    private Logger logger;
    public HashingService(){
        logger = LoggerManager.getLogger(this.getClass());
    }

    public String hash(String toHash){
        try{
            return new DigestUtils("SHA3-256").digestAsHex(toHash);
        }
        catch (Exception exception){
            logger.error(exception.getMessage());
        }
        return null;
    }
}
