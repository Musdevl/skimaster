package fr.univ_cotedazur.gate.gate.repositories;

import fr.univ_cotedazur.gate.gate.entities.NfcCardScan;
import fr.univ_cotedazur.gate.gate.entities.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NfcCardScanRepository extends JpaRepository<NfcCardScan, Long> {

    @Query("SELECT s.nfcId FROM NfcCardScan s " +
            "JOIN NfcCard c ON s.nfcId = c.nfcId " +
            "WHERE s.scannedAt BETWEEN :start AND :end AND c.plan = :plan")
    List<Long> findScannedNfcIdsByPlan(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("plan") Plan plan
    );

    @Query("SELECT DISTINCT s.nfcId FROM NfcCardScan s " +
            "WHERE s.scannedAt BETWEEN :start AND :end")
    List<Long> findScannedNfcIds(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT COUNT(s) FROM NfcCardScan s WHERE s.scannedAt BETWEEN :start AND :end")
    long countScansInInterval(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
