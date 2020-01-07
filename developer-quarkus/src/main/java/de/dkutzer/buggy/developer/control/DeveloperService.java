package de.dkutzer.buggy.developer.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.dkutzer.buggy.developer.entity.Developer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

@ApplicationScoped
public class DeveloperService {

    @Inject
    DeveloperRepository developerRepository;

    @Inject
    DeveloperGateway developerGateway;


    public DeveloperService(DeveloperRepository developerRepository) {
        this.developerRepository = developerRepository;
    }

    public Iterable<Developer> findAll() {
        return developerRepository.findAll();
    }

    public Developer findById(String id) {
        return developerRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public boolean deleteById(String id) {

        if (developerRepository.exists(id)) {
            developerRepository.delete(id);
            return true;
        }
        return false;
    }

    public Developer upsert(Developer developer) throws JsonProcessingException {

        developerRepository.upsert(developer);
        developerGateway.created(developer);
        return developer;
    }

    public boolean exists(String id) {

        return (id != null && !id.isEmpty()) && developerRepository.exists(id);
    }
}
