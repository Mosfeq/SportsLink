package com.example.sportsapp.data.remote.dao

import com.example.sportsapp.domain.model.SportEvent
import com.example.sportsapp.util.UiState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseDao {

    var sportEventItem: SportEvent? = null
    var joinedSportEventItem: SportEvent? = null

    private val dbSportEvents = FirebaseDatabase.getInstance("https://sports-application-1452c-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("Sports Events")

    private val dbJoinedSportEvents = FirebaseDatabase.getInstance("https://sports-application-1452c-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("Joined Sports Events")

    fun getJoinedEventsList(result: (UiState<List<SportEvent>>) -> Unit){
        dbJoinedSportEvents.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val joinedSportsEventsList = arrayListOf<SportEvent>()
                for (document in snapshot.children){
                    joinedSportEventItem = document.getValue(SportEvent::class.java)
                    joinedSportEventItem?.let { joinedSportsEventsList.add(it) }
                }
                result.invoke(
                    UiState.Success(joinedSportsEventsList)
                )
            }

            override fun onCancelled(error: DatabaseError) {
                result.invoke(
                    UiState.Error("Cannot Retrieve Events")
                )
            }
        })
    }
    fun getSportsEventsList(result: (UiState<List<SportEvent>>) -> Unit){
        dbSportEvents.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val sportsEventsList = arrayListOf<SportEvent>()
                for (document in snapshot.children){
                    sportEventItem = document.getValue(SportEvent::class.java)
                    sportEventItem?.let { sportsEventsList.add(it) }
                }
                result.invoke(
                    UiState.Success(sportsEventsList)
                )
            }

            override fun onCancelled(error: DatabaseError) {
                result.invoke(
                    UiState.Error("Cannot Retrieve Events")
                )
            }
        })
    }

    fun addSportEvent(sportEvent: SportEvent, response: (UiState<String>) -> Unit){
        sportEvent.title?.let {
            dbSportEvents.child(it).setValue(sportEvent)
                .addOnSuccessListener {
                    response.invoke(
                        UiState.Success("Event Added")
                    )
                }
                .addOnFailureListener { error ->
                    error.localizedMessage?.let { errorMessage ->
                        response.invoke(
                            UiState.Error(errorMessage)
                        )
                    }
                }
        }
    }

    fun joinSportEvent(sportEvent: SportEvent, response: (UiState<String>) -> Unit){
        sportEvent.title?.let {
            dbJoinedSportEvents.child(it).setValue(sportEvent)
                .addOnSuccessListener {
                    response.invoke(
                        UiState.Success("Event Joined")
                    )
                }
                .addOnFailureListener { error ->
                    error.localizedMessage?.let { errorMessage ->
                        response.invoke(
                            UiState.Error(errorMessage)
                        )
                    }
                }
        }
    }

    fun removeSportEvent(sportEventTitle: String){
        dbSportEvents.child(sportEventTitle).removeValue()
    }
}