package src.service.Auth;

import org.slf4j.Logger;
import src.db.DI.DbCollectionManager;
import src.db.SeqNames;
import src.db.UserCollectionInDbManager;
import src.loggerUtils.LoggerManager;
import src.models.User;
import src.service.HashingService;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class AuthenticationManager {

    private final Logger logger;
    private final HashingService hashingService;
    private final List<User> users;
    private final ReentrantLock lock = new ReentrantLock();
    private final DbCollectionManager<User> userCollectionInDbManager;

    public AuthenticationManager(List<User> users, DbCollectionManager<User> userCollectionInDbManager) {
        logger = LoggerManager.getLogger(this.getClass());
        hashingService = new HashingService();
        this.users = users;
        this.userCollectionInDbManager = userCollectionInDbManager;
    }

    public boolean authenticate(String name, String passwordUnencrypted, boolean createNewUser) {
        var encryptedPsw = hashingService.hash(passwordUnencrypted);
        try {
            lock.lock();
            var foundUser = users.stream()
                    .filter(u -> u.getName()
                            .equals(name) && u
                            .getPassword().equals(encryptedPsw)).toList();
            if (createNewUser) {
                if (foundUser.isEmpty()) {
                    var id = userCollectionInDbManager.getNextId(SeqNames.userSeq);
                    var user = new User(id, name, encryptedPsw);
                    userCollectionInDbManager.insert(user);
                }
            }
            return !foundUser.isEmpty();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            lock.unlock();
        }
        return true;
    }
}
