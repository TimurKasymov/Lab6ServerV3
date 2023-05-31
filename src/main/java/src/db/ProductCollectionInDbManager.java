package src.db;

import src.container.SettingsContainer;
import src.db.DI.DbCollectionManager;
import src.models.*;

import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ProductCollectionInDbManager extends DbCollectionBase implements DbCollectionManager<Product> {

    public ProductCollectionInDbManager() {
    }

    @Override
    public boolean insert(Product product) {
        try {
            var connection = ConnectionContainer.getConnection();

            String insertIntoCoordinates = "insert into Coordinates values(?, ?, ?)";
            String insertIntoOrganizations = "insert into Organizations values(?, ?, ?, ?)";
            String insertIntoProducts = "insert into Products" +
                    " values(?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (var stCoor = connection.prepareStatement(insertIntoCoordinates);
                 var stOrg = connection.prepareStatement(insertIntoOrganizations);
                 var stProd = connection.prepareStatement(insertIntoProducts)) {
                stCoor.setInt(1, product.getCoordinates().getId());
                stCoor.setDouble(2, product.getCoordinates().getX());
                stCoor.setFloat(3, product.getCoordinates().getY());
                stCoor.executeUpdate();
                int id;
                if (product.getManufacturer() != null) {
                    stOrg.setLong(1, product.getManufacturer().getId());
                    stOrg.setString(2, product.getManufacturer().getName());
                    stOrg.setInt(3, product.getManufacturer().getAnnualTurnover());
                    stOrg.setInt(4, product.getManufacturer().getOrganizationType().ordinal());
                    stOrg.executeUpdate();
                }

                stProd.setLong(1, product.getId());
                stProd.setString(2, product.getName());
                stProd.setInt(3, product.getCoordinates().getId());
                stProd.setTimestamp(4, Timestamp.valueOf(product.getCreationDate()));
                stProd.setFloat(5, product.getPrice());
                stProd.setDouble(6, product.getManufactureCost());
                stProd.setInt(7, product.getUnitOfMeasure().ordinal());
                if (product.getManufacturer() == null)
                    stProd.setNull(8, java.sql.Types.INTEGER);
                else
                    stProd.setInt(8, product.getManufacturer().getId().intValue());
                stProd.setInt(9, product.getUser().getId());
                stProd.executeUpdate();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            return false;
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return true;
    }

    @Override
    public boolean update(Product product) {
        try {
            var connection = ConnectionContainer.getConnection();
            try (var stCoor = connection.prepareStatement(updateCoordinates);
                 var stOrg = connection.prepareStatement(updateOrganization);
                 var stProd = connection.prepareStatement(updateProduct)) {
                stCoor.setDouble(1, product.getCoordinates().getX());
                stCoor.setFloat(2, product.getCoordinates().getY());
                stCoor.setInt(3, product.getCoordinates().getId());
                stCoor.executeUpdate();

                if (product.getManufacturer() != null) {
                    stOrg.setString(1, product.getManufacturer().getName());
                    stOrg.setInt(2, product.getManufacturer().getAnnualTurnover());
                    stOrg.setInt(3, product.getManufacturer().getOrganizationType().ordinal());
                    stOrg.setLong(4, product.getManufacturer().getId());
                    stOrg.executeUpdate();
                }

                stProd.setString(1, product.getName());
                stProd.setFloat(2, product.getPrice());
                stProd.setDouble(3, product.getManufactureCost());
                stProd.setInt(4, product.getUnitOfMeasure().ordinal());
                stProd.setInt(5, product.getUser().getId());
                if (product.getManufacturer() == null)
                    stProd.setNull(6, Types.INTEGER);
                else
                    stProd.setInt(6, product.getManufacturer().getId().intValue());
                stProd.setLong(7, product.getId());

                stProd.executeUpdate();
                return true;
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(Product product) {
        try {
            var connection = ConnectionContainer.getConnection();
            try (var stCoor = connection.prepareStatement(deleteCoordinates);
                 var stOrg = connection.prepareStatement(deleteOrganization);
                 var stProd = connection.prepareStatement(deleteProduct)) {
                stProd.setLong(1, product.getId());
                stProd.executeUpdate();

                stCoor.setInt(1, product.getCoordinates().getId());
                stCoor.executeUpdate();

                if (product.getManufacturer() == null)
                    return true;
                stOrg.setLong(1, product.getManufacturer().getId());
                stOrg.executeUpdate();

                return true;
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    @Override
    public List<Product> load() {
        try {
            var connection = ConnectionContainer.getConnection();
            var selectSt = "select * from ";
            var result = new LinkedList<Product>();
            try {
                try (var stPr = connection.prepareStatement(selectSt + "Products");
                     var stOrg = connection.prepareStatement(selectSt + "Organizations where id = ?");
                     var stUser = connection.prepareStatement(selectSt + "users where id = ?");
                     var stCoor = connection.prepareStatement(selectSt + "Coordinates where id = ?")) {
                    try (var products = stPr.executeQuery()) {
                        while (products.next()) {
                            var id = Long.parseLong(products.getString("id"));
                            var name = products.getString("name");
                            var creationDate = products.getTimestamp("creationDate");
                            var price = products.getFloat("price");
                            var manufactureCost = products.getDouble("manufactureCost");
                            var unitOfMeasure = products.getInt("unitOfMeasure");
                            var coordinatesId = products.getInt("coordinates");
                            var organizationId = products.getInt("organization");
                            var userId = products.getInt("userId");
                            Coordinates coordinate = null;
                            Organization organization = null;
                            User user = null;

                            if (coordinatesId != 0) {
                                stCoor.setInt(1, coordinatesId);
                                try (var coordinates = stCoor.executeQuery()) {
                                    coordinates.next();
                                    var coorid = Integer.parseInt(coordinates.getString("id"));
                                    var x = Double.valueOf(coordinates.getString("x"));
                                    var y = Float.valueOf(coordinates.getString("y"));
                                    coordinate = new Coordinates(coorid, x, y);
                                }
                            }
                            if (organizationId != 0) {
                                stOrg.setInt(1, organizationId);
                                try (var organizations = stOrg.executeQuery()) {
                                    organizations.next();
                                    var coorid = Long.parseLong(organizations.getString("id"));
                                    var orgname = organizations.getString("name");
                                    var annualTurnover = Integer.valueOf(organizations.getString("annualTurnover"));
                                    var organizationType = Integer.parseInt(organizations.getString("organizationType"));
                                    organization = new Organization(coorid, orgname, annualTurnover, OrganizationType.values()[organizationType]);
                                }
                            }
                            if (userId != 0) {
                                stUser.setInt(1, userId);
                                var rows = stUser.executeQuery();
                                while (rows.next()) {
                                    var userid = Integer.parseInt(rows.getString("id"));
                                    var username = rows.getString("name");
                                    var password = rows.getString("password");
                                    user = new User(userid, password, username);
                                }
                            }
                            var prod = new Product(id, coordinate, creationDate.toLocalDateTime(), price, manufactureCost, UnitOfMeasure.values()[unitOfMeasure],
                                    name, organization, user);
                            result.add(prod);
                        }
                    } catch (SQLException e) {
                        logger.error(e.getMessage());
                    }
                    var queryWasReversedOrNot = "select * from " + tableHistoryName.toLowerCase() + " where id = 1";
                    try (var st = ConnectionContainer.getConnection().createStatement()) {
                        try (var wasReversedOrNot = st.executeQuery(queryWasReversedOrNot)) {
                            wasReversedOrNot.next();
                            var state = wasReversedOrNot.getInt("data");
                            if (CollectionState.values()[state] == CollectionState.REVERSED)
                                Collections.reverse(result);
                        }
                    }
                    return Collections.synchronizedList(result);
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            } catch (NumberFormatException e) {
                logger.error(e.getMessage());
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    private final String updateCoordinates = "update Coordinates set x = ?, y = ? where id = ?";
    private final String updateOrganization = "update Organizations set name = ?, annualTurnover = ?, organizationType = ? where id = ?";
    private final String updateProduct = "update Products set name = ?, price = ?, manufactureCost = ?, unitOfMeasure = ?, userId = ?, organization = ? where id = ?";

    private final String deleteProduct = "delete from Products where id = ?";
    private final String deleteOrganization = "delete from Organizations where id = ?";
    private final String deleteCoordinates = "delete from Coordinates where id = ?";
}
