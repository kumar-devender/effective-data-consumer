package com.javafreakers.client;

import com.javafreakers.dtos.SinkDTO;
import com.javafreakers.dtos.SinkDTOResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ConsumerService {
    public static final String RESOURCE_SERVER = "http://localhost:7299";
    @Autowired
    RestTemplate restTemplate;

    public <T> ResponseEntity consume(String resourceName, Class<T> responseType) throws RestClientException {
        return restTemplate.getForEntity(RESOURCE_SERVER + "/source/" + resourceName, responseType);
    }

    public <T> ResponseEntity post(SinkDTO sinkDTO) throws RestClientException {
        return restTemplate.postForEntity(RESOURCE_SERVER + "/sink/a", sinkDTO, SinkDTOResponse.class);
    }
}
