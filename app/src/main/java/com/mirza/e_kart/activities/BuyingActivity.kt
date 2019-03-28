package com.mirza.e_kart.activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Spinner
import com.mirza.e_kart.R
import com.mirza.e_kart.adapters.SpinnerAdapter
import com.mirza.e_kart.constants.Constants
import com.mirza.e_kart.customdialogs.CustomAlertDialog
import com.mirza.e_kart.customdialogs.SelfieDialog
import com.mirza.e_kart.extensions.*
import com.mirza.e_kart.listeners.CustomDialogListener
import com.mirza.e_kart.listeners.SelfieDialogListener
import com.mirza.e_kart.networks.ClientAPI
import com.mirza.e_kart.preferences.AppPreferences
import kotlinx.android.synthetic.main.activity_buying.*
import kotlinx.android.synthetic.main.content_buy_activity.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class BuyingActivity : AppCompatActivity() {

    private val TAG = BuyingActivity::class.java.simpleName

    private var selectedGender = ""
    private var selectedPinCode = -1
    private var selectedResidence = ""
    private var selectedEmployment = ""
    private var documentsPathList = arrayOfNulls<String?>(5)

    private val permsRequestCode = 200
    private var cameraImageCode = -1
    private val perms = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val myCalendar by lazy {
        Calendar.getInstance()
    }

    private val appPreferences by lazy {
        AppPreferences(this)
    }

    private val productId by lazy {
        intent.getIntExtra("id", -1)
    }

    private val datePickerListener by lazy {
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            setDateTodobET()
            dob.error = null
            gender.requestFocus()
            gender.performClick()
        }
    }


    private val datePickerDialog by lazy {
        DatePickerDialog(
            this, datePickerListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
        }
    }

    private val selfieDialog by lazy {
        SelfieDialog().apply {
            setListener(object : SelfieDialogListener {
                override fun onCameraStart() {
                    openCamera(Constants.SELFIE_PIC_REQUEST)
                }

                override fun onImageSubmit() {
                    Log.d(TAG, "onImageSubmit")
                    sendCustomerRequest()
                }

                override fun onRetryClicked() {
                    Handler().postDelayed({
                        show(supportFragmentManager, "selfie_dialog")
                    }, 500)
                }

            })
        }
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
        Log.d(TAG, "Id : ${productId}")
    }

    private fun init() {
        u_pan.filters = arrayOf<InputFilter>(InputFilter.AllCaps(), InputFilter.LengthFilter(10))
        referral_code.filters = arrayOf<InputFilter>(InputFilter.AllCaps(), InputFilter.LengthFilter(6))
    }

    private fun setListeners() {
        back_button.setOnClickListener {
            finish()
        }
        buying_layout.setOnClickListener {
            closeKeyBoard()
        }

        submit_btn.setOnClickListener {
            clearErrors()
            if (validateForm()) {
                selfieDialog.show(supportFragmentManager, "selfie_dialog")
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
            showAlert(
                "Please take image of Aadhaar card's front side, any invalid image will not approve your loan.",
                false
            ) {
                openCamera(Constants.AADHAAR_FRONT_PIC_REQUEST)
            }
        }
        aadhaar_card_back.setOnClickListener {
            showAlert(
                "Please take image of Aadhaar card's back side, any invalid image will not approve your loan.",
                false
            ) {
                openCamera(Constants.AADHAAR_BACK_PIC_REQUEST)
            }
        }
        pan_card.setOnClickListener {
            showAlert(
                "Please take image of Pan card's front side, any invalid image will not approve your loan.",
                false
            ) {
                openCamera(Constants.PAN_CARD_PIC_REQUEST)
            }

        }

        passbook.setOnClickListener {
            showAlert(
                "Please take image of Bank Passbook front side, any invalid image will not approve your loan.",
                false
            ) {
                openCamera(Constants.PASSBOOK_PIC_REQUEST)
            }

        }

        u_name_first.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_FORWARD) {
                    Handler().postDelayed({
                        u_name_last.requestFocus()
                    }, 100)
                    return true
                }
                return false
            }
        })

        u_name_last.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_FORWARD) {
                    Handler().postDelayed({
                        u_name_last.clearFocus()
                        closeKeyBoard()
                        datePickerDialog.show()
                    }, 100)
                    return true
                }
                return false
            }
        })

        u_pan.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_FORWARD) {
                    Handler().postDelayed({
                        u_pan.clearFocus()
                        closeKeyBoard()
                        empolyment_type.requestFocus()
                        empolyment_type.performClick()
                    }, 100)
                    return true
                }
                return false
            }
        })
        monthly_income.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_FORWARD) {
                    Handler().postDelayed({
                        annual_income.requestFocus()
                    }, 100)
                    return true
                }
                return false
            }
        })
    }

    private fun initSpinner(spinner: Spinner, dataList: ArrayList<String>, type: Int) {
        val spinnerAdapter = SpinnerAdapter(this, R.layout.custom_spinner_item, dataList)
        spinner.adapter = spinnerAdapter
        spinner.setSelection(0, false)
        spinner.isSelected = false
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Log.d(TAG, "onItemSelected called : $position $type")
                if (position != 0) {
                    when (type) {
                        Constants.GENDER -> {
                            gender_error.error = null
                            selectedGender = genderList[position]
                            current_address.requestFocus()
                            openKeyBoard()
                        }
                        Constants.PINCODES -> {
                            closeKeyBoard()
                            pin_error.error = null
                            selectedPinCode = pinCodes[position].toInt()
                            residence_type.requestFocus()
                            residence_type.performClick()
                        }
                        Constants.RESIDENCES -> {
                            residences_error.error = null
                            selectedResidence = residences[position]
                            u_aadhaar.requestFocus()
                        }
                        Constants.EMPLOYMENT -> {
                            empolyment_error.error = null
                            selectedEmployment = employmentType[position]
                            company_name.requestFocus()
                            closeKeyBoard()
                            openKeyBoard()
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

    private fun openKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.showSoftInput(view, 0)
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

        if (documentsPathList[4] == null) {
            showAlert("Please take Bank Passbook's front page image")
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
        cameraImageCode = requestCode
        if (checkForPermission()) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
            imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, requestCode)
        }
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
                    Constants.SELFIE_PIC_REQUEST -> {
                        documentsPathList[3] = imageurl
                        selfieDialog.setImage(thumbnail)
                    }
                    Constants.PASSBOOK_PIC_REQUEST -> {
                        documentsPathList[4] = imageurl
                        passbook.setImageBitmap(thumbnail)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }




    private fun checkForPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, perms, permsRequestCode)
            false
        } else {
            true
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            permsRequestCode -> {
                if (grantResults.isEmpty())
                    return
                val locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                val storageAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED
                if (locationAccepted && cameraAccepted && storageAccepted) {
                    openCamera(cameraImageCode)
                } else {
                    showToast("Need all permissions")
                }
            }

        }

    }

    private fun sendCustomerRequest() {
        if (!isNetworkAvailable()) {
            val dialog = CustomAlertDialog().apply {
                setMessage("Please check your internet.")
                setIcon(R.drawable.ic_warning)
                setSingleButton(true)
            }
            dialog.show(supportFragmentManager, "select_day_alert")
            return
        }
        showLoadingAlert("Please waite while we submit your request. It may take few minutes to complete.")


        val c_id = RequestBody.create(okhttp3.MultipartBody.FORM, appPreferences.getUser().id.toString())
        val p_id = RequestBody.create(okhttp3.MultipartBody.FORM, productId.toString())
        val dobValue = RequestBody.create(okhttp3.MultipartBody.FORM, myCalendar.time.toString())
        val genderText = RequestBody.create(okhttp3.MultipartBody.FORM, gender.selectedItem.toString())
        val c_addr = RequestBody.create(okhttp3.MultipartBody.FORM, current_address.text.toString())
        val p_addr = RequestBody.create(okhttp3.MultipartBody.FORM, permanent_address.text.toString())
        val pin = RequestBody.create(okhttp3.MultipartBody.FORM, pin_codes.selectedItem.toString())
        val residence = RequestBody.create(okhttp3.MultipartBody.FORM, residence_type.selectedItem.toString())
        val aadhaar = RequestBody.create(okhttp3.MultipartBody.FORM, u_aadhaar.text.toString())
        val pan = RequestBody.create(okhttp3.MultipartBody.FORM, u_pan.text.toString())
        val empType = RequestBody.create(okhttp3.MultipartBody.FORM, empolyment_type.selectedItem.toString())
        val compName = RequestBody.create(okhttp3.MultipartBody.FORM, company_name.text.toString())
        val m_sal = RequestBody.create(okhttp3.MultipartBody.FORM, monthly_income.text.toString())
        val a_sal = RequestBody.create(okhttp3.MultipartBody.FORM, annual_income.text.toString())
        val f_name = RequestBody.create(okhttp3.MultipartBody.FORM, family_member_name.text.toString())
        val f_number = RequestBody.create(okhttp3.MultipartBody.FORM, family_member_name.text.toString())
        val g_name = RequestBody.create(okhttp3.MultipartBody.FORM, guarantor_name.text.toString())
        val g_number = RequestBody.create(okhttp3.MultipartBody.FORM, guarantor_number.text.toString())
        val r_code = RequestBody.create(okhttp3.MultipartBody.FORM, referral_code.text.toString())

        compressFiles(this, documentsPathList)

        val a_f_image = File(documentsPathList[0])
        val a_b_image = File(documentsPathList[1])
        val p_image = File(documentsPathList[2])
        val pass_image = File(documentsPathList[4])
        val s_image = File(documentsPathList[3])

        val a_front = MultipartBody.Part.createFormData(
            "aadhar_photo_front",
            a_f_image.name,
            RequestBody.create(MediaType.parse("image/*"), a_f_image)
        )

        val a_back = MultipartBody.Part.createFormData(
            "aadhar_photo_back",
            a_b_image.name,
            RequestBody.create(MediaType.parse("image/*"), a_b_image)
        )

        val pan_front = MultipartBody.Part.createFormData(
            "pan_photo",
            p_image.name,
            RequestBody.create(MediaType.parse("image/*"), p_image)
        )
        val pass_front = MultipartBody.Part.createFormData(
            "bank_passbook_photo",
            pass_image.name,
            RequestBody.create(MediaType.parse("image/*"), pass_image)
        )
        val selfie_front = MultipartBody.Part.createFormData(
            "image",
            s_image.name,
            RequestBody.create(MediaType.parse("image/*"), s_image)
        )


        val call = ClientAPI.clientAPI.sendCustomerRequest(
            "Bearer " + appPreferences.getJWTToken(),
            c_id,
            p_id,
            dobValue,
            genderText,
            c_addr,
            p_addr,
            pin,
            residence,
            aadhaar,
            pan,
            empType,
            compName,
            m_sal,
            a_sal,
            f_name,
            f_number,
            g_name,
            g_number,
            r_code,
            a_front,
            a_back,
            pan_front,
            pass_front,
            selfie_front
        )
        Log.d(TAG, "Request URL : ${call.request().url()}")
        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                hideLoadingAlert()
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse == null) {
                        showToast("Please try after sometime")
                        return
                    }
                    /*val jObjError = JSONObject(response.errorBody()!!.string())
                    Log.d(TAG, "${jObjError.getString("message")}")*/
                    val dialog = CustomAlertDialog().apply {
                        setMessage("Your request has been successfully submitted, you can check order status in orders history.")
                        setSingleButton(true)
                        isCancelable = false
                        setDismissListener(object : CustomDialogListener {
                            override fun onPositiveClicked() {
                                finishAffinity()
                                startActivity(Intent(this@BuyingActivity, HomeActivity::class.java))
                            }
                        })
                    }
                    dialog.show(supportFragmentManager, "select_day_alert")
                } else {
                    showToast("Internal server error, please try again")

                }

                Log.d(TAG, "Response Code : ${response.code()}")
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                hideLoadingAlert()
                t.printStackTrace()
                showToast("Network Error!")
            }
        })
    }


}