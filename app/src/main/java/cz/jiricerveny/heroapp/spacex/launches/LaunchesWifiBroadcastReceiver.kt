package cz.jiricerveny.heroapp.spacex.launches
/*

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.widget.Toast
import cz.jiricerveny.heroapp.HeroApp
import cz.jiricerveny.heroapp.spacex.launches.database.Launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class LaunchesWifiBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.getStringExtra("STATE")){
            "CONNECTED" -> {
                doSomething(context)
            }
            else -> Toast.makeText(context,"broadcast triggered", Toast.LENGTH_LONG).show()
        }
    }
}


fun doSomething(context: Context?) {
    val mainHandler = Handler()
    val app = context?.applicationContext as HeroApp
    val db = app.db
    val runnable = Runnable {
        db.launchDatabaseDao.clear()
    }
    Thread(runnable).start()
    val random = Random.nextInt(3)
    val service = when (random){
        0 -> app.serviceFail
        1 -> app.serviceSuccess
        2 -> app.service2015
        else -> app.service
    }
    val message = when (random){
        0 -> "Only failed"
        1 -> "Only successful"
        2 -> "Only from 2015"
        else -> "All"
    }
    service.clone().enqueue(object : Callback<List<Launch>> {
        override fun onResponse(
            call: Call<List<Launch>>,
            response: Response<List<Launch>>
        ) {
            val responseListOfLaunches = response.body() ?: listOf()
            val runnableAdd = Runnable {
                for (launchItem in responseListOfLaunches) {
                    db.launchDatabaseDao.insert(launchItem)
                }
                mainHandler.post {
                    Toast.makeText(context, "$message launches loaded", Toast.LENGTH_LONG)
                        .show()
                    app.newData.value = true
                }
            }
            Thread(runnableAdd).start()
        }

        override fun onFailure(call: Call<List<Launch>>, t: Throwable) {
            Toast.makeText(context, "can't load", Toast.LENGTH_LONG).show()
        }
    })
}*/
