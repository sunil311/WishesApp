package com.impetus.mailsender.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Filter {
    @Value("${filter.clients}")
    private String clients[];

    @Value("${filter.occations}")
    private String occations[];

    @Value("${filter.locations}")
    private String locations[];

    public String[] getClients() {
        return clients;
    }

    public void setClients(String[] clients) {
        this.clients = clients;
    }

    public String[] getOccations() {
        return occations;
    }

    public void setOccations(String[] occations) {
        this.occations = occations;
    }

    public String[] getLocations() {
        return locations;
    }

    public void setLocations(String[] locations) {
        this.locations = locations;
    }
}
