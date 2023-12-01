package group10.com.guesstheera

import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import group10.com.guesstheera.mainview.MainActivity


class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1440939901 // You can use any value here

    private lateinit var signInGoogleButton: Button
    private lateinit var signInLocallyButton: Button
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var account: GoogleSignInAccount? = null
    private lateinit var accountLoggedIn: SharedPreferences
    private lateinit var intent: Intent
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)
        auth = Firebase.auth
//        configureGoogleSignIn()
//        signInGoogleButton = findViewById(R.id.signInGoogleButton)
//        signInGoogleButton.visibility = View.GONE
//        signInLocallyButton = findViewById(R.id.signInButton)
//        signInLocallyButton.visibility = View.GONE
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
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
//        signInLauncher.launch(signInIntent)
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

//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            handleSignInResult(task)
//        }
//        intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
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
