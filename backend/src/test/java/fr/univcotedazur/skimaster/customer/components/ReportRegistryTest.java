package fr.univcotedazur.skimaster.customer.components;

import fr.univcotedazur.skimaster.monitoring.components.ReportRegistry;
import fr.univcotedazur.skimaster.monitoring.connectors.GateProxy;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.GateDailyReportDTO;
import fr.univcotedazur.skimaster.monitoring.dto.report.DomainDailyReportDTO;
import fr.univcotedazur.skimaster.monitoring.dto.report.ResortDailyReportDTO;
import fr.univcotedazur.skimaster.monitoring.entities.*;
import fr.univcotedazur.skimaster.monitoring.repositories.DomainRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportRegistryTest {

    @Mock
    DomainRepository domainRepository;
    @Mock
    GateProxy gateProxy;

    @InjectMocks
    ReportRegistry reportRegistry;

    private GateConnection gate1;
    private GateConnection gate2;
    private Domain domain;

    private static final GateDailyReportDTO REPORT_DTO_1 = new GateDailyReportDTO("g1", 10, 120.0, Set.of((long) 1, (long) 2));
    private static final GateDailyReportDTO REPORT_DTO_2 = new GateDailyReportDTO("g2", 5,  60.0,  Set.of((long) 2, (long) 3));

    @BeforeEach
    void setUp() {
        gate1 = mock(GateConnection.class);
        gate2 = mock(GateConnection.class);

        domain = mock(Domain.class);
    }

    // produceTodayReport

    @Test
    void produceTodayReport_noDomains_returnsEmptyReport() {
        when(domainRepository.findAll()).thenReturn(List.of());

        ResortDailyReport result = reportRegistry.produceTodayReport();

        assertEquals(LocalDate.now(), result.getDay());
        assertTrue(result.getDomainReports().isEmpty());
        assertTrue(result.getSkiersIds().isEmpty());
    }

    @Test
    void produceTodayReport_onedomainTwoGates_aggregatesSkiers() {
        when(gate1.getURI()).thenReturn("http://gate1:8080");
        when(gate2.getURI()).thenReturn("http://gate2:8080");
        when(domainRepository.findAll()).thenReturn(List.of(domain));
        when(domain.getGateConnections()).thenReturn(Set.of(gate1, gate2));
        when(gateProxy.requestReport("http://gate1:8080")).thenReturn(Optional.of(REPORT_DTO_1));
        when(gateProxy.requestReport("http://gate2:8080")).thenReturn(Optional.of(REPORT_DTO_2));

        ResortDailyReport result = reportRegistry.produceTodayReport();

        assertEquals(1, result.getDomainReports().size());
        assertEquals(3, result.getSkiersIds().size());
    }

    @Test
    void produceTodayReport_twoDomains_mergesSkiers() {
        when(gate1.getURI()).thenReturn("http://gate1:8080");
        Domain domain2 = mock(Domain.class);
        when(domain2.getName()).thenReturn("Domaine B");
        GateConnection gate3 = mock(GateConnection.class);
        when(gate3.getURI()).thenReturn("http://gate3:8080");
        when(domain2.getGateConnections()).thenReturn(Set.of(gate3));
        when(gateProxy.requestReport("http://gate3:8080"))
                .thenReturn(Optional.of(new GateDailyReportDTO("g3", 3, 30.0, Set.of(4L))));

        when(domainRepository.findAll()).thenReturn(List.of(domain, domain2));
        when(domain.getGateConnections()).thenReturn(Set.of(gate1));
        when(gateProxy.requestReport("http://gate1:8080")).thenReturn(Optional.of(REPORT_DTO_1));

        ResortDailyReport result = reportRegistry.produceTodayReport();

        assertEquals(2, result.getDomainReports().size());
        assertTrue(result.getSkiersIds().containsAll(Set.of((long) 1, (long) 2, 4L)));
    }

    // produceDomainDailyReport

    @Test
    void produceDomainDailyReport_noGates_returnsEmptyDomainReport() {
        DomainDailyReport result = reportRegistry.produceDomainDailyReport("Dom", Set.of());

        assertEquals("Dom", result.getDomainName());
        assertTrue(result.getGateReports().isEmpty());
        assertTrue(result.getSkiersIds().isEmpty());
    }

    @Test
    void produceDomainDailyReport_gateReturnsReport_buildsCorrectly() {
        when(gate1.getURI()).thenReturn("http://gate1:8080");
        when(gateProxy.requestReport("http://gate1:8080")).thenReturn(Optional.of(REPORT_DTO_1));

        DomainDailyReport result = reportRegistry.produceDomainDailyReport("Dom", Set.of(gate1));

        assertEquals(1, result.getGateReports().size());
        GateDailyReport gateReport = result.getGateReports().get(0);
        assertEquals("g1", gateReport.getGateId());
        assertEquals(10, gateReport.getPassages());
        assertEquals(120.0, gateReport.getOpenedMinutes());
        assertEquals(Set.of((long) 1, (long) 2), result.getSkiersIds());
    }

    @Test
    void produceDomainDailyReport_gateReturnsEmpty_isSkipped() {
        when(gate1.getURI()).thenReturn("http://gate1:8080");
        when(gateProxy.requestReport("http://gate1:8080")).thenReturn(Optional.empty());

        DomainDailyReport result = reportRegistry.produceDomainDailyReport("Dom", Set.of(gate1));

        assertTrue(result.getGateReports().isEmpty());
        assertTrue(result.getSkiersIds().isEmpty());
    }

    @Test
    void produceDomainDailyReport_oneGateFailsOneSucceeds_onlySuccessIncluded() {
        when(gate1.getURI()).thenReturn("http://gate1:8080");
        when(gate2.getURI()).thenReturn("http://gate2:8080");
        when(gateProxy.requestReport("http://gate1:8080")).thenReturn(Optional.of(REPORT_DTO_1));
        when(gateProxy.requestReport("http://gate2:8080")).thenReturn(Optional.empty());

        DomainDailyReport result = reportRegistry.produceDomainDailyReport("Dom", Set.of(gate1, gate2));

        assertEquals(1, result.getGateReports().size());
        assertEquals("g1", result.getGateReports().get(0).getGateId());
    }

    // toDto

    @Test
    void toDto_gateReport_mapsCorrectly() {
        GateDailyReport entity = new GateDailyReport("g1", 10, 120.0, Set.of((long) 1, (long) 2));

        GateDailyReportDTO dto = reportRegistry.toDto(entity);

        assertEquals("g1", dto.gateId());
        assertEquals(10, dto.passages());
        assertEquals(120.0, dto.openMinutes());
        assertEquals(Set.of((long) 1, (long) 2), dto.skiers_ids());
    }

    @Test
    void toDto_domainReport_mapsCorrectly() {
        GateDailyReport gateEntity = new GateDailyReport("g1", 10, 120.0, Set.of((long) 1));
        DomainDailyReport domainEntity = new DomainDailyReport("Dom", Set.of((long) 1), List.of(gateEntity));

        DomainDailyReportDTO dto = reportRegistry.toDto(domainEntity);

        assertEquals("Dom", dto.domainName());
        assertEquals(1, dto.totalSkiers());
        assertEquals(1, dto.gateReports().size());
    }

    @Test
    void toDto_resortReport_mapsCorrectly() {
        GateDailyReport gateEntity = new GateDailyReport("g1", 5, 60.0, Set.of((long) 1));
        DomainDailyReport domainEntity = new DomainDailyReport("Dom", Set.of((long) 1), List.of(gateEntity));
        ResortDailyReport resortEntity = new ResortDailyReport(LocalDate.of(2025, 1, 1), Set.of((long) 1), List.of(domainEntity));

        ResortDailyReportDTO dto = reportRegistry.toDto(resortEntity);

        assertEquals(LocalDate.of(2025, 1, 1), dto.day());
        assertEquals(1, dto.numberOfSkiers());
        assertEquals(1, dto.domainReports().size());
    }
}