package fr.univcotedazur.skimaster.cli.commands;

import fr.univcotedazur.skimaster.cli.CliContext;
import fr.univcotedazur.skimaster.cli.model.Category;
import fr.univcotedazur.skimaster.cli.model.CliCustomer;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@ShellComponent
public class CustomerCommands {

    public static final String BASE_URI = "/customers";

    private final RestClient restClient;

    private final CliContext cliContext;

    public CustomerCommands(RestClient restClient, CliContext cliContext) {
        this.restClient = restClient;
        this.cliContext = cliContext;
    }

    @ShellMethod("Register a customer in the CoD backend (register CUSTOMER_NAME CREDIT_CARD_NUMBER CUSTOMER_CATEGORY)")
    public CliCustomer register(String name, String creditCard, String category) {
        CliCustomer res = restClient.post()
                .uri(BASE_URI)
                .body(new CliCustomer(name, creditCard, Category.valueOf(category)))
                .retrieve()
                .body(CliCustomer.class);
        cliContext.getCustomers().put(Objects.requireNonNull(res).getName(), res);
        return res;
    }

    @ShellMethod("List all known customers")
    public String customers() {
        return cliContext.getCustomers().toString();
    }

    @ShellMethod("List all customer categories")
    public String showCategories() { return Arrays.toString(Category.values()); }

    @ShellMethod("Update all known customers from server")
    public String updateCustomers() {
        Map<String, CliCustomer> customerMap = cliContext.getCustomers();
        customerMap.clear();
        CliCustomer[] customers = restClient
                .get()
                .uri(BASE_URI)
                .retrieve()
                .body(CliCustomer[].class);
        customerMap.putAll(Arrays.stream(customers)
                        .collect(Collectors.toMap(CliCustomer::getName, Function.identity()))
        );
        return customerMap.toString();
    }

}
