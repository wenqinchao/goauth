package com.example.auth.base


import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat

import com.example.auth.R



open class BaseActivity : AppCompatActivity() {
//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        hideSystemUI()
//    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
        }
    }

    // 沉浸式状态栏，将最顶部的view传入即可
    protected fun immersive(view: View) {
        // 获取状态栏高度
        var height = 0;
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) height = resources.getDimensionPixelSize(resourceId)
        val newHeight = view.layoutParams.height + height
        val width = view.layoutParams.width
        view.layoutParams = LinearLayout.LayoutParams(width, newHeight)
        view.setPadding(0, height, 0, 0)
    }

    protected fun toActivity(
        context: Context?,
        clazz: Class<out AppCompatActivity?>?,
        bundle: Bundle? = null
    ) {
        val intent = Intent(context, clazz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    protected fun setToolBar(title: Int) {
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(true)
        val titleView = findViewById<TextView>(R.id.title)
        titleView.text = getString(title)
    }

    protected fun setToolBar(title: String) {
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(true)
        val titleView = findViewById<TextView>(R.id.title)
        titleView.text = title
    }


}