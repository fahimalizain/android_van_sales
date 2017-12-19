package com.casualmill.vansales.support;

/**
 * Created by faztp on 19-Dec-17.
 */

public class MainActivityEvent {
    public enum EventType { Refresh, START_LOADING, STOP_LOADING }

    public MainActivityEvent(EventType type) {
        this.type = type;
    }

    public EventType type;
}
