package fr.univcotedazur.skimaster.monitoring.repositories;

import fr.univcotedazur.skimaster.monitoring.entities.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DomainRepository extends JpaRepository<Domain, Long> {
    Optional<Domain> findDomainByName(String name);
    Optional<Domain> findByGateConnectionsId(String gate_name);

}
