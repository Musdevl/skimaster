package fr.univcotedazur.skimaster.customer.components;

import fr.univcotedazur.skimaster.customer.interfaces.CatalogExplorator;
import fr.univcotedazur.skimaster.customer.entities.Plan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class Catalog implements CatalogExplorator {

    @Override
    @Transactional(readOnly = true)
    public Set<Plan> listPlans() {
        return EnumSet.allOf(Plan.class).stream().filter(Plan::isSubscription).collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Plan> exploreCatalogue(String regexp) {
        return EnumSet.allOf(Plan.class).stream().filter(plan -> plan.name().matches(regexp) && plan.isSubscription()).collect(Collectors.toSet());
    }

}