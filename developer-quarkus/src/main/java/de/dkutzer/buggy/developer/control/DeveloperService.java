package de.dkutzer.buggy.developer.control;

import de.dkutzer.buggy.developer.entity.Developer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DeveloperService {

    @Inject
    DeveloperRepository developerRepository;


    public DeveloperService(DeveloperRepository developerRepository) {
        this.developerRepository = developerRepository;
    }

    public Iterable<Developer> findAll() {
        return developerRepository.findAll();
    }
}
