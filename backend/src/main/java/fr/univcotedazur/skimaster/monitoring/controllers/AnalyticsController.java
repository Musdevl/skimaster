package fr.univcotedazur.skimaster.monitoring.controllers;

import fr.univcotedazur.skimaster.cashier.exceptions.PaymentException;
import fr.univcotedazur.skimaster.customer.exceptions.CustomerIdNotFoundException;
import fr.univcotedazur.skimaster.monitoring.components.DashboardHandler;
import fr.univcotedazur.skimaster.monitoring.components.InvoicingHandler;
import fr.univcotedazur.skimaster.monitoring.components.ReportRegistry;
import fr.univcotedazur.skimaster.monitoring.dto.*;
import fr.univcotedazur.skimaster.monitoring.dto.report.ResortDailyReportDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/analytics", produces = APPLICATION_JSON_VALUE)
public class AnalyticsController {

    private final ReportRegistry reportRegistry;
    private final InvoicingHandler invoicingHandler;
    private final DashboardHandler dashboardHandler;

    public AnalyticsController(InvoicingHandler invoicingHandler, ReportRegistry reportRegistry, DashboardHandler dashboardHandler) {
        this.invoicingHandler = invoicingHandler;
        this.reportRegistry = reportRegistry;
        this.dashboardHandler = dashboardHandler;
    }


    @GetMapping("/dashboard")
    public DashboardDTO dashboard() {
        return dashboardHandler.getDashboard();
    }


    @GetMapping(path = "/reports/generate")
    public ResponseEntity<ResortDailyReportDTO> generateTodayReport() {
        reportRegistry.deleteExistingTodayReport();
        return ResponseEntity.ok(reportRegistry.toDto(reportRegistry.saveReport(reportRegistry.produceTodayReport())));
    }

    @GetMapping(path = "/reports/daily/{day}")
    public ResponseEntity<ResortDailyReportDTO> findByDay(@PathVariable String day) {
        return ResponseEntity.ok(reportRegistry.toDto(reportRegistry.findByDay(LocalDate.parse(day))));
    }

    @GetMapping(path = "/reports/invoices/super-cards", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<InvoicingResultDTO> invoiceSuperCards(@RequestParam(name = "day", required = false) String day) throws CustomerIdNotFoundException, PaymentException {
        LocalDate d = (day == null || day.isBlank()) ? LocalDate.now() : LocalDate.parse(day);
        InvoicingResultDTO result = invoicingHandler.invoiceSuperCardsForDay(d);
        return ResponseEntity.ok(result);
    }

}
