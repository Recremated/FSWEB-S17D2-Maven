package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.Developer;
import com.workintech.s17d2.model.JuniorDeveloper;
import com.workintech.s17d2.model.MidDeveloper;
import com.workintech.s17d2.model.SeniorDeveloper;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    public Map<Integer, Developer> developers;

    private final Taxable developerTax;

    @Autowired
    public DeveloperController(Taxable developerTax) {
        this.developerTax = developerTax;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getAllDevelopers() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public Developer getDeveloperById(@PathVariable Integer id) {
        return developers.get(id);
    }

    @PostMapping
    public ResponseEntity<Developer> addDeveloper(@RequestBody Developer developer) {
        Developer newDev;
        double salary = developer.getSalary();

        switch (developer.getExperience()) {
            case JUNIOR:
                salary = salary - (salary * developerTax.getSimpleTaxRate() / 100);
                newDev = new JuniorDeveloper(developer.getId(), developer.getName(), salary);
                break;
            case MID:
                salary = salary - (salary * developerTax.getMiddleTaxRate() / 100);
                newDev = new MidDeveloper(developer.getId(), developer.getName(), salary);
                break;
            case SENIOR:
                salary = salary - (salary * developerTax.getUpperTaxRate() / 100);
                newDev = new SeniorDeveloper(developer.getId(), developer.getName(), salary);
                break;
            default:
                throw new IllegalArgumentException("Unknown experience level");
        }
        developers.put(newDev.getId(), newDev);
        return new ResponseEntity<>(newDev, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public Developer updateDeveloper(@PathVariable Integer id, @RequestBody Developer updatedDeveloper) {
        if (!developers.containsKey(id)) {
            throw new NoSuchElementException("Developer not found");
        }
        developers.put(id, updatedDeveloper);
        return updatedDeveloper;
    }

    @DeleteMapping("/{id}")
    public String deleteDeveloper(@PathVariable Integer id) {
        developers.remove(id);
        return "Developer with id " + id + " deleted.";
    }
}