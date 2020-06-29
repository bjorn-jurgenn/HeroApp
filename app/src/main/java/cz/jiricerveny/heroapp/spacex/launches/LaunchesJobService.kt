package cz.jiricerveny.heroapp.spacex.launches

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Handler
import android.os.Looper
import cz.jiricerveny.heroapp.HeroApp
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
            db.launchDatabaseDao.clear()
            val random = if (params?.extras?.getBoolean("ALL") != true) {
                Random.nextInt(3)
            } else 3

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
            val responseList = call.execute().body() ?: listOf()

            for (launchItem in responseList) {
                db.launchDatabaseDao.insert(launchItem)
            }

            mainHandler.post {
                app.sendNotification(true, message)
            }
            jobFinished(params, false)
        }

        Thread(runnable).start()
    }

}
