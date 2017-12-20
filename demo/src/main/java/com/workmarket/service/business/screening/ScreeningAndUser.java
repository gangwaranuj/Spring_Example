package com.workmarket.service.business.screening;

import com.workmarket.domains.model.User;
import com.workmarket.screening.model.Screening;

public class ScreeningAndUser {
    private final Screening screening;
    private final User user;

    public ScreeningAndUser(Screening screening, User user) {
        this.screening = screening;
        this.user = user;
    }

    public Screening getScreening() {
        return screening;
    }

    public User getUser() {
        return user;
    }
}
