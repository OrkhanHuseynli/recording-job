package com.recording.job.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recording.job.application.controller.reponse.DataResponse;
import com.recording.job.application.data.domain.Record;
import com.recording.job.application.data.domain.RecordStatus;
import com.recording.job.application.data.services.StorageOperations;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ServiceControllerTest {
    String FILE_NAME = "records.txt";
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    StorageOperations<Record> storageOperations;


    @Test
    public void basicEndpointsTest() throws Exception {
        String insertedContent = "{\"recordedData\": \"IHsKCiJwb3NpdGlvbiI6ICJFbmdpbmVlciIKICAgImRlc2NyaXB0aW9uIjogIldyaXRlcyBhcHBzIGFuZCBmaXhlcyBwcm9ibGVtcyIKfQ==\"}";
        String mockData = "{\"recordId\":\"9e79cbb8-5ebd-4474-9357-cdd26eb744ce\",\"info\":{\"recordStatus\":\"NEW\",\"created\":\"Sun Mar 03 20:19:07 CET 2019\",\"updated\":null,\"deleted\":null,\"recordedData\":\"IHsKCiJwb3NpdGlvbiI6ICJFbmdpbmVlciIKICAgImRlc2NyaXB0aW9uIjogIldyaXRlcyBhcHBzIGFuZCBmaXhlcyBwcm9ibGVtcyIKfQ==\"}}";
        String target_uuid = "9e79cbb8-5ebd-4474-9357-cdd26eb744ce";
        try {
            storageOperations.write(mockData, FILE_NAME);
        } catch (IOException e) {
            Assert.fail();
        }
        /*
          Test for get
         */
        MvcResult data = getRecord(target_uuid);
        String contentAsString = data.getResponse().getContentAsString();
        DataResponse dataResponse = mapDataResponse(contentAsString);
        Record record = dataResponse.getRecord();
        System.out.println(record.getInfo().getRecordStatus().toString());
        Assert.assertEquals(record.getInfo().getRecordStatus(), RecordStatus.NEW);
        Assert.assertNull(record.getInfo().getUpdated());
        /*
        Test for update
         */
        this.mvc.perform(patch("/v1/process/record/".concat(target_uuid))
                .contentType(MediaType.APPLICATION_JSON).content(insertedContent)).andExpect(status().isOk()).andReturn();
        data = getRecord(target_uuid);
        contentAsString = data.getResponse().getContentAsString();
        dataResponse = mapDataResponse(contentAsString);
        Assert.assertTrue(dataResponse.getRecord().getInfo().getUpdated().size()>0);
        /*
            Test for delete
         */
        Assert.assertNull(dataResponse.getRecord().getInfo().getDeleted());
        this.mvc.perform(delete("/v1/process/record/".concat(target_uuid))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        data = getRecord(target_uuid);
        contentAsString = data.getResponse().getContentAsString();
        dataResponse = mapDataResponse(contentAsString);
        Assert.assertNotNull(dataResponse.getRecord().getInfo().getDeleted());

        cleanStorageFromRecord(target_uuid);
    }


    MvcResult getRecord(String target_uuid) throws Exception {
       return this.mvc.perform(get("/v1/process/record/".concat(target_uuid))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
    }


    DataResponse mapDataResponse(String contentAsString) throws IOException {
        return objectMapper.readValue(contentAsString.trim(), DataResponse.class);
    }

    void cleanStorageFromRecord(String uuid) throws IOException {
        String filePath = System.getProperty("java.io.tmpdir").concat(File.separator).concat(FILE_NAME);
        try {
            Stream<String> lines = Files.lines(Paths.get(filePath));
            String filteredCollection = lines.filter(line->!line.contains(uuid)).collect(Collectors.joining("\n"));
            Files.write(Paths.get(filePath), filteredCollection.getBytes());
        } catch (NoSuchFileException e) {
            System.out.println("NO SUCH FILE : " + filePath);
        }
    }
}
