package com.smalsus.redhorizonvbr.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface EventReminderAPI {
    @GET("/eventReminder/{eventId}")
    Call<Object> eventReminder(@Path("eventId") String eventId,@Header("token") String token);
}
