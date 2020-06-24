package cz.jiricerveny.heroapp.spacex.launches

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import cz.jiricerveny.heroapp.HeroApp
import cz.jiricerveny.heroapp.spacex.launches.database.DBWrapper
import cz.jiricerveny.heroapp.spacex.launches.database.Launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class LaunchesJobService : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        loadNew(params)
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }

    private fun loadNew(params: JobParameters?) {
        val runnable = Runnable {
            val app = applicationContext as HeroApp
            val mainHandler = Handler(Looper.getMainLooper())
            val db = app.db
            DBWrapper(db.launchDatabaseDao, mainHandler).clear()
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
                        app.sendNotification(true, message)
                    }
                }

                override fun onFailure(call: Call<List<Launch>>, t: Throwable) {
                    Toast.makeText(applicationContext, "can't load", Toast.LENGTH_LONG).show()
                }
            })
            jobFinished(params, false)
        }
        Thread(runnable).start()
    }

}
