package fr.univcotedazur.skimaster.cli.commands;

import fr.univcotedazur.skimaster.cli.dto.CliPanelMessageDTO;
import fr.univcotedazur.skimaster.cli.model.PanelSeverity;
import fr.univcotedazur.skimaster.cli.dto.PanelStatusDTO;
import fr.univcotedazur.skimaster.cli.utils.Format;
import org.springframework.http.MediaType;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@ShellComponent
public class AnalyticsCommands {
    private final RestClient restClient;

    public AnalyticsCommands(RestClient restClient) {
        this.restClient = restClient;
    }

    @ShellMethod("Show dashboard (dashboard (gates status + passages + thresholds + panel message))")
    public String dashboard() {
        return Format.prettyJson(get("/analytics/dashboard"));
    }

    @ShellMethod("Watch dashboard with a refresh every simulated minute (default: 1s). Ctrl+C to stop. (dashboard-watch OPTIONAL_TIME)")
    public void dashboardWatch(@ShellOption(defaultValue = "1") long refreshSeconds) throws InterruptedException {
        Duration d = Duration.ofSeconds(Math.max(1, refreshSeconds));
        while (true) {
            System.out.println("\n=== DASHBOARD ===");
            System.out.println(Format.prettyJson(get("/analytics/dashboard")));
            Thread.sleep(d.toMillis());
        }
    }
    @ShellMethod("Generate the daily report (report-generate)")
    public String reportGenerate() {
        return Format.prettyJson(get("/analytics/reports/generate"));
    }

    @ShellMethod("Show a previously generated daily report. day format: YYYY-MM-DD (report-show DAY)")
    public String reportShow(String day) {
        return Format.prettyJson(get("/analytics/reports/daily/" + day));
    }

    @ShellMethod("Invoice all super cards (invoice-super-cards)")
    public String invoiceSuperCards() {
        return Format.prettyJson(get("/analytics/reports/invoices/super-cards"));
    }

    private Object get(String uri) {
        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(Object.class);
    }
}
