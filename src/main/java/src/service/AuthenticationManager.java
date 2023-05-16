package src.service;

import org.slf4j.Logger;
import src.db.DI.DbCollectionManager;
import src.db.UserCollectionInDbManager;
import src.loggerUtils.LoggerManager;
import src.models.User;

import java.util.List;

public class AuthenticationManager {

    private Logger logger ;
    private HashingService hashingService;
    private List<User> users;
    private DbCollectionManager<User>  userCollectionInDbManager;
    public AuthenticationManager(List<User> users, DbCollectionManager<User> userCollectionInDbManager){
        logger = LoggerManager.getLogger(this.getClass());
        hashingService = new HashingService();
        this.users = users;
        this.userCollectionInDbManager = userCollectionInDbManager;
    }

    public boolean authenticate(String name, String passwordUnencrypted, boolean createNewUser){
        var encryptedPsw = hashingService.hash(passwordUnencrypted);
        var foundUser = users.stream()
                .filter(u-> u.getName()
                        .equals(name) && u
                        .getPassword().equals(encryptedPsw)).toList();
        if(createNewUser){
            if(foundUser.isEmpty()){
                var maxId = Integer.MIN_VALUE;
                    for (var userInUsers : users) {
                        maxId = Integer.max(maxId, userInUsers.getId());
                    }
                var id = users.size() == 0 ? 1 : maxId + 1;
                var user = new User(id, name, encryptedPsw);
                userCollectionInDbManager.insert(user);
            }
            return true;
        }
        return !foundUser.isEmpty();
    }
}
