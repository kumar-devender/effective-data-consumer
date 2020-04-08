package com.javafreakers.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class Tracker {
    private Set<String> allRecords;
    private Set<String> orphanRecords;
    private String currentResource;
    private boolean resourceADone;
    private boolean resourceBDone;
    /**
     * update this to true when we have processed all the records
     */
    private boolean conti;
}
