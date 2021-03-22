package com.sbd.nvrserver.nvrserver.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @Author: 食客
 * @Date: 2021/3/22 4:29 下午
 */
@Slf4j
public class OkHttpUtil {


    public static String qrRecognition(String fileName) {

        String url = "http://192.168.0.7:9999/api/cpr?image_path="+fileName+"&copyright=cbishi.com";

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


//    public static String qrRecognitionByUrl(String url) {
//
//        //新建一个OkHttpClient对象
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url(url)
//                .get()
//                .build();
//        //获取响应并把响应体返回
//        try (Response response = client.newCall(request).execute()) {
//            assert response.body() != null;
//            return response.body().string();
//        }catch (RuntimeException | IOException e) {
//            e.getMessage();
//        }
//        return null;
//    }

    public static String upDateFile(String fileName) {
        final String[] st = new String[1];
        File file = new File(fileName);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(MediaType.parse("multipart/form-data"), new File(fileName)))
                .build();

        Request request = new Request.Builder()
//                .header("Authorization", "Client-ID " + UUID.randomUUID())
                .url("http://192.168.0.7:8004/files/upload")
                .post(requestBody)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert response != null;
        if (!response.isSuccessful()) try {
            throw new IOException("Unexpected code " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
