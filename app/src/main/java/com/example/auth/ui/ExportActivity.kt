package com.example.auth.ui

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.example.auth.Data
import com.example.auth.R
import com.example.auth.base.BaseActivity
import com.example.auth.util.QrUtil
import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator

class ExportActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export)
        setToolBar(R.string.export)
        initQr()
    }

    private fun initQr() {
        val qr = findViewById<ImageView>(R.id.qr)
        var infoList: MutableList<String> = mutableListOf()
        for (secret in Data.secrets) {
            var singleString = GoogleAuthenticator(secret.secret.toByteArray())
                .otpAuthUriBuilder()
                .issuer("JianYi")
                .buildToString()
            singleString += "&nickname=${secret.nickname}"
            infoList.add(singleString)
        }
        if (infoList.size > 0) {
            val info = infoList.joinToString(";")
            val bm = QrUtil.createQRCode(info, 300, 300)
            qr.setImageBitmap(bm)
        }

    }

}