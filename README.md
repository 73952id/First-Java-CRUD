# Customer Directory — First Java CRUD

Java CRUD assignment by **73952id** (student ID **73952**).
A Spring Boot + Vaadin application with a browser interface, following the
[Spring CRUD with Vaadin guide](https://spring.io/guides/gs/crud-with-vaadin/).

## Run locally

Install **JDK 21**. Maven is supplied through the Maven Wrapper. The first build
needs internet access for Maven, Vaadin, and frontend dependencies.

```bash
./mvnw clean test
./mvnw spring-boot:run
```

Open **http://localhost:8080**. Stop the server with **Ctrl+C**.
On Windows use `mvnw.cmd` instead of `./mvnw`. Ensure `JAVA_HOME` points to your JDK.

In the current development environment, a temporary JDK and dependency cache are
available, so the equivalent launch command is:

```bash
JAVA_HOME=/tmp/first-java-crud-jdk MAVEN_USER_HOME=/tmp/clear-form-maven ./mvnw -Dmaven.repo.local=/tmp/clear-form-m2 spring-boot:run
```

These `/tmp` paths are machine-specific. Install a regular JDK for lasting use.

## Use the app

- **Create:** click Add customer, enter a name and valid email, optionally enter
  a company, and click Save customer.
- **Read:** see saved customers in the table. Search by name, email, or company.
- **Update:** select a row, edit the form, then click Save customer.
- **Delete:** select a row, click Delete, and confirm in the dialog. Cancel keeps it.
- **Clear form:** discard unsaved input and return to Add customer mode.

The H2 database is saved at `data/customer-directory.mv.db` and survives restarts.
Three fictional customers are inserted when the database is empty. Data files
are excluded from Git. Tests use a separate in-memory database.

## Structure

- `src/main/java/pl/edu/vistula/customers/CustomerApplication.java`: Spring Boot
  startup and example data.
- `Customer.java`: JPA entity and field validation.
- `CustomerRepository.java`: Spring Data database access.
- `CustomerService.java`: validated CRUD operations.
- `CustomerView.java`: Vaadin table, search, editor, and confirmation dialog.
- `src/main/frontend/themes/customers/styles.css`: interface styling.
- `src/main/resources/application.properties`: local server and database settings.
- `src/test/java/`: database CRUD, validation, search, and form reset tests.
- `doc/`: assignment evidence and outstanding submission steps.
- `legacy/CustomerApp.java`: original console application, including the partner's
  merged exit-confirmation contribution. It remains available for demonstration;
  Maven builds the web application. Its old `data/customers.tsv` is kept untouched
  and is not imported into H2 automatically.

## Collaboration and submission

Public repository: https://github.com/73952id/First-Java-CRUD

Owner: **73952id**. Partner: **valeriia73438**. The partner's exit-confirmation PR
was merged before the web conversion; its commit remains in the Git history and
its implementation is preserved in `legacy/CustomerApp.java`.

Contribution to the partner's project:
[Clear form PR](https://github.com/valeriia73438/First-Java-CRUD/pull/1).
The partner must review and merge it themselves.

Before submission:

- Verify both repositories are public and both students' contributions are merged.
- Commit real application, CRUD, startup, and GitHub history screenshots in `doc/`.
- Submit the public repository URL as **Online text** on Platon by
  **25 October 2026, 17:00 (Europe/Warsaw)**.

To run the preserved console version separately:

```bash
mkdir -p out
javac -d out legacy/CustomerApp.java
java -cp out CustomerApp
```
