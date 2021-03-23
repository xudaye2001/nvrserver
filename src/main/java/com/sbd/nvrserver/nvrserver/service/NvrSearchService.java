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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: 食客
 * @Date: 2021/3/22 3:53 下午
 */
@Slf4j
@Service
public class NvrSearchService {

    private NativeLong userId;

    List<Map<String,String>> result= new ArrayList<>();

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
        cupPictureAndRecognition();


        while (result.size()==0) {
                if (times<3) {
                    processByZero();
                    times++;
                }
        }
        // go to far
        nvrControll.zoomOut(userId,3000);
        waitForMoving();
        getOffset();
        result.clear();
        while (result.size()!=4) {
            if (result.size()==0) {
                nvrControll.zoomIn(userId,300);
            }
            getOffset();
            processByPool();
        }
        log.info("牛逼 识别出了4个");
        log.info(result.toString());

    }

    /**
     * 对没有结果返回的处理
     */
    private void processByZero() {

        // 截图
        while (result.size()==0) {
            // 推进一秒
            nvrControll.zoomIn(userId,500);
            cupPictureAndRecognition().size();
        }
    }

    /**
     * 对<4个标签的处理
     */
    private void processByPool() {
//        while (result.size()<4) {
            // 居中
//            getOffset();
            cupPictureAndRecognition();
//            break;
//        }
    }

    /**
     * 通过返回值获取偏移值
     */
    private void getOffset() {
        int imageCenterX = 860;
        int imageCenterY = 540;
        // 中心范围
        int centerAreaX = 200;
        int centerAreaY = 120;
        // 最小x
        int xMin=1920;
        int xMax=0;

        int yMin = 1080;
        int yMax = 0;

        // 获取二维码的x范围
        for (Map<String,String> map:result) {
            int x = Integer.parseInt(map.get("x"));
            int y = Integer.parseInt(map.get("y"));
            if (x<xMin) {
                xMin = x;
            }
            if (x>xMax) {
                xMax= x;
            }

            if (y<yMin) {
                yMin = y;
            }
            if (y>yMax) {
                yMax= y;
            }

        }

        // 计算二维码的中心坐标
        int currentCenterX = (xMax-xMin)/2+xMin;
        int currentCenterY = (yMax-yMin)/2+yMin;

//         判断中心点在画面左右
        int offsetValueX = currentCenterX-imageCenterX;
        if (offsetValueX>0){
            if (offsetValueX<centerAreaX) {
                log.info("小于偏移值, 不需要移动");
            }else {
                // 左移
                nvrControll.goToRightBySec(userId,offsetValueX/2);
            }
        }else {
            if (-offsetValueX<centerAreaX) {
                log.info("小于偏移值, 不需要移动");
            }else {
                // 右移
                nvrControll.goToLeftBySec(userId,-offsetValueX/2);
            }
        }

        // y
        int offsetValueY = currentCenterY-imageCenterY;
        if (offsetValueY>0){
            if (offsetValueY<centerAreaY) {
                log.info("小于偏移值, 不需要移动");
            }else {
                // 左移
                nvrControll.goToDownBySec(userId,offsetValueY/3);
            }
        }else {
            if (-offsetValueY<centerAreaY) {
                log.info("小于偏移值, 不需要移动");
            }else {
                // 右移
                nvrControll.goToTopBySec(userId,-offsetValueY/3);
            }
        }


        if (xMin>200&&xMax<1720 && yMin>90&&yMax<880) {
            nvrControll.zoomIn(userId,200);
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
                return new ArrayList<>();
            }
            result.clear();
            jsonArray.stream().forEach(pb -> {
                Map<String, String> rightMap = (Map<String, String>) pb;
                result.add(rightMap);
            });
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
            Thread.sleep(4500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
