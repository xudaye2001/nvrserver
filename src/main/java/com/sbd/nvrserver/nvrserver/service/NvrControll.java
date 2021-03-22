package com.sbd.nvrserver.nvrserver.service;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NvrControll {
    private HCNetSDK hcNetSDK;



    public boolean controller(NativeLong userId) {
        NativeLong nativeLong = new NativeLong();
        nativeLong.setValue(1);
        hcNetSDK.NET_DVR_PTZControl_Other(userId,nativeLong,HCNetSDK.PAN_LEFT,0);
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
        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        }
        return true;
    }


    public boolean zoomIn(NativeLong userId) {
        NativeLong nativeLong = new NativeLong();
        nativeLong.setValue(1);


        for (int i=0;i<4;i++) {
            hcNetSDK.NET_DVR_PTZControl_Other(userId,nativeLong,HCNetSDK.ZOOM_IN,0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hcNetSDK.NET_DVR_PTZControl_Other(userId,nativeLong,HCNetSDK.PAN_LEFT,1);

            try { Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


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
        return hcNetSDK.NET_DVR_Login("192.168.0.26", (short) 8000,"admin","hk123456",new HCNetSDK.NET_DVR_DEVICEINFO());
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
