package com.javafreakers.service.handler;

import com.javafreakers.client.ConsumerService;
import com.javafreakers.dtos.SinkDTO;
import com.javafreakers.dtos.Tracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;

import static com.javafreakers.constant.Constants.*;
import static com.javafreakers.util.ResourceBuilder.buildSinkDTO;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceBSourceHandler implements ResourceHandler {
    private static final SAXBuilder SAX_BUILDER = new SAXBuilder();
    private final ConsumerService consumerService;

    @Override
    public boolean canHandle(Tracker tracker) {
        return RESOURCE_B.equals(tracker.getCurrentResource()) && tracker.isResourceBDone() == false;
    }

    public void handle(Tracker tracker) {
        /**
         * Fetch records until we get 406
         */

        while (tracker.isResourceBDone() == false) {
            ResponseEntity<String> responseEntity = consumerService.consume(RESOURCE_B, String.class);
            String xml = responseEntity.getBody();
            try {
                Document document = SAX_BUILDER.build(new StringReader(xml));
                String msg = ofNullable(document)
                        .map(Document::getRootElement)
                        .map(element -> element.getChild("id"))
                        .map(element -> element.getAttribute("value"))
                        .map(Attribute::getValue)
                        .orElse(DONE);
                if (DONE.equals(msg)) {
                    tracker.setResourceBDone(true);
                } else {
                    processRecord(tracker, msg);
                }

            } catch (JDOMException e) {
                log.warn("Error in parsing response for resource [{}] xml [{}]", tracker.getCurrentResource(), xml);
            } catch (IOException e) {
                log.warn("Error in parsing response for resource [{}] xml [{}]", tracker.getCurrentResource(), xml);
            }
        }
    }

    private void processRecord(Tracker tracker, String msg) {
        if (tracker.getAllRecords().add(msg) == false) {
            SinkDTO sinkDTO = buildSinkDTO(msg, JOINED);
            consumerService.post(sinkDTO);
            tracker.getAllRecords().remove(msg);
            tracker.getOrphanRecords().remove(msg);
        }
    }
}
