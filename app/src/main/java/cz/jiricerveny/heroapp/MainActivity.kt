package cz.jiricerveny.heroapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import cz.jiricerveny.heroapp.databinding.ActivityMainBinding
import cz.jiricerveny.heroapp.databinding.NavHeaderBinding
import cz.jiricerveny.heroapp.recycler.RecyclerFragment
import cz.jiricerveny.heroapp.spacex.landpads.LandPadsFragment
import cz.jiricerveny.heroapp.spacex.launches.LaunchesFragment
import cz.jiricerveny.heroapp.basic.AnimationDrawablesFragment
import cz.jiricerveny.heroapp.basic.CardsFragment
import cz.jiricerveny.heroapp.basic.ChangeUsernameDialogFragment
import cz.jiricerveny.heroapp.basic.DialogsFragment
import cz.jiricerveny.heroapp.spacex.launches.database.LaunchDatabase

const val EXTRA_MESSAGE = "cz.jiricerveny.aboutme.MESSAGE"
const val ACTIVITY_RESULT = "cz.jiricerveny.aboutme.RESULT"


/**
 * Main Activity contains behavior of Navigation Drawer and ChangeUsernameDialog
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ChangeUsernameDialogFragment.ChangeUsernameDialogListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var headerBinding: NavHeaderBinding
    private lateinit var db: LaunchDatabase
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        drawerLayout = binding.drawerLayout
        navView = binding.navView

        headerBinding = NavHeaderBinding.bind(navView.getHeaderView(0))

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(::onNavigationItemSelected)

        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("POSITION")
            headerBinding.username.text = savedInstanceState.getString("USERNAME")
        }
        navigateTo(position)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> position = 0
            R.id.menu_team_cards -> position = 1
            R.id.menu_dialogs -> position = 2
            R.id.animation -> position = 3
            R.id.recycler -> position = 4
            R.id.landpads -> position = 5
            R.id.launches -> position = 6

            R.id.edit_username -> {
                val dialog =
                    ChangeUsernameDialogFragment()
                dialog.show(supportFragmentManager, "Change Username")
                Toast.makeText(this, "Change username clicked", Toast.LENGTH_SHORT).show()
            }
        }
        navigateTo(position)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * Changes fragment to passed fragment
     * @param fr - Fragment to be displayed
     */
    private fun changeFragmentTo(fr: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_container, fr)
            commit()
        }
    }

    private fun navigateTo(fragmentPosition: Int) {
        when (fragmentPosition) {
            0 -> {
                val mainFragment = MainFragment()
                changeFragmentTo(mainFragment)
            }
            1 -> {
                val cardsFragment =
                    CardsFragment()
                changeFragmentTo(cardsFragment)
            }
            2 -> {
                val dialogsFragment =
                    DialogsFragment()
                changeFragmentTo(dialogsFragment)
            }
            3 -> {
                val animationDrawableFragment =
                    AnimationDrawablesFragment()
                changeFragmentTo(animationDrawableFragment)
            }
            4 -> {
                val recyclerFragment = RecyclerFragment()
                changeFragmentTo(recyclerFragment)
            }
            5 -> {
                val landpadsFragment =
                    LandPadsFragment()
                changeFragmentTo(landpadsFragment)
            }
            6 -> {
                val launchesFragment = LaunchesFragment()
                changeFragmentTo(launchesFragment)
            }
        }

    }

    /**
     * Pass data from dialog to username in NavigationDrawer
     */
    override fun onDialogPositiveClick(dialog: DialogFragment, input: String) {
        Toast.makeText(this, "Confirmed", Toast.LENGTH_SHORT).show()
        headerBinding.username.text = input
    }


    override fun onDialogNegativeClick(dialog: DialogFragment) {
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with(outState) {
            putInt("POSITION", position)
            putString("USERNAME", headerBinding.username.text.toString())
        }

    }

}

