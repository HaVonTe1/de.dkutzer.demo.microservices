package de.dkutzer.buggy.developer.entity;

public class DeveloperCreatedEvent {

    private Developer developer;

    public DeveloperCreatedEvent(Developer developer) {
        this.developer = developer;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }
}
