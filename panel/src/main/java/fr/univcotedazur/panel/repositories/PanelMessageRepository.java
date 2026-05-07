package fr.univcotedazur.panel.repositories;

import fr.univcotedazur.panel.entities.PanelMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PanelMessageRepository extends JpaRepository<PanelMessage, Long> {
    Optional<PanelMessage> findTopByOrderByAtDesc();
}
