package fr.univcotedazur.skimaster.monitoring.repositories;

import fr.univcotedazur.skimaster.monitoring.entities.GateConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GateConnectionRepository extends JpaRepository<GateConnection, String> {

}