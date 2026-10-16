import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

// TOPIC 4: Object-Oriented Modelling
class Product 
{
    private final int id, price;
    private final String name;
    private int stock;

    Product(int id, String name, int price, int stock) 
    {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    int getId() { return id; }
    String getName() { return name; }
    int getPrice() { return price; }
    int getStock() { return stock; }

    void dispense() 
    {
        if (stock <= 0)
            throw new IllegalStateException("Out of stock.");
        stock--;
    }

    public String toString() {
        return id + ". " + name + " - Rs." + price
                + " (Stock: " + stock + ")";
    }
}

// TOPIC 4: Encapsulation
class Purchase 
{
    private final String productName;
    private final int amountPaid, change;

    Purchase(String name, int paid, int change) 
    {
        productName = name;
        amountPaid = paid;
        this.change = change;
    }

    String getProductName() { return productName; }
    int getAmountPaid() { return amountPaid; }
    int getChange() { return change; }

    public String toString() 
    {
        return productName + ", Paid: Rs." + amountPaid
                + ", Change: Rs." + change;
    }
}

public class VendingMachineApp 
{

    // TOPIC 3: Arrays
    static Product[] products = 
    {
        new Product(1, "Water", 20, 5),
        new Product(2, "Chips", 30, 4),
        new Product(3, "Chocolate", 40, 3),
        new Product(4, "Juice", 50, 4),
        new Product(5, "Biscuits", 25, 6)
    };

    // TOPIC 6: Collections
    static List<Purchase> purchases = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);
    static final String FILE = "purchase_history.txt";

    public static void main(String[] args) 
    {
        int choice;

        // TOPIC 2: Iteration and selection
        do {
            System.out.println("\n===== VENDING MACHINE =====");
            System.out.println("1. View Products");
            System.out.println("2. Buy Product");
            System.out.println("3. Search Product");
            System.out.println("4. Purchase Summary");
            System.out.println("5. Saved History");
            System.out.println("6. Exit");

            choice = readInt("Enter choice: ");

            switch (choice) 
            {
                case 1:
                    displayProducts();
                    break;
                case 2:
                    buyProduct();
                    break;
                case 3:
                    searchProduct();
                    break;
                case 4:
                    showSummary();
                    break;
                case 5:
                    readHistory();
                    break;
                case 6:
                    System.out.println("Thank you!");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 6);

        sc.close();
    }

    // TOPIC 2 and 3: Loops and methods
    static void displayProducts() 
    {
        System.out.println("\nAvailable Products:");
        for (Product p : products)
            System.out.println(p);
    }

    static void buyProduct() 
    {
        displayProducts();
        Product p = findProduct(readInt("Enter product ID: "), 0);

        if (p == null) 
        {
            System.out.println("Product not found.");
            return;
        }

        if (p.getStock() <= 0) 
        {
            System.out.println("Out of stock.");
            return;
        }

        int paid = readInt("Enter money: ");

        if (paid < 0 || paid < p.getPrice()) 
        {
            System.out.println(paid < 0
                    ? "Invalid amount." : "Insufficient money.");
            return;
        }

        try 
        {
            p.dispense();
            Purchase purchase = new Purchase(
                    p.getName(), paid, paid - p.getPrice());

            purchases.add(purchase);
            System.out.println("Dispensed: " + p.getName());
            System.out.println("Change: Rs." + purchase.getChange());
            savePurchase(purchase);

        } catch (IllegalStateException e) 
        {
            System.out.println(e.getMessage());
        }
    }

    // TOPIC 3: Recursion
    static Product findProduct(int id, int i) 
    {
        if (i >= products.length)
            return null;
        if (products[i].getId() == id)
            return products[i];
        return findProduct(id, i + 1);
    }

    // TOPIC 5: Strings and library algorithms
    static void searchProduct() 
    {
        System.out.println("Enter product name:");
        String name = sc.nextLine().trim();
        Product found = null;

        for (Product p : products) 
            {
            if (p.getName().equalsIgnoreCase(name)) 
            {
                found = p;
                break;
            }
        }

        System.out.println(found == null
                ? "Product not found." : "Found: " + found);

        Arrays.sort(products, Comparator.comparing(Product::getName));
        System.out.println("Products sorted alphabetically.");
    }

    // TOPIC 6: Streams, lambdas and grouping
    static void showSummary() 
    {
        System.out.println("\n===== PURCHASE SUMMARY =====");

        if (purchases.isEmpty()) 
        {
            System.out.println("No purchases yet.");
            return;
        }

        purchases.stream().forEach(p -> System.out.println(p));

        int paid = purchases.stream()
                .mapToInt(Purchase::getAmountPaid).sum();

        int change = purchases.stream()
                .mapToInt(Purchase::getChange).sum();

        Map<String, Long> counts = purchases.stream().collect(
                Collectors.groupingBy(
                        Purchase::getProductName,
                        Collectors.counting()));

        System.out.println("Total paid: Rs." + paid);
        System.out.println("Total change: Rs." + change);
        System.out.println("Items purchased: " + counts);
    }

    // TOPIC 5: File output and exception handling
    static void savePurchase(Purchase p) {
        try {
            Files.write(Paths.get(FILE),
                    Collections.singletonList(p.toString()),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

            System.out.println("Purchase saved.");

        } catch (IOException e) {
            System.out.println("Could not save history.");
        }
    }

    // TOPIC 5: File input
    static void readHistory() 
    {
        try {
            Path path = Paths.get(FILE);

            if (!Files.exists(path)) 
            {
                System.out.println("No saved history.");
                return;
            }

            System.out.println("\n===== SAVED HISTORY =====");
            for (String line : Files.readAllLines(path))
                System.out.println(line);

        } catch (IOException e) 
        {
            System.out.println("Could not read history.");
        }
    }

    // TOPIC 4: Input validation and exception handling
    static int readInt(String message) 
    {
        while (true) 
        {
            System.out.println(message);

            try 
            {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid whole number.");
            }
        }
    }
}