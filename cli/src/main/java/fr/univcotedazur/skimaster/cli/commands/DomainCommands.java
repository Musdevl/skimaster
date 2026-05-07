package fr.univcotedazur.skimaster.cli.commands;

import fr.univcotedazur.skimaster.cli.dto.AddPlanDomainDTO;
import fr.univcotedazur.skimaster.cli.model.Domain;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.client.RestClient;

import java.util.Set;


@ShellComponent

public class DomainCommands {

    public static final String BASE_URI = "/monitoring";

    private final RestClient restClient;

    public DomainCommands(RestClient restClient) {
        this.restClient = restClient;
    }

    @ShellMethod("List all domain (show-domains)")
    public Set<Domain> listDomains() {
        return restClient.get()
                .uri(BASE_URI + "/domains")
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<Set<Domain>>() {});
    }

    @ShellMethod("Create a new domain (create-domain DOMAIN_NAME)")
    public Domain createDomain(String domainName){
        return restClient.post()
                .uri(BASE_URI + "/domains")
                .body(domainName)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<Domain>() {});
    }

    @ShellMethod("Setup gates cards (setup-gates-cards)")
    public void setupGatesCards(){
        restClient.post()
                .uri(BASE_URI + "/setup-gates-cards")
                .retrieve()
                .toBodilessEntity();
    }

    @ShellMethod("Add plan to domain (add-plan-domain DOMAIN_ID PLAN)")
    public void addPlanDomain(Long domainId, String plan){
        restClient.post()
                .uri(BASE_URI + "/add-domain-reserved-plan")
                .body(new AddPlanDomainDTO(domainId, plan))
                .retrieve()
                .toBodilessEntity();
    }



}
