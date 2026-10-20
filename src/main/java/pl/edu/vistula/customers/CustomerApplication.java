package pl.edu.vistula.customers;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.List;

@SpringBootApplication
@Theme("customers")
public class CustomerApplication implements AppShellConfigurator {
    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }

    @Bean
    CommandLineRunner examples(CustomerRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                    new Customer("Alex Morgan", "alex@example.com", "North Studio"),
                    new Customer("Jamie Chen", "jamie@example.com", "Paper & Co."),
                    new Customer("Sam Rivera", "sam@example.com", "Rivera Design")
                ));
            }
        };
    }
}
