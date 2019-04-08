package com.mirza.e_kart.networks

import com.mirza.e_kart.networks.MConfig.Companion.CUSTOMER_REQUEST
import com.mirza.e_kart.networks.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface NetworkManager {
    @POST(MConfig.SIGNUP)
    fun doSignUp(@Body userSignup: SignupModel): Call<LoginResponse>

    @FormUrlEncoded
    @POST(MConfig.OTP)
    fun sendOTP(
        @Field("user") user: String = "niyaz",
        @Field("password") password: String = "ESTIIML3",
        @Field("msisdn") mobileNumber: String,
        @Field("sid") sid: String = "NFPLFI",
        @Field("msg") msg: String,
        @Field("fl") fl: Int = 0,
        @Field("gwid") gwid: Int = 2
    ): Call<Any>

    @Headers("Content-Type: application/json")
    @POST(MConfig.LOGIN)
    fun doLogin(@Body loginData: LoginModel): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST(MConfig.PRODUCTS)
    fun getProducts(@Header("Authorization") bearer: String): Call<ProductList>

    @Headers("Content-Type: application/json")
    @POST(MConfig.CATEGORIES)
    fun getCategories(@Header("Authorization") bearer: String): Call<CategoriesModel>

    @FormUrlEncoded
    @POST(MConfig.PRODUCT_IMAGES)
    fun getProductImages(@Header("Authorization") bearer: String, @Field("id") id: Int): Call<ProductImages>

    @FormUrlEncoded
    @POST(MConfig.BRANDS)
    fun getBrands(@Header("Authorization") bearer: String, @Field("id") id: Int): Call<BrandsModel>


    @FormUrlEncoded
    @POST(MConfig.REFERRAL_LIST)
    fun getReferrals(@Header("Authorization") bearer: String, @Field("id") id: Int): Call<ReferralModel>

    @FormUrlEncoded
    @POST(MConfig.ORDERS)
    fun getOrderHistory(@Header("Authorization") bearer: String, @Field("id") id: Int): Call<OrderHistoryModel>

    @Headers("Content-Type: application/json")
    @POST(MConfig.FEEDBACK)
    fun sendFeedback(@Header("Authorization") bearer: String,@Body feedback: FeedbackModel): Call<Any>

    @Multipart
    @POST(CUSTOMER_REQUEST)
    fun sendCustomerRequest(
        @Part("customer_id") customerId: RequestBody,
        @Part("product_id") productId: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("current_address") currentAddress: RequestBody,
        @Part("permanant_address") permanentAddress: RequestBody,
        @Part("pin_code") pinCode: RequestBody,
        @Part("residence_type") residenceType: RequestBody,
        @Part("aadhar_number") aadhaarNumber: RequestBody,
        @Part("pan_number") panNumber: RequestBody,
        @Part("employment_type") empType: RequestBody,
        @Part("company_name") companyName: RequestBody,
        @Part("monthly_salary") monthlySalary: RequestBody,
        @Part("annual_salary") annualIncome: RequestBody,
        @Part("family_member_name") familyMemberName: RequestBody,
        @Part("family_member_number") familyNumber: RequestBody,
        @Part("guarantor_name") guarantorName: RequestBody,
        @Part("guarantor_mobile_number") guarantorMobile: RequestBody,
        @Part("down_payment") downPayment: RequestBody,
        @Part("reference_code") referenceCode: RequestBody,
        @Part("color") color: RequestBody?,
        @Part aadhaarFront: MultipartBody.Part,
        @Part aadhaarBack: MultipartBody.Part,
        @Part pan: MultipartBody.Part,
        @Part bank: MultipartBody.Part,
        @Part selfie: MultipartBody.Part,
        @Part cheque_pic: MultipartBody.Part?
    ): Call<Any>


}

