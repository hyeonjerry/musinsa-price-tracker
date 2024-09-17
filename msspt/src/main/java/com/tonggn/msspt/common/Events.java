package com.tonggn.msspt.common;

import org.springframework.context.ApplicationEventPublisher;

public class Events {

  private static ApplicationEventPublisher publisher;

  static void setPublisher(final ApplicationEventPublisher publisher) {
    Events.publisher = publisher;
  }

  public static void publish(final Object event) {
    if (event != null) {
      publisher.publishEvent(event);
    }
  }
}
