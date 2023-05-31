package src.db;

import org.slf4j.Logger;
import src.container.SettingsContainer;
import src.loggerUtils.LoggerManager;

import java.sql.SQLException;

public class DbCollectionBase {

    protected final Logger logger;
    protected String toExecute;

    public DbCollectionBase() {
        this.logger = LoggerManager.getLogger(ProductCollectionInDbManager.class);
    }

    private void execute(String query) {
        try (var st = ConnectionContainer.getConnection().createStatement()) {
            st.executeUpdate(query);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public boolean isThisLastServerToTouchDB(int port) {
        var query = "select data from internalDataHistory order by id desc limit 1";
        try (var st = ConnectionContainer.getConnection().createStatement()) {
            try (var resSet = st.executeQuery(query)) {
                resSet.next();
                var lastServerPort = resSet.getInt("data");
                return lastServerPort == SettingsContainer.getSettings().localPort;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return true;
    }

    public void markReversedCollection() {
        var query = "select * from " + tableHistoryName + " where id = 1";
        var updateQuery = "update " + tableHistoryName + " set data = ? where id = 1";
        try (var st = ConnectionContainer.getConnection().createStatement()) {
            try (var resSet = st.executeQuery(query)) {
                resSet.next();
                var reversedOrNot = resSet.getInt("data");
                reversedOrNot = (reversedOrNot+1) % 2;
                try (var stInsert = ConnectionContainer.getConnection().prepareStatement(updateQuery)) {
                    stInsert.setInt(1, reversedOrNot);
                    stInsert.executeUpdate();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    public Integer getNextId(String seqName){
        try (var st = ConnectionContainer.getConnection().createStatement()) {
            try (var res = st.executeQuery(seqName)) {
                res.next();
                return res.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void markThatThisServerHasMadeChangesToDb() {
        try {
            var query = "insert into internalDataHistory(data) values(?)";
            try (var stInsert = ConnectionContainer.getConnection().prepareStatement(query)) {
                stInsert.setInt(1, SettingsContainer.getSettings().localPort);
                stInsert.executeUpdate();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    protected String tableHistoryName = "internalDataHistory";
}
