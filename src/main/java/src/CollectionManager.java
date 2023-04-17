package src;

import src.loggerUtils.LoggerManager;
import src.models.Organization;
import src.models.Product;
import src.interfaces.CollectionCustom;
import src.interfaces.Loadable;
import src.service.CustomCollectionService;
import src.service.WayOfOrder;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;


public class CollectionManager implements CollectionCustom<Product> {

    private LinkedList<Product> products;
    private File xmlfile;
    private final Loadable fileManager;
    private LocalDateTime initializationTime;

    public CollectionManager(Loadable fileManager) {
        this.fileManager = fileManager;
        this.products = new LinkedList<>();
    }

    public File getLoadedFile(){
        return xmlfile;
    }
    @Override
    public boolean load(File pathToFile){
            try {
                Map<String, String> env = System.getenv();
                if (env != null && env.get("pathToXMLCollection") != null)
                    pathToFile =  new File(env.get("pathToXMLCollection"));
                else {
                    if(!pathToFile.getName().split("\\.")[1].equals("xml")){
                        LoggerManager.getLogger(CollectionManager.class).error("the extension of the file must be .xml");
                        return false;
                    }
                }
                xmlfile = pathToFile;
                fileManager.load(xmlfile);
                products = fileManager.get();
                if (validateData())
                    products = products == null ? new LinkedList<Product>() : products;
                else {
                    products = new LinkedList<Product>();
                    LoggerManager
                            .getLogger(CollectionManager.class)
                            .error("the products in the specified file do not meet the validation criteria, loaded collection is cleared");
                }
                boolean up = true, down = true;
                var wayOfOrder = CustomCollectionService.determineWayOfOrder(products);
                if (wayOfOrder == WayOfOrder.NON)
                    products.sort((p, p1) -> (int) (p1.getId() - p.getId()));
                initializationTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
                return true;
            } catch (Exception exception){
                LoggerManager.getLogger(CollectionManager.class).error(exception.getMessage());
            }
        return false;
    }

    @Override
    public boolean validateData() {
        if (products.isEmpty())
            return true;

        var organizations = new LinkedList<Organization>();
        var productIds = new HashSet<Long>();
        var organizationIds = new HashSet<Long>();

        for (var prod : products) {
            if (prod.getManufacturer() != null) {
                var organization = prod.getManufacturer();
                organizationIds.add(organization.getId());
                organizations.add(prod.getManufacturer());
                if (organization.getName() == null || organization.getName().isEmpty() || organization.getAnnualTurnover() == null ||
                        organization.getAnnualTurnover() < 1 || organization.getOrganizationType() == null) {
                    return false;
                }
            }
            if (prod.getPrice() < 1 || prod.getCreationDate() == null || prod.getCoordinates() == null ||
                    prod.getName() == null || prod.getManufactureCost() == null || prod.getCoordinates().getX() == null
                    || prod.getCoordinates().getY() <= -264)
            {return false;}

            productIds.add(prod.getId());
        }
        var ids = productIds.toArray();
        var minId = Long.MAX_VALUE;
        for (Object id : ids) {
            if ((Long) id < minId)
                minId = (Long) id;
        }
        var minOrganizationId = organizationIds.stream().reduce(Long.MAX_VALUE, (m, i) -> {
            if (i < m) {
                m = i;
            }
            return m;
        });

        return ids.length >= products.size() && minId >= 1
                && minOrganizationId >= 1 && (long) organizationIds.size() >= organizationIds.size();

    }

    @Override
    public LinkedList<Product> get() {
        return products;
    }

    @Override
    public LocalDateTime getInitializationTime() {
        return initializationTime;
    }

    @Override
    public Class getElementType() {
        return Product.class;
    }

    @Override
    public void save() {
        try {
            if (!validateData()) {
                return;
            }
            if(xmlfile != null && xmlfile.exists())
                fileManager.save(products, xmlfile);
            LoggerManager.getLogger(CollectionManager.class).info("collections successfully saved");
        } catch (Exception exception) {
            LoggerManager.getLogger(CollectionManager.class).error(exception.getMessage());
        }
    }
}
