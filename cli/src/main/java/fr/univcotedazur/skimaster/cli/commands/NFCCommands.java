package fr.univcotedazur.skimaster.cli.commands;

import fr.univcotedazur.skimaster.cli.model.CliNfcCard;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.client.RestClient;

import java.util.Set;

@ShellComponent
public class NFCCommands {
    public static final String BASE_URI = "/nfc-cards";

    private final RestClient restClient;

    public NFCCommands(RestClient restClient) {
        this.restClient = restClient;
    }

    @ShellMethod("Show all nfc cards")
    public Set<CliNfcCard> nfcCards() {
        return restClient.get()
                .uri(BASE_URI)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<Set<CliNfcCard>>() {});
    }
}
