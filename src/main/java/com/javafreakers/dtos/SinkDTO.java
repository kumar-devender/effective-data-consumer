package com.javafreakers.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SinkDTO {
    private String id;
    private String kind;
}
