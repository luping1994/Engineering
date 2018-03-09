package net.suntrans.building

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import net.suntrans.engineering.R
import net.suntrans.engineering.utils.StatusBarCompat

/**
 * Created by Looney on 2018/1/23.
 * Des:
 */
open class BasedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                        getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    protected fun fixStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val statusBarFix = findViewById<View>(R.id.statusBarFix)
            val statusBarHeight = StatusBarCompat.getStatusBarHeight(this.applicationContext)
            if (statusBarFix != null)
                statusBarFix.layoutParams.height = statusBarHeight

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                statusBarFix.setBackgroundColor(Color.parseColor("#ffffff"))
            }else{
                statusBarFix.setBackgroundColor(Color.parseColor("#888888"))
            }
        }
    }


    fun navigate(activity: Context, toActivity: Class<*>) {
        activity.startActivity(Intent(activity, toActivity))
    }



}
