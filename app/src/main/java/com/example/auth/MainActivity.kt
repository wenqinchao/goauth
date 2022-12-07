package com.example.auth

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.auth.adaptor.SecretInfoAdaptor
import com.example.auth.base.BaseActivity
import com.example.auth.db.AppDatabase
import com.example.auth.entity.SecretInfo
import com.example.auth.ui.AddSecretActivity
import com.example.auth.ui.ExportActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    private lateinit var keys:RecyclerView
    private lateinit var adaptor:SecretInfoAdaptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        onClick()
        searchSecrets()
        initDao()
        initData()
    }

    private fun onClick() {
        val m = findViewById<ImageView>(R.id.open_menu)
        m.setOnClickListener {
            val draw = findViewById<DrawerLayout>(R.id.drawer)
            draw.openDrawer(Gravity.LEFT)
        }
        val addBtn = findViewById<FloatingActionButton>(R.id.add)
        addBtn.setOnClickListener {
            toActivity(this, AddSecretActivity::class.java)
        }

        val ex = findViewById<TextView>(R.id.export)
        ex.setOnClickListener {
            toActivity(this, ExportActivity::class.java)
        }
    }

    private fun initDao() {
        if (Data.dao == null) {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "secret"
            ).allowMainThreadQueries().build()
            Data.dao = db.secretInfoDao()
        }
    }

    private fun initData() {
        val refresh = intent.getBooleanExtra("refresh", false)
        if (Data.secrets.isEmpty() || refresh) {
            Data.secrets = Data.dao!!.getAll()
        }
        initItems()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun initItems() {
        keys = findViewById(R.id.keys)
        keys.layoutManager = LinearLayoutManager(this)
        adaptor = SecretInfoAdaptor(Data.secrets)
        adaptor.notifyDataSetChanged()
        keys.adapter = adaptor
    }

    private fun searchSecrets() {
        val si = findViewById<EditText>(R.id.search_input)
        si.addTextChangedListener {
            if (si.text.toString().isNotEmpty()) {
                Data.secrets = Data.dao!!.searchByNickname(si.text.toString())
            } else {
                Data.secrets = Data.dao!!.getAll()
            }
            initItems()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK && event?.repeatCount == 0){
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
}