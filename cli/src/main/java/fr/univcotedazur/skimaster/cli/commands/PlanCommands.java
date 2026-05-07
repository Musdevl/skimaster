package fr.univcotedazur.skimaster.cli.commands;

import fr.univcotedazur.skimaster.cli.model.PlanEnum;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.client.RestClient;

import java.util.Set;

@ShellComponent
public class PlanCommands {

    private final RestClient restClient;

    public PlanCommands(RestClient restClient) {
        this.restClient = restClient;
    }

    @ShellMethod("List all available plans")
    public Set<PlanEnum> plans() {
        return restClient.get()
                .uri("/plans")
                .retrieve()
                .body(new ParameterizedTypeReference<Set<PlanEnum>>() {});
    }

}
