package cz.jiricerveny.heroapp.spacex.launches

import android.content.BroadcastReceiver
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
        val wifi = cm.getNetworkCapabilities(cm.activeNetwork)
            ?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
        if (wifi) {
            loadNew(context, wifi)
            Toast.makeText(context, "Wi-fi connected, downloading data", Toast.LENGTH_SHORT).show()
        } else {
            sendNotification(context, wifi, "Using old data")
        }
    }

    private fun loadNew(context: Context?, wifi: Boolean) {
        val app = context?.applicationContext as HeroApp
        val db = app.db
        DBWrapper(db.launchDatabaseDao, Handler()).clear()
        val random = Random.nextInt(3)

        val call = when (random) {
            0 -> app.service.getLaunches(null, false)
            1 -> app.service.getLaunches(null, true)
            2 -> app.service.getLaunches(2015, null)
            else -> app.service.getLaunches(null, null)
        }
        val message = when (random) {
            0 -> "Only failed launches loaded."
            1 -> "Only successful launches loaded."
            2 -> "Only from 2015 launches loaded."
            else -> "All launches loaded."
        }
        // TODO it is not recommended to do background task in broadcast receiver -> better solution would be to schedule a background job
        // see the video at https://developer.android.com/guide/components/broadcasts#effects-on-process-state
        call.clone().enqueue(object : Callback<List<Launch>> {
            override fun onResponse(
                call: Call<List<Launch>>,
                response: Response<List<Launch>>
            ) {
                val responseListOfLaunches = response.body() ?: listOf()
                val dbWrapper = DBWrapper(db.launchDatabaseDao, Handler())
                for (launchItem in responseListOfLaunches) {
                    dbWrapper.insert(launchItem) {
                        sendNotification(context, wifi, message)
                    }
                }
            }

            override fun onFailure(call: Call<List<Launch>>, t: Throwable) {
                Toast.makeText(context, "can't load", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun sendNotification(context: Context, loaded: Boolean, msg: String) {
        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

        val notification = NotificationCompat.Builder(
            context,
            (context.applicationContext as HeroApp).CHANNEL_1_ID
        )
            .setContentText(msg)
            .setSmallIcon(R.drawable.ic_one)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
        if (loaded) {
            notification.setContentTitle("New data")

        } else {
            notification.setContentTitle("No Wi-Fi")
        }
        notificationManager.notify(1, notification.build())
    }
}

