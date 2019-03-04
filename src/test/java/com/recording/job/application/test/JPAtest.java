package com.recording.job.application.test;


import com.recording.job.application.RecordingApp;
import com.recording.job.application.configuration.JsonConfiguration;
import com.recording.job.application.data.domain.Record;
import com.recording.job.application.data.domain.RecordInfo;
import com.recording.job.application.data.services.StorageOperations;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@SpringBootTest(classes= RecordingApp.class)
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {JsonConfiguration.class})
public class JPAtest {
    String FILE_NAME = "fileTest.txt";

    @Autowired
    StorageOperations<Record> storageOperations;


    @Test
    public void testStorageOperations(){
        String mockData = "{\"recordId\":\"9e79cbb8-5ebd-4474-9357-cdd26eb744ce\",\"info\":{\"recordStatus\":\"NEW\",\"created\":\"Sun Mar 03 20:19:07 CET 2019\",\"updated\":null,\"deleted\":null,\"recordedData\":\"IHsKCiJwb3NpdGlvbiI6ICJFbmdpbmVlciIKICAgImRlc2NyaXB0aW9uIjogIldyaXRlcyBhcHBzIGFuZCBmaXhlcyBwcm9ibGVtcyIKfQ==\"}}";
        String expectedData = "9e79cbb8-5ebd-4474-9357-cdd26eb744ce";
        //        Record recordData = new Record(new RecordInfo(new Date().toString(), "hello world".getBytes()));
        String notExpectedData = "DANES INVADING LONDON, 893 AC";
        try {
            storageOperations.write(mockData, FILE_NAME);
        } catch (IOException e) {
            Assert.fail();
        }
        HashMap<String, Record> hashMap = storageOperations.read(FILE_NAME);
        hashMap.forEach((key, record ) -> {
            Assert.assertTrue(record instanceof Record);
            Assert.assertEquals(expectedData,record.getRecordId());
            Assert.assertNotEquals(notExpectedData, record.getRecordId());
        } );
    }

}
