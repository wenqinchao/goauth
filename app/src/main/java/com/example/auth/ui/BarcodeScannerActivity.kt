package com.example.auth.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.auth.Data
import com.example.auth.MainActivity
import com.example.auth.R
import com.example.auth.base.BaseActivity
import com.example.auth.entity.SecretInfo
import com.example.auth.interfaces.QRCodeFoundListener
import com.example.auth.util.QrCodeAnalyzer
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutionException

class BarcodeScannerActivity : BaseActivity() {
    private val PERMISSION_REQUEST_CAMERA = 0

    private var previewView: PreviewView? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null

    private val TAG = "CODE:"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner)
        previewView = findViewById(R.id.activity_main_previewView)
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        requestCamera()
    }

    private fun requestCamera() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CAMERA
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CAMERA
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera() {
        cameraProviderFuture!!.addListener({
            try {
                val cameraProvider = cameraProviderFuture!!.get()
                bindCameraPreview(cameraProvider)
            } catch (e: ExecutionException) {
                Toast.makeText(this, "Error starting camera " + e.message, Toast.LENGTH_SHORT)
                    .show()
            } catch (e: InterruptedException) {
                Toast.makeText(this, "Error starting camera " + e.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraPreview(cameraProvider: ProcessCameraProvider) {
        previewView?.implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        val preview = Preview.Builder()
            .build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(previewView?.surfaceProvider)
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(this),
            QrCodeAnalyzer(object : QRCodeFoundListener {
                override fun onQRCodeFound(_qrCode: String?) {
                    parseCode(_qrCode!!)
                    imageAnalysis.clearAnalyzer()
                }

                override fun qrCodeNotFound() {
//                    val qq = "otpauth://totp/CoinList:wenqinchao@gmail.com?secret=rsulrmwf5zftsh3ld22xqjeq&issuer=CoinList"
//                    val qq = "otpauth://totp/?algorithm=SHA1&digits=6&period=30&issuer=JianYi&secret=ASASASADDQ&nickname=ccvwqeaaaaaaae123as;otpauth://totp/?algorithm=SHA1&digits=6&period=30&issuer=JianYi&secret=QQQQQQQ&nickname=t2"
//                    parseCode(qq)
//                    imageAnalysis.clearAnalyzer()
                }
            })
        )
        cameraProvider.bindToLifecycle(
            (this as LifecycleOwner),
            cameraSelector,
            imageAnalysis,
            preview
        )
    }

    private fun parseCode(code: String) {
        val codes = code.split(";")
        code.contains("&nickname=")
        Log.d(TAG, "codes: $codes")
        val regex = Regex("(.*/)([a-zA-Z0-9\\.]+)[^\\w]+(.*)\\?secret=(\\w+)")
        val regex2 = Regex("(.*)secret=(\\w+)&nickname=(\\w+)")
        for (c in codes) {
            if (code.contains("&nickname=")) {
                val matchEntire = regex2.find(c)
                Log.d(TAG, "parseCode: ${matchEntire!!.groupValues}")
                val secretInfo = SecretInfo(
                    secret = matchEntire.groupValues[2],
                    nickname = matchEntire.groupValues[3],
                    id = null
                )
                Data.dao!!.insertAll(secretInfo)
            } else {
                val matchEntire = regex.find(c)
                val secretInfo = SecretInfo(
                    secret = matchEntire!!.groupValues[4],
                    nickname = matchEntire.groupValues[2] + "(" + matchEntire.groupValues[3] + ")",
                    id = null
                )
                Data.dao!!.insertAll(secretInfo)
            }
        }
        toActivity(this, MainActivity::class.java)
    }
}