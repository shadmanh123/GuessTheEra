package group10.com.guesstheera

import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import group10.com.guesstheera.mainview.MainActivity

var personId: String? = "null"
class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1440939901 // You can use any value here
    private val REQ_ONE_TAP = 2

    private lateinit var signInGoogleButton: Button
    private lateinit var signInLocallyButton: Button
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var account: GoogleSignInAccount? = null
    private lateinit var accountLoggedIn: SharedPreferences
    private lateinit var intent: Intent
    private lateinit var auth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)
        auth = Firebase.auth

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            .setAutoSelectEnabled(true)
            .build()


//        configureGoogleSignIn()
    }

    override fun onStart() {
        super.onStart()
//        var account = GoogleSignIn.getLastSignedInAccount(this)
//        updateUI(account)
        signInGoogleButton.setOnClickListener {
            signIn()
        }
        signInLocallyButton.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            intent.putExtra("isAuth", false)
            startActivity(intent)
        }
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }
    private fun signIn() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0)
                } catch (e: IntentSender.SendIntentException) {
                    Log.d("send intent except", "Could not start one-tap UI")
                }
            }
            .addOnFailureListener(this) { e ->
                Toast.makeText(this, "Sign in failed", Toast.LENGTH_LONG).show()
            }
    }

//    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == RESULT_OK) {
//            Log.d("Login Activity", "Result code is OK")
//            val data: Intent? = result.data
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            handleSignInResult(task)
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
//        try {
//            account = completedTask.getResult(ApiException::class.java)
//            updateUI(account)
//        } catch (t: ApiException){
//            Log.d("Login Activity", "signInResult: failed code + ${t.statusCode}")
////            updateUI(null)
//            finish()
//        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
//        Log.d("Login Activity", "update UI")
//        if (account != null) {
//            Log.d("Login Activity", "account not null")
//
//            // The user is signed in, you can get information from the 'account' object.
//            val displayName = account.displayName
//            val email = account.email
//            finish()
//            // Update your UI or proceed with the signed-in user.
//        } else {
////            Log.d("Login Activity", "account is null")
//            signInGoogleButton.visibility = View.VISIBLE
//            signInLocallyButton.visibility = View.VISIBLE
//        }
    }
}
