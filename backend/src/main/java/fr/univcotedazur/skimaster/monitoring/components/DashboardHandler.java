package fr.univcotedazur.skimaster.monitoring.components;

import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.PanelStatusDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.interfaces.Gate;
import fr.univcotedazur.skimaster.monitoring.connectors.interfaces.Panel;
import fr.univcotedazur.skimaster.monitoring.dto.DashboardDTO;
import fr.univcotedazur.skimaster.monitoring.entities.GateConnection;
import fr.univcotedazur.skimaster.monitoring.entities.PanelConnection;
import fr.univcotedazur.skimaster.monitoring.interfaces.DashboardProcessor;
import fr.univcotedazur.skimaster.monitoring.repositories.GateConnectionRepository;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.GateStatusDTO;
import fr.univcotedazur.skimaster.monitoring.repositories.PanelConnectionRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DashboardHandler implements DashboardProcessor {

    private final GateConnectionRepository gateConnectionRepository;
    private final SimulatedClock clock;
    private final Gate gateProxy;
    private final Panel panelProxy;
    private final PanelConnectionRepository panelConnectionRepository;

    public DashboardHandler(GateConnectionRepository gateConnectionRepository,
                            Panel panelProxy,
                            SimulatedClock clock,
                            Gate gateProxy,
                            PanelConnectionRepository panelConnectionRepository) {
        this.gateConnectionRepository = gateConnectionRepository;
        this.panelProxy = panelProxy;
        this.clock = clock;
        this.gateProxy = gateProxy;
        this.panelConnectionRepository = panelConnectionRepository;
    }

    @Override
    public DashboardDTO getDashboard() {
        List<GateStatusDTO> gates = new ArrayList<>();
        List<GateConnection> gateConnections = gateConnectionRepository.findAll();

        for(GateConnection gateConnection : gateConnections){
            Optional<GateStatusDTO> optionalGateStatusDTO = gateProxy.requestStatus(gateConnection.getURI());
            optionalGateStatusDTO.ifPresent(gates::add);
        }


        List<PanelStatusDTO> panels = new ArrayList<>();
        List<PanelConnection> panelConnections = panelConnectionRepository.findAll();

        for(PanelConnection panelConnection : panelConnections){
            Optional<PanelStatusDTO> optionalPanelMessageDTO = panelProxy.read(panelConnection.getURI());
            optionalPanelMessageDTO.ifPresent(panels::add);
        }

        System.out.println("Connections: " + panelConnections.size() + " Panels: " + panels.size());
        return new DashboardDTO(clock.now(), gates, panels);
    }
}
