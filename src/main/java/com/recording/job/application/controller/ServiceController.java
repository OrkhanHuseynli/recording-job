package com.recording.job.application.controller;


import com.recording.job.application.controller.reponse.DataResponse;
import com.recording.job.application.controller.reponse.SaveResponse;
import com.recording.job.application.data.domain.RecordInfo;
import com.recording.job.application.data.domain.RecordInfoUpdateOnly;
import com.recording.job.application.service.RecordsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "v1/process/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ServiceController {
    private String INIT_MESSAGE = "RECORD : ";
    private String GET_RECORD = "Getting a record";
    private String CREATE_RECORD = "Creating a new record";

    private final RecordsService recordsService;

    public ServiceController(RecordsService recordsService) {
        this.recordsService = recordsService;
    }

    @PostMapping("record")
    public ResponseEntity<SaveResponse> createRecord(@RequestBody RecordInfo newRecordInfo) {
        log.debug(INIT_MESSAGE, CREATE_RECORD);
        return recordsService.createRecordProcess(newRecordInfo);
    }

    @GetMapping("record/{uuid}")
    public ResponseEntity<DataResponse> getRecord(@PathVariable String uuid) {
        log.debug(INIT_MESSAGE, GET_RECORD);
        return  recordsService.getRecordProcess(uuid);
    }

    @PatchMapping("record/{uuid}")
    public ResponseEntity<SaveResponse> updateRecord(
            @RequestBody RecordInfoUpdateOnly partialUpdate, @PathVariable("uuid") String uuid) {
        partialUpdate.setRecordId(uuid);
        return recordsService.updateRecordProcess(partialUpdate, uuid);
    }

    @DeleteMapping("record/{uuid}")
    public ResponseEntity<SaveResponse> deleteRecord(@PathVariable String uuid) {
        return recordsService.deleteRecordProcess(uuid);
    }

    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    private String handleHttpMediaTypeNotAcceptableException() {
        return "acceptable MIME type:" + MediaType.APPLICATION_JSON_VALUE;
    }
}
