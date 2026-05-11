package com.example.techcorp.events;

import com.example.techcorp.domain.Company;

public class BonusEvent implements GameEvent {

    @Override
    public void apply(Company company) {
        company.addCash(12000);
    }

    @Override
    public String getName() {
        return "Investor Bonus";
    }
}