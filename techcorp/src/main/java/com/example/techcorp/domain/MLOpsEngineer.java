package com.example.techcorp.domain;

import java.util.Random;

public class MLOpsEngineer extends Employee {

    private Random random = new Random();

    public MLOpsEngineer(
            String name,
            int skill,
            double salary
    ) {
        super(name, skill, salary);
    }

    @Override
    public int work() {

        return skill + 3 + random.nextInt(4);

    }
}