package com.alan.alancars

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.READ_CONTACTS
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alan.alancars.databinding.ActivityHomeMainBinding
import com.alan.alancars.framework.callDelayed
import com.alan.alancars.framework.getBooleanPreference
import com.alan.alancars.framework.isOnline
import com.alan.alancars.framework.startActivity
import com.alan.alancars.model.DailyReminder
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlin.random.Random

private const val DELAY = 3000L
const val DATA_IMPORTED = "com.alan.alancars.data_imported"

const val CHANNEL_ID = "channel01"
const val notificationId = 101

class HomeMainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        const val PERMISSION_LOCATION_REQUEST_CODE = 1
    }


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeMainBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setSupportActionBar(binding.appBarHomeMain.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_about, R.id.nav_profile, R.id.nav_maps
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        createNotificationChannel()
        setNavHeader(navView)
        callCarService()
        requestLocationPermission()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            SettingsDialog.Builder(this).build().show()
        } else {
            requestLocationPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(
            this,
            getString(R.string.permission_granted),
            Toast.LENGTH_SHORT
        ).show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun requestLocationPermission(){
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.rationale_permission),
            PERMISSION_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = getString(R.string.notification_name)
            val descriptionText = getString(R.string.notification_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun refreshData(){
        val navView: NavigationView = binding.navView
        setNavHeader(navView)
    }

    private fun setNavHeader(navView: NavigationView) {
        navView.getHeaderView(0)
            .findViewById<TextView>(
                R.id.tvLoggedUserEmail
            ).text = auth.currentUser?.email.toString()
        navView.getHeaderView(0)
            .findViewById<TextView>(R.id.tvLoggedUserDisplayName)
            .text = auth.currentUser?.displayName ?: getString(R.string.car_lover)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_settings -> {
                exitApp()
                return true
            }
            R.id.action_logout -> {
                logout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.logout)
            setIcon(R.drawable.ic_baseline_logout_24)
            setMessage(getString(R.string.logout_prompt))
            setCancelable(true)
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                run {
                    auth.signOut()
                    Intent(this@HomeMainActivity, LoginActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(it)
                        finish()
                    }
                }
            }
            setNegativeButton(getString(R.string.cancle), null)
            show()
        }
    }

    private fun callCarService() {

        if (getBooleanPreference(DATA_IMPORTED)) {
            Toast.makeText(this, getString(R.string.data_imported), Toast.LENGTH_SHORT)
                .show()
        } else {
            if (isOnline()) {
                CarService.enqueue(this)
            } else {
                Toast.makeText(this, getString(R.string.no_internet_text), Toast.LENGTH_SHORT)
                    .show()
                callDelayed(DELAY) { finish() }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setAlarm(DailyReminder(Random.nextInt(0, 100), getString(R.string.daily_not_title),
            getString(R.string.daily_not_content)
        ))
    }

    private fun setAlarm(dailyReminder: DailyReminder) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("daily_reminder", dailyReminder)
        val pendingIntent = PendingIntent.getBroadcast(this, dailyReminder._id, intent, PendingIntent.FLAG_IMMUTABLE)
        val mainActivityIntent = Intent(this, HomeMainActivity::class.java)
        val basicPendingIntent = PendingIntent.getActivity(this, dailyReminder._id, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE)
        val clockInfo = AlarmManager.AlarmClockInfo(5000, basicPendingIntent)
        alarmManager.setAlarmClock(clockInfo, pendingIntent)
    }

    private fun exitApp() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.exit)
            setIcon(R.drawable.exit)
            setMessage(getString(R.string.exit_promp))
            setCancelable(true)
            setPositiveButton(getString(R.string.ok)) { _, _ -> finish() }
            setNegativeButton(getString(R.string.cancle), null)
            show()
        }
    }
}