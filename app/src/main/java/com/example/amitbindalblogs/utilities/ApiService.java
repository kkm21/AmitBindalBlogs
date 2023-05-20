package com.example.amitbindalblogs.utilities;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface ApiService {
    @Multipart
    @POST("/profile")
    Call<ResponseBody> editProfile(
            @Header("Authorization") String auth,
            @Part MultipartBody.Part profile,
            @Part("dob") RequestBody dob,
            @Part("address") RequestBody address,
            @Part("pincode") RequestBody pincode,
            @Part("mobile_number") RequestBody mobile_number,
            @Part("state") RequestBody state,
            @Part("city") RequestBody city,
            @Part("adhar_number") RequestBody adhar_number);


    @GET("/get-blog")
    Call<JSONObject> getBlog(
            @HeaderMap Map<String,String> auth
    );

    @POST("/facebook/login")
    Call<ResponseBody> signInWithFacebook(
            @Query("user_name") String user_name,
            @Query("email") String email,
            @Query("fb_id") String fb_id
    );
}
