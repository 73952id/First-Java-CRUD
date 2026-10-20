package pl.edu.vistula.customers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.List;

@Service
@Validated
public class CustomerService {
    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public List<Customer> findAll() {
        return repository.findAll(Sort.by("name", "id"));
    }

    public Customer save(@Valid Customer customer) {
        return repository.save(customer);
    }

    public void delete(Customer customer) {
        repository.deleteById(customer.getId());
    }
}
