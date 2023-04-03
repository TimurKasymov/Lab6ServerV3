package src.service;

import src.models.*;
import src.container.CommandsContainer;
import src.exceptions.CommandInterruptionException;

import java.util.Collection;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class InputService {

    public InputService(){

    }
    private Scanner scanner = null;
    {
        scanner = new Scanner(System.in);
    }

    public void setScanner(Scanner scanner){
        this.scanner = scanner;
    }

    public String inputName() throws NoSuchElementException, CommandInterruptionException {
        for ( ; ; ) {
            try {
                //messageHandler.displayToUser("Enter a name: ");
                String name = scanner.nextLine().trim();
                if(CommandsContainer.contains(name))
                    throw new CommandInterruptionException(name);
                if (name.equals("")) {
                    //messageHandler.displayToUser("This value cannot be empty. Try again");
                    continue;
                }
                return name;
            } catch (InputMismatchException inputMismatchException) {
                //messageHandler.displayToUser("This value must be non-empty string.");
            }
        }
    }

    public OrganizationType inputOrganizationType() throws NoSuchElementException, CommandInterruptionException {
        for ( ; ; ) {
            try {
                //messageHandler.displayToUser("Choose OrganizationType. Enter the number corresponding to the desired option. ");
                //inputEnum(OrganizationType.class);
                var orgType = readEnum(OrganizationType.class);
                return (OrganizationType) orgType;
            } catch (InputMismatchException inputMismatchException) {
                //messageHandler.displayToUser("This value must be a number. Try again. ");
                scanner.next();
            }
        }
    }

    public Integer getInt() throws NoSuchElementException, CommandInterruptionException {
        for ( ; ; ) {
            try {
                var str = scanner.nextLine();
                if (str.equals("")) {
                    //messageHandler.displayToUser("This value cannot be empty. Try again");
                    continue;
                }
                if(CommandsContainer.contains(str))
                    throw new CommandInterruptionException(str);
                return Integer.parseInt(str);
            } catch (InputMismatchException | NumberFormatException inputMismatchException) {
                //messageHandler.displayToUser("This value must be a number. Try again. ");
            }
        }
    }


    /** method for combining X and Y inputs */
    public Coordinates inputCoordinates() throws NoSuchElementException, CommandInterruptionException {
        //messageHandler.displayToUser("adding coordinates..");
        var coor = new Coordinates(inputXLocation(), inputYLocation());
       // messageHandler.displayToUser("done with coordinates..");
        return coor;
    }

    /**
     * Method for receiving x-coordinate of location of element
     * @return Double xLocation
     */
    public Double inputXLocation() throws NoSuchElementException, CommandInterruptionException {
        for ( ; ; ) {
            try {
                var str = scanner.nextLine();
                if (str.equals("")) {
                    continue;
                }
                if(CommandsContainer.contains(str))
                    throw new CommandInterruptionException(str);
                var val = Double.parseDouble(str);
                if(Double.isInfinite(val))
                    throw new InputMismatchException();
                return val;
            } catch (InputMismatchException | NumberFormatException inputMismatchException) {
            }
        }
    }

    /**
     * Method for receiving y-coordinate of element
     * @return float yLocation
     */
    public float inputYLocation()  throws NoSuchElementException, CommandInterruptionException {
        for ( ; ; ) {
            try {
                var str = scanner.nextLine();
                if (str.equals("")) {
                    continue;
                }
                if(CommandsContainer.contains(str))
                    throw new CommandInterruptionException(str);
                var val = Float.parseFloat(str);
                if(Float.isInfinite(val))
                    throw new InputMismatchException();
                if(val <= -264){
                    continue;
                }
                return val;
            } catch (InputMismatchException | NumberFormatException  inputMismatchException) {
                //messageHandler.displayToUser("This value must be a float-type number. Try again. ");
            }
        }
    }

    /** method for taking price input */
    public float inputPrice() throws NoSuchElementException, CommandInterruptionException {
        for ( ; ; ) {
            try {
                //messageHandler.displayToUser("Enter the price of the product: ");
                var str = scanner.nextLine();
                if (str.equals("")) {
                    //messageHandler.displayToUser("This value cannot be empty. Try again");
                    continue;
                }
                if(CommandsContainer.contains(str))
                    throw new CommandInterruptionException(str);
                var price = Float.parseFloat(str);
                if(Float.isInfinite(price))
                    throw new InputMismatchException();
                if(price < 0)
                    throw new InputMismatchException();
                return price;
            } catch (InputMismatchException | NumberFormatException  inputMismatchException) {
                //messageHandler.displayToUser("This value must be a float-type positive number. Try again. ");
            }
        }
    }

    /** method for taking price input */
    public Double inputManufactureCost() throws NoSuchElementException, CommandInterruptionException {
        for ( ; ; ) {
            try {
                //messageHandler.displayToUser("Enter manufacture cost: ");
                var str = scanner.nextLine().trim();
                if (str.equals("")) {
                    //messageHandler.displayToUser("This value cannot be empty. Try again");
                    continue;
                }
                if(CommandsContainer.contains(str))
                    throw new CommandInterruptionException(str);
                var inp = Double.parseDouble(str);
                if(Double.isInfinite(inp))
                    throw new InputMismatchException();
                if(inp < 1)
                    throw new InputMismatchException();
                return inp;
            } catch (InputMismatchException | NumberFormatException  inputMismatchException) {
                //messageHandler.displayToUser("This value must be a Double-type number. Try again. ");
            }
        }
    }

    public <T extends Enum<T>> Enum<T> readEnum(Class<T> enumClass) throws NoSuchElementException, CommandInterruptionException {
        var enums = enumClass.getEnumConstants();
        while (true){
            try {
                var str = scanner.nextLine();
                if (str.equals("")) {
                    //messageHandler.displayToUser("This value cannot be empty. Try again");
                    continue;
                }
                if(CommandsContainer.contains(str))
                    throw new CommandInterruptionException(str);
                var index = Integer.parseInt(str);
                if(1 > index || index > enums.length){
                    //messageHandler.displayToUser(String.format("You should enter a number from %s to %s. Try again. ", 1, enums.length));
                    continue;
                }
                return enums[index - 1];
            } catch (InputMismatchException | NumberFormatException inputMismatchException) {
                scanner.next();
            }

        }
    }

    public UnitOfMeasure inputUnitOfMeasure() throws NoSuchElementException, CommandInterruptionException {
        for ( ; ; ) {
            try {
                //messageHandler.displayToUser("Choose UnitOfMeasure. Enter the number corresponding to the desired option. ");
                //inputEnum(UnitOfMeasure.class);
                var unitOfMeasure = readEnum(UnitOfMeasure.class);
                return (UnitOfMeasure) unitOfMeasure;

            } catch (InputMismatchException inputMismatchException) {
                //messageHandler.displayToUser("This value must be a number. Try again. ");
                scanner.next();
            }
        }
    }

    public Organization inputOrganization(Collection<Product> products) throws NoSuchElementException, CommandInterruptionException {
        var maxId = Long.MIN_VALUE;
        for (var prod: products
        ) {
            if(prod.getManufacturer() !=  null){
                var organization = prod.getManufacturer();
                maxId = Long.max(organization.getId(), maxId);
            }
        }
        //messageHandler.displayToUser("adding organization..");
        var org = new Organization(maxId < 0 ? 1 : maxId + 1, inputName(), inputAnnualTurnover(), inputOrganizationType() );
        //messageHandler.displayToUser("done with organization..");
        return org;
    }

    public Integer inputAnnualTurnover()  throws NoSuchElementException, CommandInterruptionException {
        for ( ; ; ) {
            try {
                //messageHandler.displayToUser(String.format("Enter annual turnover. Note that value can only be from %s to %s: ", 1, Integer.MAX_VALUE));
                var str = scanner.nextLine();
                if (str.equals("")) {
                    //messageHandler.displayToUser("This value cannot be empty. Try again");
                    continue;
                }
                if(CommandsContainer.contains(str))
                    throw new CommandInterruptionException(str);
                var inp = Integer.parseInt(str);
                if(inp < 1)
                    throw new InputMismatchException();
                return inp;
            } catch (InputMismatchException | NumberFormatException inputMismatchException) {
                //messageHandler.displayToUser("This value must be a double-type number. Try again. ");
            }
        }
    }

}
