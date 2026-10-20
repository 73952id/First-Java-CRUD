package pl.edu.vistula.customers;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:customer-test;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class CustomerServiceTest {
    @Autowired CustomerService service;
    @Autowired CustomerRepository repository;
    @Autowired EntityManager entityManager;

    @Test
    void createsReadsUpdatesAndDeletesCustomer() {
        Customer saved = service.save(new Customer("Alex Test", "alex@example.com", "Studio"));
        Long id = saved.getId();
        repository.flush();
        entityManager.clear();
        assertThat(service.findAll()).extracting(Customer::getId).contains(id);
        service.save(new Customer(id, "Alex Updated", "new@example.com", "New Studio"));
        repository.flush();
        entityManager.clear();
        Customer updated = repository.findById(id).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Alex Updated");
        assertThat(updated.getEmail()).isEqualTo("new@example.com");
        assertThat(updated.getCompany()).isEqualTo("New Studio");
        service.delete(updated);
        repository.flush();
        entityManager.clear();
        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void rejectsBlankNameAndInvalidEmail() {
        assertThatThrownBy(() -> service.save(new Customer(" ", "alex@example.com", "")))
            .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> service.save(new Customer("Alex", "invalid", "")))
            .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void rejectsOverlongFields() {
        assertThatThrownBy(() -> service.save(new Customer("A".repeat(101), "a@example.com", "")))
            .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> service.save(new Customer("Alex", "a@example.com", "A".repeat(101))))
            .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void companyIsOptional() {
        Customer saved = service.save(new Customer("Alex", "a@example.com", ""));
        repository.flush();
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void doesNotDuplicateExamplesOnStartup() throws Exception {
        long count = repository.count();
        assertThat(count).isGreaterThanOrEqualTo(3);
        new CustomerApplication().examples(repository).run();
        assertThat(repository.count()).isEqualTo(count);
    }
}
