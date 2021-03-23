package com.sbd.nvrserver.nvrserver.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sbd.nvrserver.nvrserver.cocurrent.CallbackTask;
import com.sbd.nvrserver.nvrserver.cocurrent.CallbackTaskScheduler;
import com.sbd.nvrserver.nvrserver.utils.OkHttpUtil;
import com.sun.jna.NativeLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.PortInUseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

/**
 * @Author: 食客
 * @Date: 2021/3/22 3:53 下午
 */
@Slf4j
@Service
public class NvrSearchService {

    private NativeLong userId;

//    public static Set<Map<String,String>> result= new HashSet<>();

    public static Map<String,Map<String,String>> results = new HashMap<>();

    private int times;
    private int doTimes;


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
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }





    /**
     * 开始搜索标签
     */
    public void startSearchQr() {
        log.info("开始搜索标签");
        String fileName =  nvrControll.capturePicture();
        cupPictureAndRecognitionSycn(fileName);;

        while (results.size()==0) {
                if (times<3) {
                    processByZero();
                    times++;
                }
        }
        // go to far
        getOffset();
        nvrControll.zoomOut(userId,3000);
        waitForMoving();
        results.clear();
        while (results.size()<4) {
            if (results.size()==0) {
                nvrControll.zoomIn(userId,300);
            }
            getOffset();
            processByPoolSycn();
        }
        log.info("牛逼 识别出了4个");
        log.info(results.toString());

    }

    /**
     * 对没有结果返回的处理
     */
    private void processByZero() {

        // 截图
        while (results.size()==0) {
            // 推进一秒
            nvrControll.zoomIn(userId,1000);
            processByPoolSycn();
        }
    }

    /**
     * 对<4个标签的处理
     */
    private void processByPoolSycn() {
//        cupPictureAndRecognition();
        results.clear();
        // 不同焦点识别
        times=40;
        doTimes=40;
        for (int i=0;i<times;i++) {
            nvrControll.changeFocus(50);
            String fileName =  nvrControll.capturePicture();
            cutpictureSync(fileName);
        }

        while (doTimes>0) {
            try {
                Thread.sleep(1000);
                log.info("wait for result");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }




    /**
     * 线程池截图
     */
    private void cutpictureSync(String fileName) {
        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {

            @Override
            public Boolean execute() throws PortInUseException {
                try {
                    cupPictureAndRecognitionSycn(fileName);
                }catch (Exception e) {
                    log.error(e.getMessage());
                }finally {
                    doTimes--;
                }
                return true;
            }

            @Override
            public void onBack(Boolean aBoolean) {
                if (aBoolean) {
                    log.info("成功");
                }else {
                    log.info("失败");
                }
            }

            @Override
            public void onException(Throwable t) {
                log.error("报错:"+t.getMessage());
            }
        });
    }



    /**
     * 通过返回值获取偏移值
     */
    private void getOffset() {
        int imageCenterX = 860;
        int imageCenterY = 540;
        // 中心范围
        int centerAreaX = 200;
        int centerAreaY = 80;
        // 最小x
        int xMin=1920;
        int xMax=0;

        int yMin = 1080;
        int yMax = 0;

        // 获取二维码的x范围


        for (Map.Entry<String,Map<String,String>> map:results.entrySet()) {
            log.info("process:"+map.getKey());
            int x = Integer.parseInt(results.get(map.getKey()).get("x"));
            int y = Integer.parseInt(results.get(map.getKey()).get("y"));
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
            nvrControll.zoomIn(userId,300);
        }
    }


    /**
     * 截图并获取识别结果
     */
    private JSONArray cupPictureAndRecognitionSycn(String fileNmae) {
        // 等待对焦
        // 截取图片
//        log.info("截图并获取结果");


        String response = OkHttpUtil.upDateFile(fileNmae);

//        log.info(response);

        // 向python发送图片, 获取结果;
        JSONObject responseDateJSON = JSONObject.parseObject(response);
        String id = responseDateJSON.getString("data");
        String url = "http://192.168.0.7:8004/files/view/"+id;
        String data = OkHttpUtil.qrRecognition(url);
//        log.info("识别结果:"+data);
        List<String> dataList = new ArrayList<>();
        if (data==null||"".equals(data)) {
            return new JSONArray();
        }else {
            JSONObject responseData = JSONObject.parseObject(data);
            JSONArray jsonArray = responseData.getJSONArray("data");
            if (jsonArray==null||jsonArray.size()==0) {
                return new JSONArray();
            }

            update(jsonArray);

            return jsonArray;
        }
    }

    private synchronized void update(JSONArray jsonArray) {
        jsonArray.stream().forEach(pb -> {
            Map<String, String> rightMap = (Map<String, String>) pb;
            Map<String,String> location = new HashMap<>();
            location.put("x",rightMap.get("x"));
            location.put("y",rightMap.get("y"));
            results.put(rightMap.get("value"),location);
//            result.add(rightMap);
        });
        log.info("resultSize:"+results.size());
    }




//    /**
//     * 更新数据
//     */
//    private void updateResult(Map<String,String> data) {
//        for (Map<String,String> resultCurrent:result) {
//            if (data.get("value").equals(resultCurrent.get("value")))
//        }
//    }




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
