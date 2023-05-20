package com.example.amitbindalblogs.utilities

open class NetworkConstants {

    val BASE_URL="https://amitbindal.in/api/"

    val REG_API="${BASE_URL}user/registration"
    val VERIFY_REG_OTP="${BASE_URL}verify/otp"

    val LOGIN_API="${BASE_URL}user/login"
    val LOGIN_VERIFY_API="${BASE_URL}otp/verify"

    val Gmail_Login="${BASE_URL}gmail/login"
    val Facebook_Login="${BASE_URL}facebook/login"

    val Profile_Update="${BASE_URL}profile"
    var token:String=""

}