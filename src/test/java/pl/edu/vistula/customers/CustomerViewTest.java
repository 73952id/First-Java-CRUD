package pl.edu.vistula.customers;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomerViewTest {
    private static Stream<Component> children(Component parent) {
        return Stream.concat(Stream.of(parent), parent.getChildren().flatMap(CustomerViewTest::children));
    }

    private static <T extends Component> T find(CustomerView view, Class<T> type) {
        return children(view).filter(type::isInstance).map(type::cast).findFirst().orElseThrow();
    }

    private static Button button(CustomerView view, String text) {
        return children(view).filter(Button.class::isInstance).map(Button.class::cast)
            .filter(button -> button.getText().equals(text)).findFirst().orElseThrow();
    }

    private static List<TextField> fields(CustomerView view) {
        return children(view).filter(TextField.class::isInstance).map(TextField.class::cast)
            .filter(field -> field.getLabel() != null && !field.getLabel().isEmpty()).toList();
    }

    @Test
    @SuppressWarnings("unchecked")
    void clearDiscardsEditsWithoutChangingSavedCustomer() {
        CustomerService service = mock(CustomerService.class);
        Customer customer = new Customer(12L, "Alex", "alex@example.com", "Studio");
        when(service.findAll()).thenReturn(List.of(customer));
        CustomerView view = new CustomerView(service);
        Grid<Customer> grid = find(view, Grid.class);
        grid.select(customer);
        assertThat(find(view, H2.class).getText()).isEqualTo("Edit customer #12");
        assertThat(button(view, "Delete").isEnabled()).isTrue();
        fields(view).forEach(field -> field.setValue("Unsaved change"));
        clearInvocations(service);
        button(view, "Clear form").click();
        assertThat(fields(view)).hasSize(3).allSatisfy(field -> assertThat(field.getValue()).isEmpty());
        assertThat(grid.getSelectedItems()).isEmpty();
        assertThat(find(view, H2.class).getText()).isEqualTo("Add customer");
        assertThat(button(view, "Delete").isEnabled()).isFalse();
        verifyNoInteractions(service);
        grid.select(customer);
        assertThat(fields(view)).extracting(TextField::getValue)
            .containsExactly("Alex", "alex@example.com", "Studio");
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchFiltersNamesEmailsAndCompaniesIgnoringCase() {
        CustomerService service = mock(CustomerService.class);
        Customer alex = new Customer("Alex", "alex@example.com", "North");
        Customer sam = new Customer("Sam", "sam@example.com", "South");
        when(service.findAll()).thenReturn(List.of(alex, sam));
        CustomerView view = new CustomerView(service);
        TextField search = find(view, TextField.class);
        Grid<Customer> grid = find(view, Grid.class);
        for (String query : List.of("ALEX", "alex@", "north")) {
            search.setValue(query);
            assertThat(grid.getListDataView().getItems().toList()).containsExactly(alex);
        }
        search.setValue("no match");
        assertThat(grid.getListDataView().getItems().toList()).isEmpty();
        search.clear();
        assertThat(grid.getListDataView().getItems().toList()).containsExactly(alex, sam);
    }
}
