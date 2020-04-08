package com.javafreakers.service.handler;

import com.javafreakers.client.ConsumerService;
import com.javafreakers.dtos.SinkDTO;
import com.javafreakers.dtos.Tracker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.javafreakers.constant.Constants.ORPHANED;
import static com.javafreakers.util.ResourceBuilder.buildSinkDTO;

@Service
@RequiredArgsConstructor
public class ResourceASinkHandler implements ResourceHandler {
    private final ConsumerService consumerService;

    @Override
    public boolean canHandle(Tracker tracker) {
        return tracker.isResourceBDone() == true && tracker.isResourceADone() == true;
    }

    @Override
    public void handle(Tracker tracker) {
        tracker.getAllRecords()
                .forEach(id -> {
                    SinkDTO sinkDTO = buildSinkDTO(id, ORPHANED);
                    consumerService.post(sinkDTO);
                });
        tracker.setConti(false);
    }
}
