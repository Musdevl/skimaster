package fr.univcotedazur.skimaster.monitoring.repositories;

import fr.univcotedazur.skimaster.monitoring.entities.PanelMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PanelMessageRepository extends JpaRepository<PanelMessage, Long> {
    Optional<PanelMessage> findTopByOrderByAtDesc();
}
