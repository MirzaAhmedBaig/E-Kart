package com.mirza.e_kart.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Spinner
import com.mirza.e_kart.R
import com.mirza.e_kart.adapters.SpinnerAdapter
import com.mirza.e_kart.constants.Constants
import com.mirza.e_kart.customdialogs.CustomAlertDialog
import com.mirza.e_kart.extensions.*
import kotlinx.android.synthetic.main.content_buy_activity.*
import java.text.SimpleDateFormat
import java.util.*


class BuyingActivity : AppCompatActivity() {

    private val TAG = BuyingActivity::class.java.simpleName

    private var selectedGender = ""
    private var selectedPinCode = -1
    private var selectedResidence = ""
    private var selectedEmployment = ""
    private var documentsPathList = arrayOfNulls<String?>(4)
    private val myCalendar by lazy {
        Calendar.getInstance()
    }

    private val datePickerListener by lazy {
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            setDateTodobET()
            dob.error = null
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
        init()
        setListeners()
        initSpinner(gender, genderList, Constants.GENDER)
        initSpinner(pin_codes, pinCodes, Constants.PINCODES)
        initSpinner(residence_type, residences, Constants.RESIDENCES)
        initSpinner(empolyment_type, employmentType, Constants.EMPLOYMENT)
    }

    private fun init() {
        u_pan.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
        referral_code.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
    }

    private fun setListeners() {
        buying_layout.setOnClickListener {
            closeKeyBoard()
        }

        submit_btn.setOnClickListener {
            clearErrors()
            if (validateForm()) {

            }
        }
        dob.setOnClickListener {
            it.clearFocus()
            datePickerDialog.show()
        }

        address_checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                permanent_address.setText(current_address.text.toString())
            } else {
                permanent_address.setText("")
            }
        }

        datePickerDialog.setOnDismissListener {
            if (dob.text.isNotBlank()) {
                dob.error = null
            }
        }

        aadhaar_card_front.setOnClickListener {
            openCamera(Constants.AADHAAR_FRONT_PIC_REQUEST)
        }
        aadhaar_card_back.setOnClickListener {
            openCamera(Constants.AADHAAR_BACK_PIC_REQUEST)
        }
        pan_card.setOnClickListener {
            openCamera(Constants.PAN_CARD_PIC_REQUEST)
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
                    when (type) {
                        Constants.GENDER -> {
                            gender_error.error = null
                            selectedGender = genderList[position]
                        }
                        Constants.PINCODES -> {
                            pin_error.error = null
                            selectedPinCode = pinCodes[position].toInt()
                        }
                        Constants.RESIDENCES -> {
                            residences_error.error = null
                            selectedResidence = residences[position]
                        }
                        Constants.EMPLOYMENT -> {
                            empolyment_error.error = null
                            selectedEmployment = employmentType[position]
                        }
                    }
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

    private fun validateForm(): Boolean {
        if (u_name_first.text.isBlank()) {
            u_name_first.requestFocus()
            u_name_first.error = "Enter first name"
            return false
        }
        if (u_name_last.text.isBlank()) {
            u_name_last.requestFocus()
            u_name_last.error = "Enter last name"
            return false
        }
        if (dob.text.isBlank()) {
            dob.requestFocus()
            dob.error = "Select birth date"
            return false
        }
        if (gender.selectedItemPosition == 0) {
            gender_error.requestFocus()
            gender_error.error = "Select gender"
            return false
        }

        if (current_address.text.isBlank()) {
            current_address.requestFocus()
            current_address.error = "Enter address"
            return false
        }
        if (permanent_address.text.isBlank()) {
            permanent_address.requestFocus()
            permanent_address.error = "Enter address"
            return false
        }
        if (pin_codes.selectedItemPosition == 0) {
            pin_error.requestFocus()
            pin_error.error = "Select Pin"
            return false
        }
        if (residence_type.selectedItemPosition == 0) {
            residences_error.requestFocus()
            residences_error.error = "Select Residence"
            return false
        }
        if (u_aadhaar.text.isBlank() || u_aadhaar.text.toString().trim().length < 12) {
            u_aadhaar.requestFocus()
            u_aadhaar.error = "Enter valid Aadhaar number"
            return false
        }
        if (u_pan.text.isBlank() || u_pan.text.toString().trim().length < 10) {
            u_pan.requestFocus()
            u_pan.error = "Enter valid Pan number"
            return false
        }

        if (empolyment_type.selectedItemPosition == 0) {
            empolyment_error.requestFocus()
            empolyment_error.error = "Select Employment type"
            return false
        }
        if (company_name.text.isBlank()) {
            company_name.requestFocus()
            company_name.error = "Enter company name"
            return false
        }

        if (monthly_income.text.isBlank()) {
            monthly_income.requestFocus()
            monthly_income.error = "Enter income"
            return false
        }
        if (annual_income.text.isBlank()) {
            annual_income.requestFocus()
            annual_income.error = "Enter income"
            return false
        }
        if (family_member_name.text.isBlank()) {
            family_member_name.requestFocus()
            family_member_name.error = "Enter family member name"
            return false
        }
        if (family_member_number.text.isBlank() || family_member_number.text.toString().trim().length < 10) {
            family_member_number.requestFocus()
            family_member_number.error = "Enter valid mobile number"
            return false
        }

        if (guarantor_name.text.isBlank()) {
            guarantor_name.requestFocus()
            guarantor_name.error = "Enter guarantor name"
            return false
        }
        if (guarantor_number.text.isBlank() || guarantor_number.text.toString().trim().length < 10) {
            guarantor_number.requestFocus()
            guarantor_number.error = "Enter valid mobile number"
            return false
        }

        if (documentsPathList[0] == null || documentsPathList[1] == null) {
            showAlert("Please take Aaadhaar front and back image")
            return false
        }

        if (documentsPathList[2] == null) {
            showAlert("Please take Pan Card image")
            return false
        }

        return true
    }

    private fun clearErrors() {
        u_name_first.error = null
        u_name_last.error = null
        guarantor_number.error = null
        guarantor_name.error = null
        family_member_number.error = null
        family_member_name.error = null
        annual_income.error = null
        monthly_income.error = null
        company_name.error = null
        empolyment_error.error = null
        u_pan.error = null
        u_aadhaar.error = null
        residences_error.error = null
        pin_error.error = null
        permanent_address.error = null
        current_address.error = null
        gender_error.error = null
        dob.error = null
    }

    private var imageUri: Uri? = null
    private fun openCamera(requestCode: Int) {
        /*val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, requestCode)*/
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            try {
                val thumbnail = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                val imageurl = imageUri?.getRealPathFromURI(contentResolver)
                Log.d(TAG, "Path : $imageurl")
                when (requestCode) {
                    Constants.AADHAAR_FRONT_PIC_REQUEST -> {
                        aadhaar_card_front.setImageBitmap(thumbnail)
                        documentsPathList[0] = imageurl
                    }
                    Constants.AADHAAR_BACK_PIC_REQUEST -> {
                        aadhaar_card_back.setImageBitmap(thumbnail)
                        documentsPathList[1] = imageurl
                    }
                    Constants.PAN_CARD_PIC_REQUEST -> {
                        pan_card.setImageBitmap(thumbnail)
                        documentsPathList[2] = imageurl
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

//        val image = data?.extras?.get("data") as Bitmap? ?: return

    }

    private fun showAlert(message: String) {
        val dialog = CustomAlertDialog().apply {
            setMessage(message)
            setIcon(R.drawable.ic_warning)
        }
        dialog.show(supportFragmentManager, "validation_alert")
        return
    }
}
