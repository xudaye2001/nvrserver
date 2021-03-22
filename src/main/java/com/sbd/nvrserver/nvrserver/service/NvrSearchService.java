package com.sbd.nvrserver.nvrserver.service;

import com.sbd.nvrserver.nvrserver.utils.OkHttpUtil;
import com.sun.jna.NativeLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: 食客
 * @Date: 2021/3/22 3:53 下午
 */
@Slf4j
public class NvrSearchService {

    private NativeLong userId;

    @Autowired
    private NvrControll nvrControll;

    /**
     * 初始化nvr
     */
    public void initNvr() {
        log.info("初始化球机");
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

        log.info("");
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

        // 向python发送图片, 获取结果;
        String response = OkHttpUtil.qrRecognition(filePath);
        log.info("识别结果:"+response);
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
