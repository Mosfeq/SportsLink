package com.example.sportsapp.data.remote.dao

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.example.sportsapp.domain.model.SportEvent
import com.example.sportsapp.domain.model.User
import com.example.sportsapp.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseDao {
    private var sportEventItem: SportEvent? = null
    private var joinedSportEventItem: SportEvent? = null

    private val auth = FirebaseAuth.getInstance()

    private fun replaceEmailCharacters(email: String): String{
        return email.replace(".", ",")
    }

    private val formattedEmail: String
        get() = replaceEmailCharacters(auth.currentUser?.email ?: "")

    private val dbSportEvents = FirebaseDatabase.getInstance("https://sports-application-1452c-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("Sports Events")

    private val dbUsers = FirebaseDatabase.getInstance("https://sports-application-1452c-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("Users")

    fun register(name: String, email: String, password: String, result: (UiState<String>) -> Unit){
        val user = User(name = name, email = email, emptyList(), emptyList())
        auth.createUserWithEmailAndPassword(
            email, password
        ).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val formattedEmail = replaceEmailCharacters(email)
                dbUsers.child(formattedEmail).setValue(user)
                    .addOnSuccessListener {
                        result.invoke(
                            UiState.Success("Registration Successful")
                        )
                    }
                    .addOnFailureListener { error ->
                        error.localizedMessage?.let { errorMessage ->
                            result.invoke(
                                UiState.Error(errorMessage)
                            )
                        }
                    }
            } else {
                result.invoke(
                    UiState.Error(task.exception.toString())
                )
            }
        }

    }

    fun signIn(email: String, password: String, result: (UiState<String>) -> Unit){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                result.invoke(
                    UiState.Success("Sign In Successful")
                )
            } else {
                result.invoke(
                    UiState.Error("Incorrect Credentials")
                )
            }
        }
    }

    fun getCurrentUser(result: (UiState<User>) -> Unit){
        dbUsers.child(formattedEmail).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null){
                    result.invoke(UiState.Success(user))
                } else {
                    result.invoke(UiState.Error("User not found"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                result.invoke(UiState.Error(error.message))
            }
        })
    }

    fun getHostedEventsList(result: (UiState<List<SportEvent>>) -> Unit){
        dbUsers.child(formattedEmail).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                val hostedEventsList = user?.hostedEvents ?: emptyList()
                result.invoke(
                    UiState.Success(hostedEventsList)
                )
            }
            override fun onCancelled(error: DatabaseError) {
                result.invoke(
                    UiState.Error("Cannot Retrieve Events")
                )
            }
        })
    }

    fun getJoinedEventsList(result: (UiState<List<SportEvent>>) -> Unit){
        dbUsers.child(formattedEmail).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                val joinedSportsEventsList = user?.joinedEvents?: emptyList()
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
        dbUsers.child(formattedEmail).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)

                dbSportEvents.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val sportsEventsList = arrayListOf<SportEvent>()
                        for (document in snapshot.children){
                            sportEventItem = document.getValue(SportEvent::class.java)
                            if (sportEventItem?.host != user?.name){
                                sportEventItem?.let { sportsEventsList.add(it) }
                            }
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
            override fun onCancelled(error: DatabaseError) {
                result.invoke(
                    UiState.Error("Cannot Retrieve Events")
                )
            }
        })
    }

    fun addSportEvent(sportEvent: SportEvent, response: (UiState<String>) -> Unit){
        sportEvent.title.let {
            dbSportEvents.child(it).setValue(sportEvent)
                .addOnSuccessListener {
                    dbUsers.child(formattedEmail).addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(User::class.java)
                            if (user != null) {
                                val updatedHostedEvents = user.hostedEvents.toMutableList()
                                updatedHostedEvents.add(sportEvent)
                                val updatedUser = user.copy(hostedEvents = updatedHostedEvents)

                                dbUsers.child(formattedEmail).setValue(updatedUser)
                                    .addOnSuccessListener {
                                        response.invoke(UiState.Success("Event Added"))
                                    }
                                    .addOnFailureListener { error ->
                                        error.localizedMessage?.let { errorMessage ->
                                            response.invoke(UiState.Error(errorMessage))
                                        }
                                    }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            response.invoke(UiState.Error(error.message))
                        }
                    })
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
        dbUsers.child(formattedEmail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null){
                    if (user.joinedEvents.any {it.id == sportEvent.id}){
                        response.invoke(UiState.Error("Event Already Joined"))
                        return
                    }

                    val updatedJoinedEvents= user.joinedEvents.toMutableList()
                    updatedJoinedEvents.add(sportEvent)
                    val updatedUser = user.copy(joinedEvents = updatedJoinedEvents)

                    dbUsers.child(formattedEmail).setValue(updatedUser)
                        .addOnSuccessListener {
                            response.invoke(UiState.Success("Event Joined"))
                        }
                        .addOnFailureListener { error ->
                            error.localizedMessage?.let { errorMessage ->
                                response.invoke(UiState.Error("User Not Found"))
                            }
                        }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                response.invoke(UiState.Error(error.message))
            }
        })

    }

    fun removeSportEvent(sportEventTitle: String){
        dbSportEvents.child(sportEventTitle).removeValue()
    }
}