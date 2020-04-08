package com.javafreakers.service.handler;

import com.javafreakers.dtos.Tracker;

public interface ResourceHandler {
    boolean canHandle(Tracker tracker);

    void handle(Tracker tracker);
}
