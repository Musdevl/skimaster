package fr.univcotedazur.skimaster.customer.interfaces;

import fr.univcotedazur.skimaster.customer.entities.Plan;

import java.util.Set;

public interface CatalogExplorator {

    Set<Plan> listPlans();

    Set<Plan> exploreCatalogue(String regexp);

}
