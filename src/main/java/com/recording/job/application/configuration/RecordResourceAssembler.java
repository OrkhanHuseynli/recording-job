package com.recording.job.application.configuration;


import com.recording.job.application.controller.ServiceController;
import com.recording.job.application.data.domain.Record;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

//@Component
//public class RecordResourceAssembler implements ResourceAssembler<Record, Resource<Record>> {
//    @Override
//    public Resource<Record> toResource(Record record) {
////        return new Resource<>(record, linkTo(methodOn(ServiceController.class).createRecord(record) ) )
//    }
//}
