package com.sbd.nvrserver.nvrserver.service;

import com.sun.jna.NativeLong;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NvrControllTest {

    private NativeLong userId;
    private NvrControll nvrControll;
    @BeforeEach
    void setUp() {
        nvrControll = new NvrControll();
        nvrControll.init();
        userId = nvrControll.login();
    }

    @Test
    void testOne() {

        nvrControll.goTo(userId,1);
        nvrControll.zoomIn(userId);
        nvrControll.goTo(userId,5);

        nvrControll.goTo(userId,2);
        nvrControll.zoomIn(userId);
        nvrControll.goTo(userId,5);

        nvrControll.goTo(userId,1);

        nvrControll.close(userId);
    }

    @Test
    void testTwo() {
        nvrControll.goTo(userId,1);
        nvrControll.zoomInAlive(userId);
        nvrControll.goTo(userId,5);

        nvrControll.goTo(userId,2);
        nvrControll.zoomInAlive(userId);
        nvrControll.goTo(userId,5);

//        nvrControll.goTo(userId,1);


    }

    @AfterEach
    void tearDown() {
        nvrControll.close(userId);
    }

}