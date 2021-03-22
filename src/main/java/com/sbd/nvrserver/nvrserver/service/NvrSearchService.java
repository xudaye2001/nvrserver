package com.sbd.nvrserver.nvrserver.service;

import com.alibaba.fastjson.JSONObject;
import com.sbd.nvrserver.nvrserver.utils.OkHttpUtil;
import com.sun.jna.NativeLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: 食客
 * @Date: 2021/3/22 3:53 下午
 */
@Slf4j
@Service
public class NvrSearchService {

    private NativeLong userId;

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
    }


    /**
     * 开始搜索标签
     */
    public void startSearchQr() {
        log.info("开始搜索标签");
        cupPictureAndRecognition();
    }


    /**
     * 截图并获取识别结果
     */
    private void cupPictureAndRecognition() {

        // 等待对焦
        waitForFocuce();

        // 截取图片
        log.info("截图并获取结果");
        String filePath =  nvrControll.capturePicture();

        String response = OkHttpUtil.upDateFile(filePath);

//        OkHttpUtil.qrRecognition(response);
        log.info(response);

        // 向python发送图片, 获取结果;
//        String responseDate = OkHttpUtil.qrRecognition(filePath);
        JSONObject responseDateJSON = JSONObject.parseObject(response);
        String id = responseDateJSON.getString("data");
        String url = "http://192.168.0.7:8004/files/view/"+id;
        String data = OkHttpUtil.qrRecognition(url);
        log.info("识别结果:"+data);
    }




    /**
     * 回到主视图
     */
    private void goToMainView() {
        log.info("回到主视图");
        nvrControll.goToMainView(userId);
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
