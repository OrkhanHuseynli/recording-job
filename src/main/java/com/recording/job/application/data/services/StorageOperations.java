package com.recording.job.application.data.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recording.job.application.data.domain.Record;
import com.recording.job.application.data.domain.RecordInfoUpdateOnly;
import com.recording.job.application.data.domain.RecordStatus;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class StorageOperations <T extends Object>{

    @Autowired
    private ObjectMapper objectMapper;

    public void create(T object, String FILE_NAME) throws IOException {
        write(convertObjToJsonString(object), FILE_NAME);
    }

    public HashMap<String, Record> read(String FILE_NAME){
        String tmdir = getStringPath(FILE_NAME);
        Path path = Paths.get(tmdir);
        Stream<String> lines;
        HashMap<String, Record> hashMap = new HashMap<>();
        try {

            lines = Files.lines(path);
            List<String> list = lines.collect(Collectors.toList());
            list.forEach(json -> populateHashMap(json, hashMap));
            lines.close();
        } catch (NoSuchFileException e) {
            log.debug("NO SUCH FILE : " + tmdir);
            createFile(tmdir);
        }catch (IOException e) {
            e.printStackTrace();
            log.debug("ERROR WHEN READING A FILE");
        }
        return  hashMap;
    }

    public void delete(String uuid, String FILE_NAME) throws IOException, NotFoundException {
        HashMap<String, Record> hashMap = read(FILE_NAME);
        if(hashMap.containsKey(uuid)){
            Record record = hashMap.get(uuid);
            record.getInfo().setDeleted(new Date().toString());
            record.getInfo().setRecordStatus(RecordStatus.DELETED);
            updateStorage(FILE_NAME, uuid, record);
        } else {
            throw new NotFoundException("Record was not found");
        }
    }

    public void update(RecordInfoUpdateOnly updateOnly, String FILE_NAME) throws IOException, NotFoundException {
        HashMap<String, Record> hashMap = read(FILE_NAME);
        if(hashMap.containsKey(updateOnly.getRecordId())){
            Record record = hashMap.get(updateOnly.getRecordId());
            try{
                record.getInfo().getUpdated().add(new Date().toString());
            } catch (NullPointerException e){
                List<String> list = new ArrayList<>();
                list.add(new Date().toString());
                record.getInfo().setUpdated(list);
            }
            record.getInfo().setRecordedData(updateOnly.getRecordedData());
            record.getInfo().setRecordStatus(RecordStatus.UPDATED);
            updateStorage(FILE_NAME, updateOnly.getRecordId(), record);
        } else {
            throw new NotFoundException("Record was not found");
        }
    }

    public void write(String data, String FILE_NAME) throws IOException {
        try {
            Stream<String> lines = Files.lines(Paths.get(getStringPath(FILE_NAME)));
            String collectedData = lines.collect(Collectors.joining("\n"));
            collectedData = String.join("\n",collectedData, data);
            Files.write(Paths.get(getStringPath(FILE_NAME)), collectedData.getBytes());
        } catch (NoSuchFileException e) {
            log.debug("NO SUCH FILE : " + getStringPath(FILE_NAME));
            Files.write( createFile(getStringPath(FILE_NAME)), data.getBytes());
        }
    }

    private void updateStorage(String FILE_NAME, String uuid, Record record) throws IOException {
        try {
            Stream<String> lines = Files.lines(Paths.get(getStringPath(FILE_NAME)));
            String filteredCollection = lines.filter(line->!line.contains(uuid)).collect(Collectors.joining("\n"));
            String updatedRecordAsJsonString  = objectMapper.writeValueAsString(record);
            filteredCollection = String.join("\n", filteredCollection, updatedRecordAsJsonString);
            Files.write(Paths.get(getStringPath(FILE_NAME)), filteredCollection.getBytes());
        } catch (NoSuchFileException e) {
            log.debug("NO SUCH FILE : " + getStringPath(FILE_NAME));
            createFile(getStringPath(FILE_NAME));
        }
    }


    private void populateHashMap(String json, HashMap hashMap){
        try {
            Record record = objectMapper.readValue(json.trim(), Record.class);
            hashMap.put(record.getRecordId(), record);
        } catch (IOException e) {
            log.debug("json doesn't conform with the record type");
        }
    }

    private String getStringPath(String FILE_NAME){
        return System.getProperty("java.io.tmpdir").concat(File.separator).concat(FILE_NAME);
    }

    private String convertObjToJsonString(T object) throws JsonProcessingException {
        String jsonInString  = objectMapper.writeValueAsString(object);
        return  jsonInString;

    }

    private Path createFile(String tmpdir) {
        Path path = Paths.get(tmpdir);
        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException x) {
            System.err.format("FILE named %s" +
                    " already exists%n", tmpdir);
        } catch (IOException x) {
            System.err.format("CREATE FILE error: %s%n", x);
        }
        return path;
    }
}
