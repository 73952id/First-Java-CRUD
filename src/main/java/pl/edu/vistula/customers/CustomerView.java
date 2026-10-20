package pl.edu.vistula.customers;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Locale;

@Route("")
@PageTitle("Customer Directory · 73952")
public class CustomerView extends VerticalLayout {
    private static final Logger log = LoggerFactory.getLogger(CustomerView.class);
    private final CustomerService service;
    private final Grid<Customer> grid = new Grid<>(Customer.class, false);
    private final BeanValidationBinder<Customer> binder = new BeanValidationBinder<>(Customer.class);
    private final TextField name = new TextField("Name");
    private final TextField email = new TextField("Email");
    private final TextField company = new TextField("Company (optional)");
    private final TextField search = new TextField();
    private final Span count = new Span();
    private final H2 editorTitle = new H2("Add customer");
    private final Button delete = new Button("Delete", event -> confirmDelete());
    private Customer selected;

    public CustomerView(CustomerService service) {
        this.service = service;
        addClassName("customer-page");
        setWidthFull();
        Span eyebrow = new Span("FIRST JAVA CRUD / 73952");
        eyebrow.addClassName("eyebrow");
        add(eyebrow, new H1("Customer directory"),
            new Paragraph("A little order for the people you work with."));

        search.setPlaceholder("Search customers…");
        search.setAriaLabel("Search customers");
        search.setClearButtonVisible(true);
        search.setValueChangeMode(ValueChangeMode.EAGER);
        search.addValueChangeListener(event -> refresh());
        Button add = new Button("Add customer", event -> {
            search.clear();
            clearForm();
            name.focus();
        });
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout toolbar = new HorizontalLayout(search, count, add);
        toolbar.addClassName("toolbar");
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.expand(search);

        grid.addColumn(Customer::getId).setHeader("ID").setWidth("80px").setFlexGrow(0);
        grid.addColumn(Customer::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(Customer::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(Customer::getCompany).setHeader("Company").setAutoWidth(true);
        grid.setHeight("360px");
        grid.getElement().setAttribute("aria-label", "Customers");
        grid.asSingleSelect().addValueChangeListener(event -> loadForm(event.getValue()));

        name.setMaxLength(100);
        email.setMaxLength(254);
        company.setMaxLength(100);
        binder.bindInstanceFields(this);
        FormLayout form = new FormLayout(name, email, company);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("650px", 3));
        form.setWidthFull();
        Button save = new Button("Save customer", event -> save());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button clear = new Button("Clear form", event -> clearForm());
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        VerticalLayout editor = new VerticalLayout(editorTitle,
            new Paragraph("Select a row to edit, or fill in the form to add someone new."),
            form, new HorizontalLayout(save, clear, delete));
        editor.addClassName("editor");
        add(toolbar, grid, editor, new Paragraph("Java · Spring Boot · Vaadin · H2 / University collaboration project"));
        refresh();
        clearForm();
    }

    private void clearForm() {
        grid.deselectAll();
        loadForm(null);
    }

    private void loadForm(Customer customer) {
        selected = customer;
        binder.readBean(customer == null ? new Customer() : customer);
        editorTitle.setText(customer == null ? "Add customer" : "Edit customer #" + customer.getId());
        delete.setEnabled(customer != null);
    }

    private void save() {
        // Bind into a separate instance: unsuccessful writes cannot mutate the table row.
        Customer changes = new Customer();
        if (!binder.writeBeanIfValid(changes)) {
            message("Check the highlighted fields.", true);
            return;
        }
        try {
            if (selected == null) {
                service.save(changes);
            } else {
                service.save(copyForUpdate(changes));
            }
            clearForm();
            refresh();
            message("Customer saved.", false);
        } catch (RuntimeException exception) {
            log.error("Could not save customer", exception);
            message("Could not save. Your input is still in the form; please try again.", true);
        }
    }

    private Customer copyForUpdate(Customer changes) {
        return new Customer(selected.getId(), changes.getName(), changes.getEmail(), changes.getCompany());
    }

    private void confirmDelete() {
        if (selected == null) return;
        Customer customer = selected;
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete customer?");
        dialog.setText("Delete " + customer.getName() + "? This cannot be undone.");
        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(event -> {
            try {
                service.delete(customer);
                clearForm();
                refresh();
                message("Customer deleted.", false);
            } catch (RuntimeException exception) {
                log.error("Could not delete customer", exception);
                message("Could not delete the customer. Please try again.", true);
            }
        });
        dialog.open();
    }

    private void refresh() {
        String term = search.getValue().strip().toLowerCase(Locale.ROOT);
        List<Customer> all = service.findAll();
        List<Customer> shown = all.stream().filter(customer ->
            (customer.getName() + " " + customer.getEmail() + " " + customer.getCompany())
                .toLowerCase(Locale.ROOT).contains(term)).toList();
        grid.setItems(shown);
        count.setText(shown.size() + " / " + all.size() + " customers");
    }

    private void message(String text, boolean error) {
        Notification notification = Notification.show(text, 4000, Notification.Position.BOTTOM_CENTER);
        notification.addThemeVariants(error ? NotificationVariant.LUMO_ERROR : NotificationVariant.LUMO_SUCCESS);
    }
}
