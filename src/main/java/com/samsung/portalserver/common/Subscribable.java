package com.samsung.portalserver.common;

public interface Subscribable {

    void addSubscriber(Subscriber s);

    void notifytSubscribers();

    void notifytSubscribers(Object arg);

    int countSubscribers();
}
