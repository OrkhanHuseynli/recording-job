package com.recording.job.application.data.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@NoArgsConstructor
@Data
public class Record {
    String recordId;
    RecordInfo info;

    public Record(RecordInfo info){
        this.recordId = UUID.randomUUID().toString();
        this.info = info;
    }
}
