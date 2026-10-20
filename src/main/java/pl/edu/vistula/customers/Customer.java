package pl.edu.vistula.customers;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Enter a customer name")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Enter an email address")
    @Email(message = "Enter a valid email address")
    @Size(max = 254)
    private String email;

    @Size(max = 100)
    private String company;

    public Customer() { }

    public Customer(String name, String email, String company) {
        this.name = name;
        this.email = email;
        this.company = company;
    }

    Customer(Long id, String name, String email, String company) {
        this(name, email, company);
        this.id = id;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
}
