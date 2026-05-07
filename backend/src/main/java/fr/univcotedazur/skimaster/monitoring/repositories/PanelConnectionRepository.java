package fr.univcotedazur.skimaster.monitoring.repositories;

import fr.univcotedazur.skimaster.monitoring.entities.PanelConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PanelConnectionRepository extends JpaRepository<PanelConnection, String> {

    Optional<PanelConnection> findById(String panelId);
}
