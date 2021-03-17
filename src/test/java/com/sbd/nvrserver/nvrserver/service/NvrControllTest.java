package com.sbd.nvrserver.nvrserver.service;

import com.sun.jna.NativeLong;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NvrControllTest {


    @Test
    void init() {
        NvrControll nvrControll = new NvrControll();
        nvrControll.init();
        NativeLong userId = nvrControll.login();
        nvrControll.moveLeft(userId);
        nvrControll.close(userId);
    }
}