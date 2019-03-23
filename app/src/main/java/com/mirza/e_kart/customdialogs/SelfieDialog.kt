package com.mirza.e_kart.customdialogs

import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mirza.e_kart.R
import com.mirza.e_kart.listeners.SelfieDialogListener
import kotlinx.android.synthetic.main.selfie_alert_layout.*

class SelfieDialog : android.support.v4.app.DialogFragment() {
    val TAG = SelfieDialog::class.java.simpleName
    private var selfieDialogListener: SelfieDialogListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(
            DialogFragment.STYLE_NO_TITLE, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                android.R.style.Theme_Material_Light_NoActionBar
            } else {
                android.R.style.Theme_DeviceDefault_Light_NoActionBar
            }
        )
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#4B000000")))
        return inflater.inflate(R.layout.selfie_alert_layout, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        ok_button.setOnClickListener {
            dismiss()
            selfieDialogListener?.onImageSubmit()
        }
        cancel_button.setOnClickListener {
            resetDialog()
            dismiss()
            selfieDialogListener?.onRetryClicked()
        }
        only_ok_button.setOnClickListener {
            selfieDialogListener?.onCameraStart()
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
    }

    fun setListener(selfieDialogListener: SelfieDialogListener) {
        this.selfieDialogListener = selfieDialogListener
    }

    fun resetDialog() {
        icon_image.visibility = View.GONE
        icon_image?.setImageBitmap(null)
        only_ok_button?.visibility = View.VISIBLE
        ok_button?.visibility = View.GONE
        cancel_button?.visibility = View.GONE
    }


    fun setImage(bitmap: Bitmap) {
        icon_image.visibility = View.VISIBLE
        icon_image.setImageBitmap(bitmap)
        only_ok_button.visibility = View.GONE
        ok_button.visibility = View.VISIBLE
        cancel_button.visibility = View.VISIBLE
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        manager?.beginTransaction()?.add(this, tag)?.commitAllowingStateLoss()
    }
}