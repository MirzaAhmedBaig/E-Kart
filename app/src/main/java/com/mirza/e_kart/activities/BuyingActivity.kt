package com.mirza.e_kart.activities

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Spinner
import com.mirza.e_kart.R
import com.mirza.e_kart.adapters.SpinnerAdapter
import com.mirza.e_kart.extensions.employmentType
import com.mirza.e_kart.extensions.genderList
import com.mirza.e_kart.extensions.pinCodes
import com.mirza.e_kart.extensions.residences
import kotlinx.android.synthetic.main.content_buy_activity.*
import java.text.SimpleDateFormat
import java.util.*


class BuyingActivity : AppCompatActivity() {

    private val TAG = BuyingActivity::class.java.simpleName

    private val myCalendar by lazy {
        Calendar.getInstance()
    }

    private val datePickerListener by lazy {
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            setDateTodobET()
        }
    }


    private val datePickerDialog by lazy {
        DatePickerDialog(
            this, datePickerListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.mirza.e_kart.R.layout.activity_buying)
        setListeners()
        initSpinner(gender, genderList, 0)
        initSpinner(pin_codes, pinCodes, 1)
        initSpinner(residence_type, residences, 2)
        initSpinner(empolyement_type, employmentType, 3)
    }

    private fun setListeners() {
        buying_layout.setOnClickListener {
            closeKeyBoard()
        }
        dob.setOnClickListener {
            datePickerDialog.show()
        }

        address_checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                permanent_address.setText(current_address.text.toString())
            } else {
                permanent_address.setText("")
            }
        }
    }

    private fun initSpinner(spinner: Spinner, dataList: ArrayList<String>, type: Int) {
        val spinnerAdapter = SpinnerAdapter(this, R.layout.custom_spinner_item, dataList)
        spinner.adapter = spinnerAdapter
        spinner.setSelection(0, false)
        spinner.isSelected = false
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Log.d(TAG, "OnItemSelelcted called : $position $type")
                if (position != 0) {
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    private fun setDateTodobET() {
        val myFormat = "dd/MM/yy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        dob.text = sdf.format(myCalendar.time)
    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
