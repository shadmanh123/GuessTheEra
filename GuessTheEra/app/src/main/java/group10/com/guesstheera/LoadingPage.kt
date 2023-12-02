package group10.com.guesstheera

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import group10.com.guesstheera.mainview.MainActivity

class LoadingPage: AppCompatActivity()  {
    private lateinit var gameTitleTV: TextView
    private lateinit var intent: Intent
    private lateinit var fadeInAnimation: ObjectAnimator
    private lateinit var fadeOutAnimation: ObjectAnimator
    private lateinit var animatorSet: AnimatorSet
    private lateinit var auth: FirebaseAuth
    private var isSignedIn: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_page)
        gameTitleTV = findViewById(R.id.gameTitleTV)
        auth = Firebase.auth
        isSignedIn()
        createFadeInAnimation()
        createFadeOutAnimation()
        createAnimation()
    }

    private fun isSignedIn() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            isSignedIn = true
        }
    }
    private fun createAnimation() {
        animatorSet = AnimatorSet()
        animatorSet.playSequentially(fadeInAnimation, fadeOutAnimation)
        animatorSet.start()
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (!isSignedIn) {
                    intent = Intent(this@LoadingPage, LoginActivity::class.java)
                    startActivity(intent)
                }
                else {
                    intent = Intent(this@LoadingPage, MainActivity::class.java)
                    intent.putExtra("isAuth", true)
                    startActivity(intent)
                }
                // Finish the LoadingPage activity if you want
//                finish()
            }
        })
    }

    private fun createFadeOutAnimation() {
        fadeOutAnimation = ObjectAnimator.ofFloat(gameTitleTV, "alpha", 1f, 0f)
        fadeOutAnimation.startDelay = 2000 // Wait for 2 seconds before starting fade-out
        fadeOutAnimation.duration = 1000
        fadeOutAnimation.interpolator = AccelerateDecelerateInterpolator()
    }

    private fun createFadeInAnimation() {
        fadeInAnimation = ObjectAnimator.ofFloat(gameTitleTV, "alpha", 0f, 1f)
        fadeInAnimation.startDelay = 1000
        fadeInAnimation.duration = 1000 // Set the duration of the animation in milliseconds
        fadeInAnimation.interpolator = AccelerateDecelerateInterpolator()
    }
}