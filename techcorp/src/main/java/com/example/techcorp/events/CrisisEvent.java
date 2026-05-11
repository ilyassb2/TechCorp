package com.example.techcorp.events;

import com.example.techcorp.domain.Company;

public class CrisisEvent implements GameEvent {

    @Override
    public void apply(Company company) {
        company.reduceCash(15000);
    }

    @Override
    public String getName() {
        return "Market Crisis";
    }
}