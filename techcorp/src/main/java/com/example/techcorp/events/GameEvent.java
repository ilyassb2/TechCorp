package com.example.techcorp.events;

import com.example.techcorp.domain.Company;

public interface GameEvent {
    void apply(Company company);

    String getName();
}