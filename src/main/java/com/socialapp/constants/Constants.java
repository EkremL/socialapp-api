package com.socialapp.constants;

import java.util.concurrent.TimeUnit;

//! Projedeki sabit (constants) değerler için bu classı oluşturdum
public final class Constants {
    //! JWT Token geçerlilik süresini 30 gün ayarladım.
    public static final long EXPIRATION_TIME = TimeUnit.DAYS.toMillis(30);
}
