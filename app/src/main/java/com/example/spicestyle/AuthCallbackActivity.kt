package com.example.spicestyle

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import okhttp3.FormBody
import retrofit2.HttpException

class AuthCallbackActivity : AppCompatActivity() {
    private val tokenStore by lazy { TokenStore(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri: Uri? = intent?.data
        if (uri != null && uri.scheme == "spicestyle" && uri.host == "callback") {
            val code = uri.getQueryParameter("code")
            val error = uri.getQueryParameter("error")

            if (error != null) {
                Log.e("Auth", "Auth error: $error")
                finishToMain()
                return
            }

            if (code != null) {
                val verifier = tokenStore.codeVerifier ?: ""
                lifecycleScope.launch {
                    try {
                        val tokens = SpotifyClient.auth()
                            .getTokens(AuthManager.tokenRequestBodyAuthCode(code, verifier))
                        tokenStore.accessToken = tokens.accessToken
                        tokens.refreshToken?.let { tokenStore.refreshToken = it }
                    } catch (e: HttpException) {
                        Log.e("Auth", "Token exchange failed: ${e.code()} ${e.message()}", e)
                    } catch (t: Throwable) {
                        Log.e("Auth", "Token exchange failure: ${t.message}", t)
                    } finally {
                        finishToMain()
                    }
                }
                return
            }
        }

        finishToMain()
    }

    private fun finishToMain() {
        startActivity(Intent(this, MainActivity::class.java).addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        ))
        finish()
    }
}
