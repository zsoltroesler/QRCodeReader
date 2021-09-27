package com.roeslerz.qrcodereader

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.roeslerz.qrcodereader.databinding.ActivityMainBinding
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.util.*

class MainActivity : AppCompatActivity(), ZXingScannerView.ResultHandler,
    MessageDialogFragment.MessageDialogListener {

    companion object {
        const val RC_HANDLE_CAMERA_PERM = 2
    }

    private lateinit var mScannerView: ZXingScannerView
    private val mCurrentQRCode: String? = null
    private lateinit var flashlight: ImageView
    private var flash = false
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpScanner()
    }

    override fun onResume() {
        super.onResume()
        mScannerView.setResultHandler(this)
        if (mCurrentQRCode == null) {
            mScannerView.startCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        mScannerView.stopCamera()
    }

    private fun setUpScanner() {
        val contentFrame: ViewGroup = binding.contentFrame
        mScannerView = ZXingScannerView(this)
        contentFrame.addView(mScannerView)

        val rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (PackageManager.PERMISSION_GRANTED != rc) {
            requestCameraPermission()
        }

        flashlight = binding.flashlight
        flashlight.setOnClickListener {
            if (!flash) {
                flash = true
                flashlight.setImageResource(R.drawable.ic_baseline_highlight_yellow)
            } else {
                flash = false
                flashlight.setImageResource(R.drawable.ic_baseline_highlight_white)
            }
            mScannerView.flash = flash
        }

        val formats: MutableList<BarcodeFormat> = ArrayList()
        formats.add(BarcodeFormat.QR_CODE)
        mScannerView.setFormats(formats)
    }

    private fun requestCameraPermission() {
        val permissions = arrayOf(Manifest.permission.CAMERA)

        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            )
        ) {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                RC_HANDLE_CAMERA_PERM
            )
            return
        }

        val thisActivity: Activity = this

        val listener =
            View.OnClickListener {
                ActivityCompat.requestPermissions(
                    thisActivity, permissions,
                    RC_HANDLE_CAMERA_PERM
                )
            }

        mScannerView.setOnClickListener(listener)
        Snackbar.make(
            mScannerView, R.string.permission_camera_rationale,
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.ok, listener)
            .show()
    }

    override fun handleResult(rawResult: Result?) {
        val qrCode: String = rawResult.toString()
        if (isItWebsite(qrCode)) {
            val webPage: Uri = Uri.parse(qrCode)
            val intent = Intent(Intent.ACTION_VIEW, webPage)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        } else {
            showMessageDialog("QR code", qrCode)
        }
    }

    private fun isItWebsite(result: String): Boolean {
        return result.startsWith("http", true)
    }

    override fun onDialogClick(dialogFragment: DialogFragment?) {
        finish()
    }

    override fun showMessageDialog(title: String?, message: String?) {
        val dialogFragment: DialogFragment = MessageDialogFragment.newInstance(title, message, this)
        dialogFragment.show(supportFragmentManager, "message_tag")
        dialogFragment.isCancelable = false
    }

}