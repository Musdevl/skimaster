package fr.univcotedazur.panel.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SimulatedClock {

    private final long acceleration;
    private final Instant realStart;
    private final Instant simulatedStart;

    public SimulatedClock(@Value("${skimaster.simulation.acceleration:60}") long acceleration) {
        this.acceleration = Math.max(1, acceleration);
        this.realStart = Instant.now();
        this.simulatedStart = Instant.now();
    }

    public Instant now() {
        long realElapsedMillis = Instant.now().toEpochMilli() - realStart.toEpochMilli();
        long simulatedElapsedMillis = Math.multiplyExact(realElapsedMillis, acceleration);
        return Instant.ofEpochMilli(simulatedStart.toEpochMilli() + simulatedElapsedMillis);
    }
}
