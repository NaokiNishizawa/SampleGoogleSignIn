package com.example.samplegooglesignin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.samplegooglesignin.ui.theme.SampleGoogleSignInTheme
import kotlinx.coroutines.NonDisposableHandle.parent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleGoogleSignInTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }

    @Composable
    private fun Greeting( modifier: Modifier = Modifier) {
        Column (
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { /* Do something */ },
            ) {
                Text("Google Sign In Pattern 1")
            }

            Button(
                onClick = { /* Do something */ },
            ) {
                Text("Google Sign In Pattern 2")
            }
            Button(
                onClick = { /* Do something */ },
            ) {
                Text("Google Sign Out")
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun GreetingPreview() {
        SampleGoogleSignInTheme {
            Greeting()
        }
    }
}