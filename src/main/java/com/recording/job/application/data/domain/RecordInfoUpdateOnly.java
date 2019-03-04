package com.recording.job.application.data.domain;

import lombok.Data;

import java.util.List;

@Data
public class RecordInfoUpdateOnly {
    private String recordId;
    private byte[] recordedData;
}
