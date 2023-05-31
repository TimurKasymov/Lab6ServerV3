package src.Repositories;

import src.models.User;
import src.network.Request;
import src.service.HashingService;

import java.util.List;
import java.util.Optional;

public class UserRepo implements src.Repositories.DI.UserRepo {

    private final List<User> users ;

    public UserRepo(List<User> users) {
        this.users = users;
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    public Optional<User> getUser(Integer id) {
        return users
                .stream()
                .filter(u -> u.getId() == id)
                .findFirst();
    }

    public synchronized Optional<User> getUser(Request request) {
        var hashService = new HashingService();
        return users
                .stream()
                .filter(u -> u.getPassword()
                        .equals(hashService.hash(request.userPassword))
                        && u.getName().equals(request.userName))
                .findFirst();
    }

}
