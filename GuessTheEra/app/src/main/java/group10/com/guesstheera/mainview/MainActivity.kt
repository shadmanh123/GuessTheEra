package group10.com.guesstheera.mainview

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import group10.com.guesstheera.LoginActivity
import group10.com.guesstheera.R
import group10.com.guesstheera.backend.FirebaseApplication
import group10.com.guesstheera.backend.ImageDatabaseViewModel
import group10.com.guesstheera.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var signOutButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        FirebaseApplication.imageDatabaseViewModel.startDownloadProcess()
        /*//floating mail button listener
        binding.appBarMain.fab.setOnClickListener {
            view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_game_options,R.id.nav_game_options_multiplayer, R.id.nav_leaderboard), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        signOutButton = navView.getHeaderView(0).findViewById(R.id.signOutButton)
        signOutButton.setOnClickListener {
            auth.signOut()
            var intentToLogin = Intent(this,LoginActivity::class.java)
            startActivity(intentToLogin)
        }
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            if (destination.id == R.id.nav_leaderboard) {
//                intent = Intent(this, LoadingPage::class.java)
//                startActivity(intent)
//            }
//        }

    }

    //provides overflow menu in action bar; not needed but gtk
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*menuInflater.inflate(R.menu.main, menu)*/
        //menuInflater.inflate(R.menu.activity_main_drawer, menu)
        menuInflater.inflate(R.menu.main, menu)
        /*val group = menu.findItem(R.id.drawer_group)

        for (i in 0 until (group.subMenu?.size() ?: 2)) {
            val menuItem = group.subMenu?.getItem(i)
            menuItem?.let<MenuItem, View?> { findViewById(it.itemId) }
                ?.setBackgroundResource(R.color.atomic_tangerine)
        }*/
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}


