package fr.univcotedazur.panel.repositories;

import fr.univcotedazur.panel.entities.PanelGateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PanelGateStatusRepository extends JpaRepository<PanelGateStatus,String> {

}
