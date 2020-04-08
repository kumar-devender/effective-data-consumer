package com.javafreakers.service.handler;

import com.javafreakers.client.ConsumerService;
import com.javafreakers.dtos.SinkDTO;
import com.javafreakers.dtos.SourceADTO;
import com.javafreakers.dtos.Tracker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.javafreakers.constant.Constants.*;
import static com.javafreakers.util.ResourceBuilder.buildSinkDTO;

@Service
@RequiredArgsConstructor
public class ResourceASourceHandler implements ResourceHandler {
    private final ConsumerService consumerService;

    @Override
    public boolean canHandle(Tracker tracker) {
        return RESOURCE_A.equals(tracker.getCurrentResource()) && tracker.isResourceADone() == false;
    }

    public void handle(Tracker tracker) {
        /**
         * Fetch records until we get 406
         */
        while (tracker.isResourceADone() == false) {
            ResponseEntity<SourceADTO> responseEntity = consumerService.consume(RESOURCE_A, SourceADTO.class);
            if (responseEntity.getBody() != null && responseEntity.getBody().getStatus().equals(DONE)) {
                tracker.setResourceADone(true);
            } else {
                String id = responseEntity.getBody().getId();
                if (tracker.getAllRecords().add(id) == false) {
                    SinkDTO sinkDTO = buildSinkDTO(id, JOINED);
                    consumerService.post(sinkDTO);
                    tracker.getAllRecords().remove(id);
                } else {
                    tracker.getOrphanRecords().add(id);
                }
            }
        }
    }
}
