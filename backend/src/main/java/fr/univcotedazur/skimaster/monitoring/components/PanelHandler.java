package fr.univcotedazur.skimaster.monitoring.components;

import fr.univcotedazur.skimaster.monitoring.interfaces.PanelProcessor;
import fr.univcotedazur.skimaster.monitoring.connectors.PanelProxy;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.PanelStatusDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.interfaces.Panel;
import fr.univcotedazur.skimaster.monitoring.dto.panel.PanelMessageDTO;

import fr.univcotedazur.skimaster.monitoring.entities.GateStatus;
import fr.univcotedazur.skimaster.monitoring.entities.PanelConnection;
import fr.univcotedazur.skimaster.monitoring.entities.PanelSeverity;
import fr.univcotedazur.skimaster.monitoring.exceptions.PanelNotFoundException;
import fr.univcotedazur.skimaster.monitoring.repositories.PanelConnectionRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;import java.util.List;
import java.util.Optional;

@Component
public class PanelHandler implements PanelProcessor {

    private final Panel panelProxy;
    private final PanelConnectionRepository panelConnectionRepository;
    private final SimulatedClock clock;

    public PanelHandler(PanelProxy panelProxy, SimulatedClock clock, PanelConnectionRepository panelConnectionRepository) {
        this.panelProxy = panelProxy;
        this.clock = clock;
        this.panelConnectionRepository = panelConnectionRepository;
    }

    @Override
    public PanelStatusDTO read(String panelId) {
        Optional<PanelConnection> panelConnection = panelConnectionRepository.findById(panelId);
        if(panelConnection.isPresent()){

            Optional<PanelStatusDTO> optionalPanelStatusDTO = panelProxy.read(panelConnection.get().getURI());

            return optionalPanelStatusDTO.orElseGet(() -> new PanelStatusDTO(new ArrayList<>(),
                    new PanelMessageDTO(clock.now(), PanelSeverity.INFO, "No message for the moment...")));
        } else {
            throw new PanelNotFoundException("Panel with id: " + panelId + " not found");
        }

    }

    @Override
    public PanelStatusDTO write(String panelId, String message, PanelSeverity severity) {
        Optional<PanelConnection> panelConnection = panelConnectionRepository.findById(panelId);
        if(panelConnection.isPresent()){
            Optional<PanelStatusDTO> optionalPanelStatusDTO = panelProxy.write(
                    panelConnection.get().getURI(),
                    new PanelMessageDTO(clock.now(), severity, message)
            );

            return optionalPanelStatusDTO.orElseGet(() -> new PanelStatusDTO(
                    new ArrayList<>(),
                    new PanelMessageDTO(clock.now(), PanelSeverity.INFO, "No message for the moment..."))
            );
        } else {
            throw new PanelNotFoundException("Panel with id: " + panelId + " not found");
        }
    }

    @Override
    public void write(String message, PanelSeverity severity) {
        List<PanelConnection> panelConnections = panelConnectionRepository.findAll();

        for(PanelConnection panelConnection : panelConnections){
            panelProxy.write(panelConnection.getURI(), new PanelMessageDTO(clock.now(), severity, message));
        }
    }

    @Override
    public void updateGateStatus(String gateName, GateStatus status, String detail){
        List<PanelConnection> panelConnections = panelConnectionRepository.findAll();

        for(PanelConnection panelConnection: panelConnections){
            panelProxy.updateGateStatus(panelConnection.getURI(), gateName, status, detail);
        }
    }

    @Override
    public String addGateStatus(String panelId, String gateName, GateStatus status, String detail){
        PanelConnection panelConnection = panelConnectionRepository.findById(panelId).orElseThrow(
                () -> new PanelNotFoundException("Panel " + panelId + "not found"));
        return panelProxy.addGateStatus(panelConnection.getURI(), gateName, status, detail)
                .orElse("Failed to save gate status");

    }

    @Override
    public String removeGateStatus(String panelId, String gateName){
        PanelConnection panelConnection = panelConnectionRepository.findById(panelId).orElseThrow(
                () -> new PanelNotFoundException("Panel " + panelId + "not found"));
        return panelProxy.removeGateStatus(panelConnection.getURI(), gateName)
                .orElse("Failed to remove gate status");
    }

    @Override
    public void registerPanel(String panelId, String uri){
        PanelConnection newCon = new PanelConnection(panelId, uri);
        panelConnectionRepository.save(newCon);
    }

}
