package com.example.sportsapp.domain.model

import com.google.firebase.database.Exclude
import java.sql.Time
import java.util.Date
import java.util.UUID

data class SportEvent (
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val sports: String = "",
    val location: String = "",
    val date: Long = 0L,
    val time: Long = 0L,
    val experience: String = "",
    val host: String = ""
){
    @get:Exclude
    val dateStamp: Date
        get() = Date(date)

    @get:Exclude
    val timeStamp: Time
        get() = Time(time)

    @get:Exclude
    val sportsName: Sports
        get() = try {
            Sports.valueOf(sports)
        } catch (e: IllegalArgumentException){
            Sports.Football
        }

    constructor(
        title: String,
        sports: Sports,
        location: String,
        date: Date,
        time: Time,
        experience: String,
        host: String
    ): this(
        id = UUID.randomUUID().toString(),
        title = title,
        sports = sports.displayName,
        location = location,
        date = date.time,
        time = time.time,
        experience = experience,
        host = host
    )
}