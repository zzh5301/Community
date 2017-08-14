package com.example.wenda.async;

import java.util.List;

/**
 * Created by lenovo on 2017/5/28.
 */
public interface EventHandler {

    void doHandle(EventModel eventModel);

    List<EventType> getSupportEventTypes();
}
