package fr.univcotedazur.skimaster.customer.controllers;

import fr.univcotedazur.skimaster.customer.entities.Plan;
import fr.univcotedazur.skimaster.customer.interfaces.CatalogExplorator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class PlanController {

    public static final String BASE_URI = "/plans";

    private final CatalogExplorator catalogExp;

    public PlanController(CatalogExplorator catalogExp) {
        this.catalogExp = catalogExp;
    }

    @GetMapping(path = PlanController.BASE_URI, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<Plan>> listAllPlan() {
        return ResponseEntity.ok(catalogExp.listPlans());
    }
}