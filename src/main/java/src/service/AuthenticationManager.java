package src.service;

import org.slf4j.Logger;
import src.db.UserCollectionInDbManager;
import src.loggerUtils.LoggerManager;
import src.models.User;

public class AuthenticationManager {

    private Logger logger ;
    private HashingService hashingService;
    private UserCollectionInDbManager userService;
    public AuthenticationManager(UserCollectionInDbManager userService){
        logger = LoggerManager.getLogger(this.getClass());
        hashingService = new HashingService();
        this.userService = userService;
    }

    public boolean isAuthenticated(User user){

    }

}
