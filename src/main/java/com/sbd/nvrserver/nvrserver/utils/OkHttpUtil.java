package com.sbd.nvrserver.nvrserver.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @Author: 食客
 * @Date: 2021/3/22 4:29 下午
 */
public class OkHttpUtil {


    public static String qrRecognition(String fileName) {

        String url = "http://127.0.0.1:9999/api/cpr?image_path="+fileName+"&copyright=cbishi.com";

        //新建一个OkHttpClient对象
        OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            //获取响应并把响应体返回
            try (Response response = client.newCall(request).execute()) {
                assert response.body() != null;
                return response.body().string();
            }catch (RuntimeException | IOException e) {
                e.getMessage();
            }
            return null;
    }
}
