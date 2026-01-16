package com.example.sportsapp.presentation.screens.auth.sign_in

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.sportsapp.R
import com.example.sportsapp.presentation.navigation.Screens
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect){
                is SignInEffect.NavigateToSportListScreen -> {
                    navController.navigate(Screens.SportsListScreen.createRoute()){
                        // Clearing the backstack so users can't go back to the sign in screen
                        popUpTo(Screens.SignInScreen.route){
                            inclusive = true
                        }
                    }
                }
                is SignInEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is SignInEffect.NavigateToRegisterScreen -> {
                    navController.navigate(Screens.RegisterScreen.createRoute())
                }
            }
        }

    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                painter = painterResource(R.drawable.location),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp)
            )
            OutlinedTextField(
                value = state.email,
                onValueChange = {viewModel.handleIntent(SignInIntent.UpdateEmail(it))},
                label = { Text("Enter Email:") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp)
            )
            OutlinedTextField(
                value = state.password,
                onValueChange = {viewModel.handleIntent(SignInIntent.UpdatePassword(it))},
                label = { Text("Enter Password:") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp)
            )
            Button(
                modifier = Modifier
                    .padding(2.dp),
                onClick = {
                    viewModel.handleIntent(SignInIntent.SignInClicked(state.email, state.password))
                    viewModel.handleIntent(SignInIntent.ClearEmail)
                    viewModel.handleIntent(SignInIntent.ClearPassword)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sign In",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(2.dp)
                    )
                    Icon(
                        Icons.Filled.Search,
                        "",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Text(
                modifier = Modifier
                    .padding(5.dp)
                    .clickable{ viewModel.handleIntent(SignInIntent.RegisterBtnClicked) },
                text = "Haven't registered?",
                fontSize = 12.sp
            )
        }

    }

}

