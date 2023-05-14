package src.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import src.container.SettingsContainer;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionContainer {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    static {
        config.setJdbcUrl(SettingsContainer.getSettings().dbUrl);
        config.setUsername(SettingsContainer.getSettings().dbUser);
        config.setPassword(SettingsContainer.getSettings().dbPsw);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }
}
