package cz.jiricerveny.heroapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import cz.jiricerveny.heroapp.spacex.launches.database.DBWrapper
import cz.jiricerveny.heroapp.spacex.launches.database.Launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val app = context?.applicationContext as HeroApp
        val message = intent?.getStringExtra("toastMessage") ?: "nothing"
        if (message != "nothing") Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        if (intent?.getBooleanExtra("getLaunches", false) == true) {
            app.startNotificationService()
            loadAll(app)
        }
    }

    private fun loadAll(context: Context?) {
        val runnable = Runnable {
            val app = context?.applicationContext as HeroApp
            val mainHandler = Handler(Looper.getMainLooper())
            val db = app.db
            DBWrapper(db.launchDatabaseDao, mainHandler).clear()

            val call = app.service.getLaunches(null, null)

            call.clone().enqueue(object : Callback<List<Launch>> {
                override fun onResponse(
                    call: Call<List<Launch>>,
                    response: Response<List<Launch>>
                ) {
                    val responseListOfLaunches = response.body() ?: listOf()
                    val dbWrapper = DBWrapper(db.launchDatabaseDao, Handler())
                    for (launchItem in responseListOfLaunches) {
                        dbWrapper.insert(launchItem)
                    }
                    mainHandler.post {
                        app.sendNotification(true, "All launches loaded")
                    }
                }

                override fun onFailure(call: Call<List<Launch>>, t: Throwable) {
                    Toast.makeText(app.applicationContext, "can't load", Toast.LENGTH_LONG).show()
                }
            })

        }
        Thread(runnable).start()
    }
}