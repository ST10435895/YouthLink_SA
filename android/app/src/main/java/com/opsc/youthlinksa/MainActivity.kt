package com.opsc.youthlinksa

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

// Single-activity architecture: this Activity just hosts a NavHostFragment,
// and every screen in the app (Welcome, Login, Register, Home, Opportunity
// Search, Settings, etc.) is a Fragment navigated to via nav_graph.xml.
//
// A BottomNavigationView gives logged-in users quick access to the app's
// five main feature areas (Opportunities, Careers, Funding, Events, Saved).
// It's hidden on screens where it doesn't make sense to show it (auth
// screens, settings, and the opportunity detail screen).
class MainActivity : AppCompatActivity() {

    // Destinations where the bottom navigation bar should be visible.
    private val topLevelDestinations = setOf(
        R.id.homeFragment,
        R.id.careersFragment,
        R.id.fundingFragment,
        R.id.eventsFragment,
        R.id.savedFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Wired manually (rather than via NavigationUI.setupWithNavController)
        // because this is a single flat nav graph whose overall start
        // destination is the Welcome screen, not Home - the automatic
        // helper's default popUpTo target assumes tabs sit directly on top
        // of the graph's start destination, which doesn't apply here.
        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId != navController.currentDestination?.id) {
                navController.navigate(item.itemId)
            }
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNav.visibility =
                if (destination.id in topLevelDestinations) View.VISIBLE else View.GONE
            // Keep the highlighted tab in sync when navigation happens some
            // other way (e.g. the back button, or the settings gear icon
            // returning to Home).
            if (destination.id in topLevelDestinations && bottomNav.selectedItemId != destination.id) {
                bottomNav.menu.findItem(destination.id)?.isChecked = true
            }
        }

        // If the user already has a saved token, skip straight to Home
        // instead of showing the Welcome screen again.
        val app = application as YouthLinkApp
        if (app.tokenManager.isLoggedIn()) {
            navController.navigate(R.id.homeFragment)
        }
    }
}
