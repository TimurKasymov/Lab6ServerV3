package src.db;

import src.loggerUtils.LoggerManager;

public class TableManager {
    private static final String productCreationQ = "create table if not exists Coordinates(\n" +
            "\tid serial primary key,\n" +
            "\tx double precision not null,\n" +
            "\ty float\n" +
            ");\n" +
            "\n" +
            "create table if not exists Organizations(\n" +
            "\tid serial primary key,\n" +
            "\tname text not null,\n" +
            "\tannualTurnover bigint not null,\n" +
            "\torganizationType int not null\n" +
            ");\n" +
            "\n" +
            "create table if not exists Products(\n" +
            "\tid serial primary key,\n" +
            "\tname text not null,\n" +
            "\tcoordinates int references Coordinates(id),\n" +
            "\tcreationDate timestamp not null,\n" +
            "\tprice float not null check (price > 0),\n" +
            "\tmanufactureCost double precision not null,\n" +
            "\tunitOfMeasure int not null,\n" +
            "\torganization int references Organizations(id),\n" +
            "\tuserId int references users(id)" +
            ");";

    private static final String userCreatingQ = "create table if not exists users(\n" +
            "\tid serial primary key,\n" +
            "\tpassword text,\n" +
            "\tname text not null,\n" +
            "\trole int not null" +
            ")";

    private static final String baseTableCreatingQ = "create table if not exists internalDataHistory(\n" +
            "\tid serial primary key,\n" +
            "\tdata int not null\n" +
            ")";

    public static void ensureTablesExist() {
        try {
            var connection = ConnectionContainer.getConnection();
            try (var usersSt = connection.createStatement();
                 var internalSt = connection.createStatement();
                 var prodSt = connection.createStatement()) {
                usersSt.executeUpdate(userCreatingQ);
                prodSt.executeUpdate(productCreationQ);
                internalSt.executeUpdate(baseTableCreatingQ);
            }
            var scrToEx = "insert into internalDataHistory(data) values(0)";
            try (var st = connection.createStatement()){
                st.executeUpdate(scrToEx);
            }
        } catch (Exception exception) {
            LoggerManager.getLogger(ProductCollectionInDbManager.class).error(exception.getMessage());
        }
    }
}
