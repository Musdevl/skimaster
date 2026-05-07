package fr.univcotedazur.skimaster.monitoring.interfaces;

import fr.univcotedazur.skimaster.monitoring.entities.ResortDailyReport;

import java.time.LocalDate;

public interface ReportFinder {

    ResortDailyReport findByDay(LocalDate day);

    void deleteExistingTodayReport();

    ResortDailyReport saveReport(ResortDailyReport report);
}
