package src.db;

import org.slf4j.Logger;
import src.db.DI.DbCollectionManager;
import src.loggerUtils.LoggerManager;
import src.models.User;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class UserCollectionInDbManager extends DbCollectionBase implements DbCollectionManager<User> {
    private final Logger logger;

    public UserCollectionInDbManager() {
        super("users", "create table users(\n" +
                "\tid serial primary key,\n" +
                "\tpassword text,\n" +
                "\tname text not null\n" +
                ")");
        this.logger = LoggerManager.getLogger(ProductCollectionInDbManager.class);
    }

    public boolean insert(User user) {
        try{
            var connection =  ConnectionContainer.getConnection();
            try (var stUser = connection.prepareStatement(insertUser)) {
                stUser.setInt(1, user.getId());
                stUser.setString(2, user.getPassword());
                stUser.setString(3, user.getName());
                stUser.executeUpdate();
                return true;
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        catch (SQLException e){
            logger.error(e.getMessage());
        }
        return false;
    }

    public boolean update(User user) {
        try{
            var connection = ConnectionContainer.getConnection();
            try (var stUser = connection.prepareStatement(updateUser)) {
                stUser.setString(1, user.getPassword());
                stUser.setString(2, user.getName());
                stUser.setInt(3, user.getId());
                stUser.executeUpdate();
                return true;
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        catch (SQLException e){
            logger.error(e.getMessage());
        }
        return false;
    }

    public boolean delete(User user) {
        try (var stUser = ConnectionContainer.getConnection().prepareStatement(deleteUser)) {
            stUser.setInt(1, user.getId());
            stUser.executeUpdate();
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    public List<User> load() {
        var result = new LinkedList<User>();
        var query = "select * from Users";
        try(var stUser = ConnectionContainer.getConnection().prepareStatement(query)) {
            var rows = stUser.executeQuery();
            while(rows.next()) {
                var id = Integer.parseInt(rows.getString("id"));
                var name = rows.getString("name");
                var password = rows.getString("password");
                result.add(new User(id, password, name));
            }
        }
        catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return Collections.synchronizedList(result);
    }

    private String insertUser = "insert into Users values(?, ?, ?)";
    private String updateUser = "update Users set password = ?, name = ? where id = ?";
    private String deleteUser = "delete from Users where id = ?";
}
