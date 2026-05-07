package fr.univcotedazur.panel.components;

import fr.univcotedazur.panel.connector.interfaces.Skimaster;
import fr.univcotedazur.panel.dto.PanelGateStatusDTO;
import fr.univcotedazur.panel.dto.PanelMessageDTO;
import fr.univcotedazur.panel.dto.PanelStatusDTO;
import fr.univcotedazur.panel.entities.GateStatus;
import fr.univcotedazur.panel.entities.PanelGateStatus;
import fr.univcotedazur.panel.entities.PanelMessage;
import fr.univcotedazur.panel.entities.PanelSeverity;
import fr.univcotedazur.panel.exceptions.GateStatusAlreadyExistsException;
import fr.univcotedazur.panel.exceptions.NoGateFoundException;
import fr.univcotedazur.panel.interfaces.PanelManager;
import fr.univcotedazur.panel.repositories.PanelGateStatusRepository;
import fr.univcotedazur.panel.repositories.PanelMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class PanelService implements PanelManager {

    private final PanelMessageRepository panelMessageRepository;
    private final Skimaster skimasterProxy;
    private final SimulatedClock clock;
    private final PanelGateStatusRepository panelGateStatusRepository;

    @Value("${panel.name}")
    private String panelName;

    @Value("${panel.uri}")
    private String panelURI;

    public PanelService(PanelMessageRepository panelMessageRepository, Skimaster skimasterProxy, SimulatedClock clock, PanelGateStatusRepository panelGateStatusRepository){
        this.panelMessageRepository = panelMessageRepository;
        this.skimasterProxy = skimasterProxy;
        this.clock = clock;
        this.panelGateStatusRepository = panelGateStatusRepository;
    }


    @Override
    @Transactional
    public PanelStatusDTO read() {
        Optional<PanelMessage> latest = panelMessageRepository.findTopByOrderByAtDesc();

        List<PanelGateStatusDTO> gateStatusDtos = this.panelGateStatusRepository.findAll().stream().map(this::toDto).toList();

        if(latest.isPresent()){
            return toDTO(
                    gateStatusDtos,
                    latest.get());
        } else {
            return toDTO(gateStatusDtos, new PanelMessage(clock.now(), PanelSeverity.INFO, "Nothing for the moment..."));
        }
    }

    @Override
    @Transactional
    public PanelStatusDTO write(PanelMessageDTO messageDTO) {
        List<PanelGateStatusDTO> gateStatusDtos = this.panelGateStatusRepository.findAll().stream().map(this::toDto).toList();
        return toDTO(gateStatusDtos, panelMessageRepository.save(new PanelMessage(messageDTO.at(), messageDTO.severity(), messageDTO.message())));
    }

    @Override
    public PanelStatusDTO toDTO(List<PanelGateStatusDTO> gatesStatus, PanelMessage message){
        return new PanelStatusDTO(gatesStatus, new PanelMessageDTO(message.getAt(), message.getSeverity(), message.getMessage()));
    }

    @Override
    public String registerPanel(){
        return this.skimasterProxy.registerPanel(panelName, panelURI);
    }

    public PanelGateStatusDTO updateGateStatus(String gateName, GateStatus status, String detail){
        PanelGateStatus gateStatus = this.panelGateStatusRepository.findById(gateName)
                .orElseThrow(() -> new NoGateFoundException(gateName));

        gateStatus.setStatus(status);
        gateStatus.setDetail(detail);

        return toDto(this.panelGateStatusRepository.save(gateStatus));
    }

    public PanelGateStatusDTO addGateStatus(String gateName, GateStatus status, String detail){
        Optional<PanelGateStatus> opt = this.panelGateStatusRepository.findById(gateName);
        if(opt.isEmpty()){
            return toDto(this.panelGateStatusRepository.save(new PanelGateStatus(gateName, status, detail)));
        } else {
            throw new GateStatusAlreadyExistsException("Gate Status: " + gateName + "already exists");
        }
    }

    public String deleteGateStatus(String gateName){
        this.panelGateStatusRepository.deleteById(gateName);
        return "Gate status successfully deleted";
    }

    public PanelGateStatusDTO toDto(PanelGateStatus status){
        return new PanelGateStatusDTO(status.getName(), status.getStatus(), status.getDetail());
    }


}
