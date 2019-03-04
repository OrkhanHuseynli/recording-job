package com.recording.job.application.controller.reponse;

import com.recording.job.application.data.domain.Record;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DataResponse extends AbstractResponse {
    private Record record;
    private List<Record> records;
    public DataResponse(String status, String message) {
        super(status, message);
    }

}
