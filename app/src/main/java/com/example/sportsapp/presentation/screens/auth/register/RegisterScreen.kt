package com.example.sportsapp.presentation.screens.auth.register

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sportsapp.R
import com.example.sportsapp.presentation.navigation.Screens
import com.example.sportsapp.ui.theme.SportsAppTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect){
                is RegisterEffect.NavigateToSportListScreen -> {
                    navController.navigate(Screens.SportsListScreen.createRoute()){
                        // Clearing the backstack so users can't go back to the sign in screen
                        popUpTo(Screens.RegisterScreen.route){
                            inclusive = true
                        }
                    }
                }
                is RegisterEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is RegisterEffect.NavigateToSignInScreen -> {
                    navController.navigate(Screens.SignInScreen.createRoute())
                }
            }
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                painter = painterResource(R.drawable.location),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(80.dp)
                    .padding(10.dp)
            )
            Text(
                modifier = Modifier.padding(10.dp),
                text = "Welcome Back!",
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.primary
            )
            OutlinedTextField(
                value = state.name,
                onValueChange = {viewModel.handleIntent(RegisterIntent.UpdateName(it))},
                label = { Text("Enter First Name:") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp),)
            OutlinedTextField(
                value = state.email,
                onValueChange = {viewModel.handleIntent(RegisterIntent.UpdateEmail(it))},
                label = { Text("Enter Email:") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp)
            )
            OutlinedTextField(
                value = state.password,
                onValueChange = {viewModel.handleIntent(RegisterIntent.UpdatePassword(it))},
                label = { Text("Enter Password:") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp)
            )
            Button(
                modifier = Modifier
                    .padding(2.dp),
                onClick = {
                    viewModel.handleIntent(RegisterIntent.RegisterClicked(state.name, state.email, state.password))
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Register",
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
                    .clickable{ viewModel.handleIntent(RegisterIntent.SignInBtnClicked) },
                text = "Already have an account?",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }

    }
}

