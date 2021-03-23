package com.sbd.nvrserver.nvrserver.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sbd.nvrserver.nvrserver.utils.OkHttpUtil;
import com.sun.jna.NativeLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: 食客
 * @Date: 2021/3/22 3:53 下午
 */
@Slf4j
@Service
public class NvrSearchService {

    private NativeLong userId;

    int size;

    private int times;

    @Autowired
    private NvrControll nvrControll;

    /**
     * 初始化nvr
     */
    public void initNvr() {
        log.info("初始化球机");
        if (nvrControll==null) {
            nvrControll = new NvrControll();
        }
        if (nvrControll.init()) {
            userId = nvrControll.login();
        }

        // 回到主视图
        goToMainView();
        waitForMoving();
    }


    /**
     * 开始搜索标签
     */
    public void startSearchQr() {
        log.info("开始搜索标签");
        List<String> result =  cupPictureAndRecognition();

         size= result.size();
        if (size==0) {
            processByZero();
        }else if (size==4) {
            log.info("4ge");
        }else {
            log.info("data");
        }

    }

    /**
     * 对没有结果返回的处理
     */
    private void processByZero() {

        // 截图
        while (size==0) {
            // 推进一秒
            nvrControll.zoomIn(userId,1);
            size = cupPictureAndRecognition().size();
        }
    }





    /**
     * 截图并获取识别结果
     */
    private List<String> cupPictureAndRecognition() {
        // 等待对焦
        waitForFocuce();

        // 截取图片
        log.info("截图并获取结果");
        String fileNmae =  nvrControll.capturePicture();

        String response = OkHttpUtil.upDateFile(fileNmae);

        log.info(response);

        // 向python发送图片, 获取结果;
        JSONObject responseDateJSON = JSONObject.parseObject(response);
        String id = responseDateJSON.getString("data");
        String url = "http://192.168.0.7:8004/files/view/"+id;
        String data = OkHttpUtil.qrRecognition(url);
        log.info("识别结果:"+data);
        List<String> dataList = new ArrayList<>();
        if (data==null||"".equals(data)) {
            return dataList;
        }else {
            JSONObject responseData = JSONObject.parseObject(data);
            JSONArray jsonArray = responseData.getJSONArray("data");
            if (jsonArray==null||jsonArray.size()==0) {
                return dataList;
            }
            dataList = jsonArray.toJavaList(String.class);
            log.info(dataList.toString());
            return dataList;
        }


    }




    /**
     * 回到主视图
     */
    private void goToMainView() {
        log.info("回到主视图");
        nvrControll.goToMainView(userId);
    }



    public void waitForMoving() {
        log.info("等待moving");
        try {
            Thread.sleep(4500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }






    public void waitForFocuce() {
        log.info("等待对焦");
        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
