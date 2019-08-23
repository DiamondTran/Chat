package com.diamond.diamond.chat.fragment;

import com.diamond.diamond.chat.Notifications.MyRespones;
import com.diamond.diamond.chat.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
@Headers(
        {
                "Content-Type:application/json",
                "Authorization:key=AAAAyPxKD0k:APA91bFklgDq2nD1hSivKe-GzXwi68Sc1d_6R6wyIXJxNkKVJZzBS2jqiw0t52TRZrMF02EvlTANqML3TdNHe0jc1q9--alnRn-IuM5xHrhpftphiASpzDQ7ABVj5HCGq-jnPTiGosKm"
        }
    )

   @POST("fcm/send")
    Call<MyRespones>  sendNotification(@Body Sender body);
}

