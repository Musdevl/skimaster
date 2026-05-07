package fr.univcotedazur.panel.interfaces;

import fr.univcotedazur.panel.dto.PanelGateStatusDTO;
import fr.univcotedazur.panel.dto.PanelMessageDTO;
import fr.univcotedazur.panel.dto.PanelStatusDTO;
import fr.univcotedazur.panel.entities.PanelMessage;

import java.util.List;

public interface PanelManager {

    PanelStatusDTO read();

    PanelStatusDTO write(PanelMessageDTO messageDTO);

    PanelStatusDTO toDTO(List<PanelGateStatusDTO> gateStatusDTOList, PanelMessage message);

    String registerPanel();
}
