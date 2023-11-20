package group10.com.guesstheera

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import group10.com.guesstheera.mainview.MainActivity


class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1440939901 // You can use any value here

    private lateinit var signInButton: Button
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mainIntent: Intent
    private var account: GoogleSignInAccount? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        signInButton = findViewById(R.id.signInButton)
        signInButton.setOnClickListener {
            signIn()
        }

        configureGoogleSignIn()
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            onActivityResult(RC_SIGN_IN, RESULT_OK, data)
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 0) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
//        try {
//            val account = completedTask.getResult(ApiException::class.java)
//            // Signed in successfully, you can use the account information.
//            updateUI(account)
//        } catch (e: ApiException) {
//            // Handle sign-in failure.
//            Log.w("GoogleSignIn", "signInResult:failed code=" + e.statusCode)
//            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show()
//        }
        account = completedTask.getResult(ApiException::class.java)
        if(account != null){
            updateUI(account)
        }
        else{
            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            // The user is signed in, you can get information from the 'account' object.
            val displayName = account.displayName
            val email = account.email
//            mainIntent = Intent(this, MainActivity::class.java)
//            startActivity(mainIntent)
            finish()
            // Update your UI or proceed with the signed-in user.
        } else {
            // The user is signed out.
            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
        }
    }
}
