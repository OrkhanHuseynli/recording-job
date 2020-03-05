package com.recording.job.application.service;

import com.recording.job.application.controller.reponse.DataResponse;
import com.recording.job.application.controller.reponse.SaveResponse;
import com.recording.job.application.data.domain.Record;
import com.recording.job.application.data.domain.RecordInfo;
import com.recording.job.application.data.domain.RecordInfoUpdateOnly;
import com.recording.job.application.data.domain.RecordStatus;
import com.recording.job.application.data.services.StorageOperations;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Slf4j
@Service
public class RecordsService {
    private String INIT_MESSAGE = "RECORD : ";
    private String GET_RECORD = "Getting a record";
    private String DELETE_RECORD = "Record status is set as DELETED";
    private String CREATE_RECORD = "Creating a new record";
    private String SAVED_RECORD = "A new record was stored";
    private String UPDATE_RECORD = "Record was updated : ";
    private String ERROR_TRY_AGAIN = "Please, retry your attempt after a while";
    private String FILE_NAME = "records.txt";

    private final StorageOperations<Record> storageOperations;

    public RecordsService(StorageOperations<Record> storageOperations) {
        this.storageOperations = storageOperations;
    }


    public ResponseEntity<SaveResponse> createRecordProcess(RecordInfo newRecordInfo) {
        String responseMessage;
        HttpStatus responseStatus;

        if (newRecordInfo.getRecordedData() == null) {
            responseStatus = HttpStatus.BAD_REQUEST;
            responseMessage = "Please, send correctly defined payload";
            return ResponseEntity.status(responseStatus)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new SaveResponse(responseStatus.getReasonPhrase(), responseMessage));
        }

        try {
            newRecordInfo.setCreated(new Date().toString());
            newRecordInfo.setRecordStatus(RecordStatus.NEW);
            storageOperations.create(new Record(newRecordInfo), FILE_NAME);
            responseMessage = SAVED_RECORD;
            responseStatus = HttpStatus.CREATED;
        } catch (IOException e) {
            e.printStackTrace();
            responseMessage = ERROR_TRY_AGAIN;
            responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(responseStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new SaveResponse(responseStatus.getReasonPhrase(), responseMessage));
    }

    public ResponseEntity<SaveResponse> deleteRecordProcess(String uuid){
        String responseMessage = DELETE_RECORD;
        HttpStatus responseStatus = HttpStatus.OK;
        log.debug(INIT_MESSAGE, DELETE_RECORD);
        SaveResponse dataResponse = new SaveResponse(responseStatus.getReasonPhrase(), responseMessage);
        try {
            storageOperations.delete(uuid, FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            responseMessage = ERROR_TRY_AGAIN;
            responseStatus = HttpStatus.BAD_REQUEST;
        } catch (NotFoundException e) {
            responseStatus = HttpStatus.NOT_FOUND;
            responseMessage = e.getMessage();
        }
        dataResponse.setStatus(responseStatus.getReasonPhrase());
        dataResponse.setMessage(responseMessage);
        return ResponseEntity.status(responseStatus).contentType(MediaType.APPLICATION_JSON).body(dataResponse);
    }

    public ResponseEntity<SaveResponse> updateRecordProcess(RecordInfoUpdateOnly partialUpdate, String uuid) {
        String responseMessage;
        HttpStatus responseStatus;
        partialUpdate.setRecordId(uuid);
        try {
            storageOperations.update(partialUpdate, FILE_NAME);
            responseMessage = UPDATE_RECORD + uuid;
            responseStatus = HttpStatus.OK;
        } catch (IOException e) {
            e.printStackTrace();
            responseMessage = ERROR_TRY_AGAIN;
            responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (NotFoundException e){
            responseStatus = HttpStatus.NOT_FOUND;
            responseMessage = e.getMessage();
        }
        SaveResponse dataResponse = new SaveResponse(responseStatus.getReasonPhrase(), responseMessage);
        return ResponseEntity.status(responseStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(dataResponse);
    }


    public ResponseEntity<DataResponse> getRecordProcess(String uuid){
        String responseMessage;
        HttpStatus responseStatus;
        Record record = null;
        if (storageOperations.read(FILE_NAME).containsKey(uuid)){
            record = storageOperations.read(FILE_NAME).get(uuid);
            responseStatus = HttpStatus.OK;
            responseMessage = GET_RECORD;
        } else {
            responseStatus = HttpStatus.NOT_FOUND;
            responseMessage =  "No record found with given uuid : ".concat(uuid);
        }
        DataResponse dataResponse = new DataResponse(responseStatus.getReasonPhrase(), responseMessage);
        dataResponse.setRecord(record);
        return ResponseEntity.status(responseStatus).contentType(MediaType.APPLICATION_JSON).body(dataResponse);
    }
}
