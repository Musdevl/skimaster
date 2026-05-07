package fr.univcotedazur.skimaster.monitoring.repositories;

import fr.univcotedazur.skimaster.monitoring.entities.ResortDailyReport;import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyReportRepository extends JpaRepository<ResortDailyReport, Long> {

    @Query("""
        SELECT r FROM ResortDailyReport r
        LEFT JOIN FETCH r.skiers_ids
        LEFT JOIN FETCH r.domainReports d
        LEFT JOIN FETCH d.skiers_ids
        LEFT JOIN FETCH d.gateReports g
        LEFT JOIN FETCH g.skiers_ids
        WHERE r.reportDay = :day
    """)
    Optional<ResortDailyReport> findByReportDayWithFullGraph(@Param("day") LocalDate day);


    Optional<ResortDailyReport> findByReportDay(LocalDate reportDay);

    void deleteByReportDay(LocalDate reportDay);

}
