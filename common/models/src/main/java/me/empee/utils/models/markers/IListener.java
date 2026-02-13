package me.empee.utils.models.markers;

import com.hypixel.hytale.event.IBaseEvent;

public interface IListener<EventType extends IBaseEvent<?>> {

  Class<EventType> getEvent();

  void handle(EventType event);

}
