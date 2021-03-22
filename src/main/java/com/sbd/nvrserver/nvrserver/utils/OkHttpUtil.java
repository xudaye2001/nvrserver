package com.sbd.nvrserver.nvrserver.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.File;
import java.io.IOException;

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

    public static void qrRecognitionByUrl(String fileName) {
        File file = new File(fileName);
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("192.168.0.7:8004/files/upload")
                .post(RequestBody.create(mediaType, file))
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.info("onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                log.info(response.protocol() + " " +response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    log.info( headers.name(i) + ":" + headers.value(i));
                }
                log.info( "onResponse: " + response.body().string());
            }
        });
    }
}
