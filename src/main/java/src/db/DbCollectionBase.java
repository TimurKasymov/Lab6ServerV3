package src.db;

import org.slf4j.Logger;
import src.container.SettingsContainer;
import src.loggerUtils.LoggerManager;

import java.sql.SQLException;

public class DbCollectionBase {

    protected final Logger logger;
    protected String tableNameToCheck;
    protected String toExecute;

    public DbCollectionBase(String tableNameToCheck, String toExecute) {
        this.toExecute = toExecute;
        this.tableNameToCheck = tableNameToCheck;
        this.logger = LoggerManager.getLogger(ProductCollectionInDbManager.class);
    }

    public void ensureTablesExists() {
        try {
            boolean ownTableCreated = false;
            boolean requiredTableCreated = false;
            var md = ConnectionContainer.getConnection().getMetaData();
            var tables = md.getTables("Tables", "public", null, null);
            while (tables.next()) {
                var tableName = tables.getString(3);
                if (tableName.equals(tableNameToCheck))
                    requiredTableCreated = true;
                if (tableName.equals(ownTableName))
                    ownTableCreated = true;
            }
            if (!ownTableCreated) {
                execute(ownCreatingQuery);
                var queryToReserveFirstIdForReversedORNotInfo = "insert into " + ownTableName + " values(1, ?)";
                try (var st = ConnectionContainer.getConnection().prepareStatement(queryToReserveFirstIdForReversedORNotInfo)) {
                    st.setInt(1, CollectionState.NORMAL.ordinal());
                    st.executeUpdate();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            if (!requiredTableCreated)
                execute(toExecute);
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
        }
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
                var lastServerPort = resSet.getInt("serverPort");
                return lastServerPort == SettingsContainer.getSettings().localPort;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return true;
    }

    public void markReversedCollection() {
        var query = "select from " + ownTableName + " where id = 1";
        try (var st = ConnectionContainer.getConnection().createStatement()) {
            try (var resSet = st.executeQuery(query)) {
                resSet.next();
                var reversedOrNot = resSet.getInt("data");
                //reversedOrNot = (reversedOrNot+1) % 1

            }
        } catch (Exception e) {
            logger.error(e.getMessage());
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

    private String ownTableName = "internalDataHistory";
    public String ownCreatingQuery = "create table internalDataHistory(\n" +
            "\tid serial primary key,\n" +
            "\tdata int not null\n" +
            ")";
}
