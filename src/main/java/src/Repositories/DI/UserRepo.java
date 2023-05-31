package src.Repositories.DI;

import src.models.User;
import src.network.Request;

import java.util.List;
import java.util.Optional;

public interface UserRepo {

    List<User> getUsers();

    Optional<User> getUser(Integer id);

    Optional<User> getUser(Request request);
}
