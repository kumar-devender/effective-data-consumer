package com.javafreakers.util;

import com.javafreakers.dtos.SinkDTO;
import com.javafreakers.dtos.Tracker;
import lombok.experimental.UtilityClass;

import java.util.HashSet;

import static com.javafreakers.constant.Constants.RESOURCE_A;

@UtilityClass
public class ResourceBuilder {

    public static SinkDTO buildSinkDTO(String id, String kind) {
        return SinkDTO.builder()
                .kind(kind)
                .id(id)
                .build();
    }

    public static Tracker buildTracker() {
        return Tracker.builder()
                .currentResource(RESOURCE_A)
                .resourceADone(false)
                .resourceBDone(false)
                .conti(true)
                .allRecords(new HashSet<>())
                .orphanRecords(new HashSet<>())
                .build();
    }
}
