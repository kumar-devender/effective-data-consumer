package com.javafreakers.service;

import com.javafreakers.client.ConsumerService;
import com.javafreakers.dtos.SinkDTO;
import com.javafreakers.dtos.Tracker;
import com.javafreakers.service.handler.ResourceHandler;
import com.javafreakers.util.ResourceBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.javafreakers.constant.Constants.*;
import static com.javafreakers.util.ResourceBuilder.buildSinkDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceProcessor {
    private final List<ResourceHandler> resourceHandlers;
    private final ConsumerService consumerService;

    public void process() {
        Tracker tracker = ResourceBuilder.buildTracker();
        while (tracker.isConti()) {
            try {
                resourceHandlers.stream()
                        .filter(resourceHandler -> resourceHandler.canHandle(tracker))
                        .findFirst()
                        .ifPresent(resourceHandler -> resourceHandler.handle(tracker));
                updateCurrentResource(tracker);
            } catch (ResourceAccessException e) {
                log.warn("Error in accessing resource [{}]", tracker.getCurrentResource());
                updateTracker(tracker);
            } catch (RestClientException e) {
                log.warn("Error in accessing resource [{}]", tracker.getCurrentResource());
            }
        }
    }

    private void updateTracker(Tracker tracker) {
        if (tracker.getCurrentResource().equals(RESOURCE_A) || tracker.isResourceADone()) {
            tracker.setCurrentResource(RESOURCE_B);
        } else {
            tracker.setCurrentResource(RESOURCE_A);
            processRecords(tracker.getOrphanRecords(), tracker.getAllRecords(), ORPHANED);
            tracker.setOrphanRecords(new HashSet<>());
        }
    }

    private void processRecords(Set<String> unProcessedRecords, Set<String> allRecords, String kind) {
        unProcessedRecords.forEach(id -> {
            SinkDTO sinkDTO = buildSinkDTO(id, kind);
            consumerService.post(sinkDTO);
            allRecords.remove(id);
        });
    }

    private void updateCurrentResource(Tracker tracker) {
        if (tracker.isResourceADone()) {
            tracker.setCurrentResource(RESOURCE_B);
        }

        if (tracker.isResourceBDone()) {
            tracker.setCurrentResource(RESOURCE_A);
        }
    }
}

