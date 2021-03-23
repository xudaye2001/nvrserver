package com.sbd.nvrserver.nvrserver.service;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;

@Service
@Slf4j
public class NvrControll {
    private HCNetSDK hcNetSDK;
    private NativeLong userId;


    public String capturePicture(   ) {

        String fileName = UUID.randomUUID().toString();
        // 临时文件名
        String filePath = "/home/sbd/IdeaProjects/nvrserver/" + fileName + ".jpeg";
        //抓图配置
        HCNetSDK.NET_DVR_JPEGPARA lpJpegPara = new HCNetSDK.NET_DVR_JPEGPARA();

        // 0=CIF, 1=QCIF, 2=D1 3=UXGA(1600x1200), 4=SVGA(800x600), 5=HD720p(1280x720),6=VGA
        lpJpegPara.wPicSize = 255;

        // 图片质量系数 0-最好 1-较好 2-一般
        lpJpegPara.wPicQuality = 2;

        boolean result = hcNetSDK
                .NET_DVR_CaptureJPEGPicture(userId, new NativeLong(1), lpJpegPara,
                        filePath);
        if (result) {
            return fileName;
        }else {
            return "";
        }
    }


    /**
     * 左移xx毫秒
     * @param userId 登录id
     * @param millis 时间
     */
    public boolean goToLeftBySec(NativeLong userId,int millis) {
        NativeLong nativeLong = new NativeLong();
        nativeLong.setValue(1);
        hcNetSDK.NET_DVR_PTZControlWithSpeed_Other(userId,nativeLong,HCNetSDK.PAN_LEFT,0,4);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        hcNetSDK.NET_DVR_PTZControlWithSpeed_Other(userId,nativeLong,HCNetSDK.PAN_LEFT,1,4);
        return true;
    }

    /**
     * 右移xx毫秒
     * @param userId 登录id
     * @param millis 时间
     */
    public boolean goToRightBySec(NativeLong userId,int millis) {
        NativeLong nativeLong = new NativeLong();
        nativeLong.setValue(1);
        hcNetSDK.NET_DVR_PTZControlWithSpeed_Other(userId,nativeLong,HCNetSDK.PAN_RIGHT,0,4);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        hcNetSDK.NET_DVR_PTZControlWithSpeed_Other(userId,nativeLong,HCNetSDK.PAN_RIGHT,1,4);
        return true;
    }


    /**
     * 上移xx毫秒
     * @param userId 登录id
     * @param millis 时间
     */
    public boolean goToTopBySec(NativeLong userId,int millis) {
        NativeLong nativeLong = new NativeLong();
        nativeLong.setValue(1);
        hcNetSDK.NET_DVR_PTZControlWithSpeed_Other(userId,nativeLong,HCNetSDK.TILT_UP,0,4);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        hcNetSDK.NET_DVR_PTZControlWithSpeed_Other(userId,nativeLong,HCNetSDK.TILT_UP,1,4);
        return true;
    }


    /**
     * 上移xx毫秒
     * @param userId 登录id
     * @param millis 时间
     */
    public boolean goToDownBySec(NativeLong userId,int millis) {
        NativeLong nativeLong = new NativeLong();
        nativeLong.setValue(1);
        hcNetSDK.NET_DVR_PTZControlWithSpeed_Other(userId,nativeLong,HCNetSDK.TILT_DOWN,0,4);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        hcNetSDK.NET_DVR_PTZControlWithSpeed_Other(userId,nativeLong,HCNetSDK.TILT_DOWN,1,4);
        return true;
    }


    public boolean goTo(NativeLong userId, int index) {
        NativeLong nativeLong = new NativeLong();
        nativeLong.setValue(1);
        boolean isSuccess = hcNetSDK.NET_DVR_PTZPreset_Other(userId,nativeLong,HCNetSDK.GOTO_PRESET,index);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 回到1号预置点
     * @param userId id
     * @return f
     */
    public boolean goToMainView(NativeLong userId) {
        NativeLong nativeLong = new NativeLong();
        nativeLong.setValue(1);
        boolean isSuccess = hcNetSDK.NET_DVR_PTZPreset_Other(userId,nativeLong,HCNetSDK.GOTO_PRESET,1);
        return true;
    }





    public boolean zoomInAlive(NativeLong userId) {
        NativeLong nativeLong = new NativeLong();
        nativeLong.setValue(1);

//        for (int i=0;i<30;i++) {
            hcNetSDK.NET_DVR_PTZControlWithSpeed_Other(userId,nativeLong,HCNetSDK.ZOOM_IN,0,7);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hcNetSDK.NET_DVR_PTZControlWithSpeed_Other(userId,nativeLong,HCNetSDK.PAN_LEFT,1,7);

//        }
        return true;
    }


    public boolean zoomIn(NativeLong userId,int sec) {
        log.info("推进一秒");
        NativeLong nativeLong = new NativeLong();
        nativeLong.setValue(1);

        hcNetSDK.NET_DVR_PTZControl_Other(userId,nativeLong,HCNetSDK.ZOOM_IN,0);
        try {
            Thread.sleep( sec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        hcNetSDK.NET_DVR_PTZControl_Other(userId,nativeLong,HCNetSDK.ZOOM_IN,1);

        return true;
    }

    public boolean zoomOut(NativeLong userId,int sec) {
        log.info("推进一秒");
        NativeLong nativeLong = new NativeLong();
        nativeLong.setValue(1);

        hcNetSDK.NET_DVR_PTZControl_Other(userId,nativeLong,HCNetSDK.ZOOM_OUT,0);
        try {
            Thread.sleep( sec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        hcNetSDK.NET_DVR_PTZControl_Other(userId,nativeLong,HCNetSDK.ZOOM_OUT,1);

        return true;
    }


    public boolean init() {
        hcNetSDK = HCNetSDK.INSTANCE;

        if (!hcNetSDK.NET_DVR_Init()) {
            log.info("海康SDK初始化失败");
            return false;
        }
        // set reconect 3000ms
        hcNetSDK.NET_DVR_SetReconnect(3000,true);

        // set exception call back
//        hcNetSDK.NET_DVR_SetExceptionCallBack_V30(1,1, new HCNetSDK.FExceptionCallBack() {
//            @Override
//            public void invoke(int dwType, NativeLong lUserID, NativeLong lHandle, Pointer pUser) {
//                log.info(dwType+":"+lUserID+":"+lHandle.toString());
//            }
//        },null);

        return true;
    }




    /**
     * login
     */
    public NativeLong login() {
        // login
        userId = hcNetSDK.NET_DVR_Login("192.168.0.26", (short) 8000,"admin","hk123456",new HCNetSDK.NET_DVR_DEVICEINFO());
        return userId;
    }


    /**
     * close
     * @param userId
     */
    public void close(NativeLong userId) {
        // 注销设备
        hcNetSDK.NET_DVR_Logout(userId);
        // 释放SDK
        hcNetSDK.NET_DVR_Cleanup();
    }

}
