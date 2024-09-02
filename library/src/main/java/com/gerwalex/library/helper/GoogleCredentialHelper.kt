package com.gerwalex.library.helper

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object GoogleCredentialHelper {

    val scope = CoroutineScope(Dispatchers.Main)
    fun logout(context: Context, success: suspend (success: Boolean) -> Unit) {
        val manager = CredentialManager.create(context)
        scope.launch {
            try {
                val s = ClearCredentialStateRequest()
                manager.clearCredentialState(s)
                success(true)
            } catch (e: ClearCredentialException) {
                e.printStackTrace()
                success(false)
            }
        }
    }

    fun silentSignIn(
        context: Context,
        clientId: String,
        signInResult: suspend (credential: GoogleIdTokenCredential?) -> Unit
    ) {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setAutoSelectEnabled(true)
            .setServerClientId(clientId)
            .build()
        logInWithGoogle(context, googleIdOption, signInResult)
    }

    fun signIn(
        context: Context,
        clientId: String,
        signInResult: suspend (credential: GoogleIdTokenCredential?) -> Unit
    ) {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(clientId)
            .build()
        logInWithGoogle(context, googleIdOption, signInResult)
    }


    private fun logInWithGoogle(
        context: Context,
        googleIdOption: CredentialOption,
        signInResult: suspend (GoogleIdTokenCredential?) -> Unit,
    ) {
        val manager = CredentialManager.create(context)
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch {
            var acc: GoogleIdTokenCredential? = null
            try {
                val result = manager.getCredential(
                    request = request,
                    context = context,
                )
                when (val credential = result.credential) {
                    is CustomCredential -> {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        acc = GoogleIdTokenCredential.createFrom(credential.data).apply {
                            Log.d("GoogleSignInHelper", "GoogleSignIn ok")
                            Log.d("GoogleSignInHelper", "familyName=${familyName}")
                            Log.d("GoogleSignInHelper", "displayName=${displayName}")
                            Log.d("GoogleSignInHelper", "givenName=${givenName}")
                            Log.d("GoogleSignInHelper", "phone=${phoneNumber}")
                            Log.d("GoogleSignInHelper", "id=${id}")
                            Log.d("GoogleSignInHelper", "token=${idToken}")
                            Log.d("GoogleSignInHelper", "photo=${profilePictureUri}")
                        }
                    }
                }
            } catch (e: GoogleIdTokenParsingException) {
                Log.d(
                    "GoogleCredentialHelper",
                    "Received an invalid google id token response",
                    e
                )
            } catch (e: GetCredentialException) {
                handleFailure(e)
            } finally {
                signInResult(acc)
            }
        }
    }


    private fun handleFailure(e: GetCredentialException) {
        e.printStackTrace()
    }

    fun handleSignIn(
        result: GetCredentialResponse,
        signInResult: (GoogleIdTokenCredential) -> Unit
    ) {
        // Handle the successfully returned credential.

        when (val credential = result.credential) {
            is PublicKeyCredential -> {
                // Share responseJson such as a GetCredentialResponse on your server to
                // validate and authenticate
                val responseJson = credential.authenticationResponseJson
                Log.d("GoogleCredentialHelper", "handleSignIn: $responseJson")
            }

            is PasswordCredential -> {
                // Send ID and password to your server to validate and authenticate.
                val username = credential.id
                val password = credential.password
            }

            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val acc = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        Log.d("GoogleSignInHelper", "GoogleSignIn ok")
                        Log.d("GoogleSignInHelper", "id=${acc.id}")
                        Log.d("GoogleSignInHelper", "name=${acc.displayName}")
                        Log.d("GoogleSignInHelper", "mail=${acc.familyName}")
                        Log.d("GoogleSignInHelper", "token=${acc.idToken}")
                        Log.d("GoogleSignInHelper", "photo=${acc.givenName}")

                        Log.d(
                            "GoogleCredentialHelper",
                            "googleIdTokenCredential=$acc "
                        )
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.d(
                            "GoogleCredentialHelper",
                            "Received an invalid google id token response",
                            e
                        )
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.d("GoogleCredentialHelper", "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.d("GoogleCredentialHelper", "Unexpected type of credential")
            }
        }
    }
}