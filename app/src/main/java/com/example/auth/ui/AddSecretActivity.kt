package com.example.auth.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.auth.Data
import com.example.auth.MainActivity
import com.example.auth.R
import com.example.auth.base.BaseActivity
import com.example.auth.entity.SecretInfo
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AddSecretActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_secret)
        setToolBar(R.string.add_account)
        onClick()
    }


    private fun onClick() {
        val btnAdd = findViewById<Button>(R.id.btn_add)
        btnAdd.setOnClickListener {
            val accountEdit = findViewById<EditText>(R.id.account)
            if (accountEdit.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.please_input_account, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val secretEdit = findViewById<EditText>(R.id.secret)
            if (secretEdit.text.toString().isEmpty()) {
                Toast.makeText(this, R.string.please_input_secret, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val secretInfo =
                SecretInfo(null, secretEdit.text.toString(), accountEdit.text.toString())

            Data.dao!!.insertAll(secretInfo)
            val bundle = Bundle()
            bundle.putBoolean("refresh", true)
            toActivity(baseContext, MainActivity::class.java, bundle)
        }

        val btnSc = findViewById<Button>(R.id.btn_scan)
        btnSc.setOnClickListener {
            toActivity(this,BarcodeScannerActivity::class.java)
        }
    }


}