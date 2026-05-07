package fr.univcotedazur.panel.connector.interfaces;

import fr.univcotedazur.panel.dto.PanelGateStatusDTO;

import java.util.List;

public interface Skimaster {
    String registerPanel(String panelId, String uri);

    List<PanelGateStatusDTO> fetchGateStatus();
}