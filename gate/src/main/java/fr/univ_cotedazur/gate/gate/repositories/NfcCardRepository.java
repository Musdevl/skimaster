package fr.univ_cotedazur.gate.gate.repositories;

import fr.univ_cotedazur.gate.gate.entities.NfcCard;
import fr.univ_cotedazur.gate.gate.entities.Plan;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NfcCardRepository extends JpaRepository<NfcCard, Long> {
    List<NfcCard> findAllByPlan(Plan plan);

    Optional<NfcCard> findByNfcId(Long nfcId);
}
