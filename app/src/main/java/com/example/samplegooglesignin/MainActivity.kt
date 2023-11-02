package com.example.samplegooglesignin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.samplegooglesignin.ui.theme.SampleGoogleSignInTheme
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class MainActivity : ComponentActivity() {
    private val patter1Launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            pattern1SignInGoogleResult(result)
        }
    private val patter2launcher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            pattern2SignInGoogleResult(result)
        }

    private lateinit var idToken: String
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
        try {
            val inputStream = assets.open("clientId.txt")
            idToken = inputStream.bufferedReader().use { it.readText() }
            Toast.makeText(this, "idToken: $idToken", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error reading assets/clientId.txt", Toast.LENGTH_LONG).show()
        }
    }

    @Composable
    private fun Greeting(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { googleSignInPattern1() },
            ) {
                Text("Google Sign In Pattern 1")
            }

            Button(
                onClick = { googleSignInPattern2() },
            ) {
                Text("Google Sign In Pattern 2")
            }
            Button(
                onClick = { googleSignOut() },
            ) {
                Text("Google Sign Out")
            }
        }
    }

    private fun googleSignInPattern1() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(idToken)
            .requestEmail()
            .build()

        GoogleSignIn.getClient(this, googleSignInOptions).signInIntent.also {
            patter1Launcher.launch(it)
        }
    }

    private fun googleSignInPattern2() {
        val oneTapClient = Identity.getSignInClient(this)
        val signInRequest = try {
            BeginSignInRequest.Builder()
                .setPasswordRequestOptions(
                    BeginSignInRequest.PasswordRequestOptions.Builder()
                        .setSupported(true)
                        .build()
                )
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.Builder()
                        .setSupported(true)
                        .setServerClientId(idToken)
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .build()
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            null
        }
        signInRequest?.let {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener {
                    patter2launcher.launch(
                        IntentSenderRequest.Builder(it.pendingIntent.intentSender).build()
                    )
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } ?: run {
            Toast.makeText(this, "signInRequest is null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pattern1SignInGoogleResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            if (result.data != null) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            }
        } else {
            Toast.makeText(this, "result code is not RESULT_OK", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pattern2SignInGoogleResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            if (result.data != null) {
                val credential = try {
                    Identity.getSignInClient(this).getSignInCredentialFromIntent(result.data)
                } catch (e: Exception) {
                    // ログインキャンセル時等にExceptionが発生する
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    null
                }
                val idToken = credential?.googleIdToken
                if (idToken != null) {
                    Toast.makeText(this, "success", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "result code is not RESULT_OK", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                Toast.makeText(this, "success", Toast.LENGTH_LONG).show()
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun googleSignOut() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(idToken)
            .requestEmail()
            .build()

        val signInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        signInClient.signOut().addOnCompleteListener(this) {
            Toast.makeText(this, "success sign out", Toast.LENGTH_SHORT).show()
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