package com.mirza.e_kart.customdialogs

import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mirza.e_kart.R
import com.mirza.e_kart.extensions.blur
import com.mirza.e_kart.listeners.CustomDialogListener
import kotlinx.android.synthetic.main.custom_alert_layout.*
import kotlinx.android.synthetic.main.custom_alert_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomAlertDialog : android.support.v4.app.DialogFragment() {
    val TAG = CustomAlertDialog::class.java.simpleName
    private var bitmap: Bitmap? = null
    private var resourceId: Int? = null
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private var customDialogListener: CustomDialogListener? = null
    private var positiveButtonText = "OK"
    private var negativeButtonText = "Cancel"
    private var isSingleButton = false
    private var noButtons = false
    private var msg = "message"
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
        return inflater.inflate(R.layout.custom_alert_layout, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        ok_button.text = positiveButtonText
        only_ok_button.text = positiveButtonText
        cancel_button.text = negativeButtonText
        messageTV.text = msg
        if (isSingleButton) {
            only_ok_button.visibility = View.VISIBLE
            ok_button.visibility = View.GONE
            cancel_button.visibility = View.GONE
        } else {
            only_ok_button.visibility = View.GONE
            ok_button.visibility = View.VISIBLE
            cancel_button.visibility = View.VISIBLE
        }
        if (noButtons) {
            only_ok_button.visibility = View.GONE
            ok_button.visibility = View.GONE
            cancel_button.visibility = View.GONE
        }
        resourceId?.let {
            icon_image.setImageResource(it)
        }
        bitmap?.let {
            view.frameLayout2.background = BitmapDrawable(resources, it)
            uiScope.launch {
                view.frameLayout2.background = BitmapDrawable(resources, it.blur(this@CustomAlertDialog.context!!))
            }
        }
    }

    private fun initListeners() {
        ok_button.setOnClickListener {
            dismiss()
            customDialogListener?.onPositiveClicked()
        }
        cancel_button.setOnClickListener {
            dismiss()
            customDialogListener?.onNegativeClicked()
        }
        only_ok_button.setOnClickListener {
            dismiss()
            customDialogListener?.onPositiveClicked()
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        customDialogListener?.onNegativeClicked()
    }

    fun setDismissListener(customDialogListener: CustomDialogListener) {
        this.customDialogListener = customDialogListener
    }

    fun setMessage(msg: String) {
        this.msg = msg
    }


    fun setIcon(resourceId: Int) {
        this.resourceId = resourceId
    }


    fun setSingleButton(value: Boolean) {
        isSingleButton = value
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        manager?.beginTransaction()?.add(this, tag)?.commitAllowingStateLoss()
    }
}