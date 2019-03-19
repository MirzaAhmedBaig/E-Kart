package com.mirza.e_kart.customdialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mirza.e_kart.R
import kotlinx.android.synthetic.main.fragment_loading_alert_dialog.view.*

class LoadingAlertDialog : android.support.v4.app.DialogFragment() {

    val TAG = LoadingAlertDialog::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.fragment_loading_alert_dialog, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.progressBar?.progressDrawable?.setColorFilter(
            Color.parseColor("#D9905B"),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        manager?.beginTransaction()?.add(this, tag)?.commitAllowingStateLoss()
    }

}
