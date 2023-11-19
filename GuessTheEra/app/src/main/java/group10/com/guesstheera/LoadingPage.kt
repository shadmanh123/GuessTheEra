package group10.com.guesstheera

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoadingPage: AppCompatActivity()  {
    private lateinit var gameTitleTV: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_page)
        gameTitleTV = findViewById(R.id.gameTitleTV)
        val fadeInAnimation = ObjectAnimator.ofFloat(gameTitleTV, "alpha", 0f, 1f)
        fadeInAnimation.duration = 1000 // Set the duration of the animation in milliseconds
        fadeInAnimation.interpolator = AccelerateDecelerateInterpolator()

        val fadeOutAnimation = ObjectAnimator.ofFloat(gameTitleTV, "alpha", 1f, 0f)
        fadeOutAnimation.startDelay = 2000 // Wait for 2 seconds before starting fade-out
        fadeOutAnimation.duration = 1000
        fadeOutAnimation.interpolator = AccelerateDecelerateInterpolator()

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(fadeInAnimation, fadeOutAnimation)
        animatorSet.start()

    }
}