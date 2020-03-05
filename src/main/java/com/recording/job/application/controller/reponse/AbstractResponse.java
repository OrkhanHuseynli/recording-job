package com.recording.job.application.controller.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractResponse {
    String status;
    String message;
}
