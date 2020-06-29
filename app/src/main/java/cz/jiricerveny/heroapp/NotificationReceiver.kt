package cz.jiricerveny.heroapp

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import cz.jiricerveny.heroapp.spacex.launches.LaunchesJobService


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        val app = context?.applicationContext as HeroApp
        val message = intent?.getStringExtra("toastMessage") ?: "nothing"
        if (message != "nothing") Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        if (intent?.getBooleanExtra("getLaunches", false) == true) {
            app.startNotificationService()
            loadAll(app)
            Log.d("L NotificationReceiver", "app visible: ${app.isActivityVisible()}")
            if (!app.isActivityVisible()) {
                val activityIntent = Intent(context, MainActivity::class.java)
                    .putExtra("PAGE", 6)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(activityIntent)
            }
        }
    }

    private fun loadAll(context: Context?) {
        val componentName =
            ComponentName(context!!.applicationContext, LaunchesJobService::class.java)
        val bundle = PersistableBundle()
        bundle.putBoolean("ALL", true)
        val builder = JobInfo.Builder(0, componentName)
            .setExtras(bundle)
        val jobScheduler = context.getSystemService(JobScheduler::class.java)
        jobScheduler?.schedule(builder.build())
    }
}