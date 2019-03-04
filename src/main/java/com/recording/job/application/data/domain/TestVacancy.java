package com.recording.job.application.data.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TestVacancy {
    String name;
    String description;

    public TestVacancy(String name, String description){
        this.name = name;
        this.description = description;
    }
}
