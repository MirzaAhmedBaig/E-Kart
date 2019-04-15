package com.mirza.e_kart.classes

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View


/**
 * Created by Mirza Ahmed Baig on 19/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */

class RecyclerItemClickListener(
    context: Context,
    listener: OnItemClickListener
) :
    RecyclerView.OnItemTouchListener {
    override fun onTouchEvent(p0: RecyclerView, p1: MotionEvent) {

    }

    override fun onInterceptTouchEvent(p0: RecyclerView, e: MotionEvent): Boolean {
        val childView = p0.findChildViewUnder(e.x, e.y)
        if (childView != null && mGestureDetector!!.onTouchEvent(e)) {
            mListener.onItemClick(childView, p0.getChildAdapterPosition(childView))
            return true
        }
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(p0: Boolean) {
    }

    var mListener: OnItemClickListener = listener

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    private var mGestureDetector: GestureDetector? = null

    init {
        mGestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent) {
            }
        })
    }


}