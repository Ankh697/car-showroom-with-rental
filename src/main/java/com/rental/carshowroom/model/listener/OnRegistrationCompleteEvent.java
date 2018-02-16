package com.rental.carshowroom.model.listener;

import com.rental.carshowroom.model.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private final String appUrl;
    private final User user;

    public OnRegistrationCompleteEvent(final String appUrl,final User user) {
        super(user);
        this.appUrl = appUrl;
        this.user = user;
    }
}
