package com.recording.job.application.data.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecordInfo {
    private RecordStatus recordStatus;
    private String created;
    private List<String> updated;
    private String deleted;
    private byte[] recordedData;

    public RecordInfo(String created, byte[] recordedData){
        this.created = created;
        this.recordedData = recordedData;
        this.updated = new ArrayList<>();
    }


}
