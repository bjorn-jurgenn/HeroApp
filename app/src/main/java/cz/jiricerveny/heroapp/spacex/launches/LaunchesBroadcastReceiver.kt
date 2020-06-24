package cz.jiricerveny.heroapp.spacex.launches

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cz.jiricerveny.heroapp.HeroApp
import cz.jiricerveny.heroapp.R
import cz.jiricerveny.heroapp.spacex.launches.database.DBWrapper
import cz.jiricerveny.heroapp.spacex.launches.database.Launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

val TAG = "LaunchesBroadcastReceiver"

class LaunchesBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: triggered")
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val app = context.applicationContext as HeroApp
        val wifi = cm.getNetworkCapabilities(cm.activeNetwork)
            ?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
        if (wifi) {
            Toast.makeText(context, "Wi-fi connected, downloading data", Toast.LENGTH_SHORT).show()

            val componentName =
                ComponentName(context.applicationContext, LaunchesJobService::class.java)
            val builder = JobInfo.Builder(0, componentName)
            val jobScheduler = context.getSystemService(JobScheduler::class.java)
            jobScheduler?.schedule(builder.build())
        } else {
            app.sendNotification(wifi, "Using old data")
        }
    }

}

