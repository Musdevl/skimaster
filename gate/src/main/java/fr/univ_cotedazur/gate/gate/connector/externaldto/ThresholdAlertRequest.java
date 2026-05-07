package fr.univ_cotedazur.gate.gate.connector.externaldto;

import fr.univ_cotedazur.gate.gate.entities.Severity;

public record ThresholdAlertRequest(
        String gateId,
        int currentGauge,
        int threshold,
        Severity severity
) {}
