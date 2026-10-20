import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

/** A small customer CRUD application. Requires JDK 17 or newer. */
public class CustomerApp {
    record Customer(int id, String name, String email) {}

    private final List<Customer> customers = new ArrayList<>();
    private final Scanner input = new Scanner(System.in);
    private final Path file;
    private int nextId = 1;

    CustomerApp(Path file) throws IOException {
        this.file = file;
        if (Files.exists(file)) {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String[] fields = line.split("\t", -1);
                if (fields.length != 3) throw new IOException("Invalid customer data file");
                try {
                    int id = Integer.parseInt(fields[0]);
                    if (id < 1 || id == Integer.MAX_VALUE || customers.stream().anyMatch(c -> c.id() == id)) {
                        throw new IllegalArgumentException("Invalid or duplicate ID");
                    }
                    customers.add(new Customer(id, decode(fields[1]), decode(fields[2])));
                    nextId = Math.max(nextId, id + 1);
                } catch (IllegalArgumentException e) {
                    throw new IOException("Invalid customer data file", e);
                }
            }
        }
    }

    private static String encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String text) {
        return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
    }

    private void save(List<Customer> updated) throws IOException {
        Path absolute = file.toAbsolutePath();
        Files.createDirectories(absolute.getParent());
        Path temporary = Files.createTempFile(absolute.getParent(), "customers-", ".tmp");
        try {
            List<String> lines = updated.stream()
                .map(c -> c.id() + "\t" + encode(c.name()) + "\t" + encode(c.email())).toList();
            Files.write(temporary, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporary, absolute, java.nio.file.StandardCopyOption.ATOMIC_MOVE,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                Files.move(temporary, absolute, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            customers.clear();
            customers.addAll(updated);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private String ask(String prompt) {
        System.out.print(prompt);
        if (!input.hasNextLine()) throw new java.util.NoSuchElementException();
        return input.nextLine().trim();
    }

    private String required(String prompt) {
        while (true) {
            String value = ask(prompt);
            if (!value.isBlank()) return value;
            System.out.println("Value cannot be empty.");
        }
    }

    private String email() {
        while (true) {
            String value = required("Email: ");
            if (value.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) return value;
            System.out.println("Enter an email such as alex@example.com.");
        }
    }

    private void list() {
        if (customers.isEmpty()) System.out.println("No customers yet.");
        else {
            System.out.println("ID | Name | Email");
            customers.forEach(c -> System.out.println(c.id() + " | " + c.name() + " | " + c.email()));
        }
    }

    private void create() throws IOException {
        if (nextId == Integer.MAX_VALUE) {
            System.out.println("Customer ID limit reached.");
            return;
        }
        Customer customer = new Customer(nextId, required("Name: "), email());
        List<Customer> updated = new ArrayList<>(customers);
        updated.add(customer);
        save(updated);
        nextId++;
        System.out.println("Created customer " + customer.id() + ".");
    }

    private void change(boolean delete) throws IOException {
        int id;
        try {
            id = Integer.parseInt(ask("Customer ID: "));
        } catch (NumberFormatException e) {
            System.out.println("Enter a numeric customer ID.");
            return;
        }
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).id() != id) continue;
            List<Customer> updated = new ArrayList<>(customers);
            if (delete) {
                if (!ask("Delete customer " + id + "? Type yes: ").equalsIgnoreCase("yes")) {
                    System.out.println("Deletion cancelled.");
                    return;
                }
                updated.remove(i);
            } else {
                updated.set(i, new Customer(id, required("New name: "), email()));
            }
            save(updated);
            System.out.println((delete ? "Deleted" : "Updated") + " customer " + id + ".");
            return;
        }
        System.out.println("Customer not found.");
    }

    private void run() {
        System.out.println("First Java CRUD started successfully.");
        System.out.println("Data file: " + file.toAbsolutePath());
        try {
            while (true) {
                System.out.println("\n1 List customers | 2 Create | 3 Update | 4 Delete | 0 Exit");
                try {
                    switch (ask("Choose: ")) {
                        case "1" -> list();
                        case "2" -> create();
                        case "3" -> change(false);
                        case "4" -> change(true);
                        case "0" -> { System.out.println("Goodbye!"); return; }
                        default -> System.out.println("Choose 0, 1, 2, 3 or 4.");
                    }
                } catch (IOException e) {
                    System.out.println("Could not save changes: " + e.getMessage());
                }
            }
        } catch (java.util.NoSuchElementException e) {
            System.out.println("\nInput closed. Goodbye!");
        }
    }

    public static void main(String[] args) {
        try {
            new CustomerApp(Path.of(args.length > 0 ? args[0] : "data/customers.tsv")).run();
        } catch (IOException e) {
            System.err.println("Cannot start: " + e.getMessage());
            System.exit(1);
        }
    }
}
