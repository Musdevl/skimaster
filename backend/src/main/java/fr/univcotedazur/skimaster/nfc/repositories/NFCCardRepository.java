package fr.univcotedazur.skimaster.nfc.repositories;

import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.nfc.entities.NFCCard;
import fr.univcotedazur.skimaster.customer.entities.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NFCCardRepository extends JpaRepository<NFCCard, Long> {

    Optional<NFCCard> findNFCCardById(Long id);

    List<NFCCard> findAllByPlan(Plan plan);

    List<NFCCard> findAllByPlanIn(List<Plan> plans);

    List<NFCCard> findAllByCustomer(Customer customer);

    Optional<NFCCard> findByCustomerAndPlan(Customer customer, Plan plan);
}