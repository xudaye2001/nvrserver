package com.sbd.nvrserver.nvrserver.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class NvrSearchServiceTest {
    @Autowired
    NvrSearchService nvrSearchService;
    @Autowired
    NvrControll nvrControll;

    @Test
    public void testMain() {
        nvrControll = new NvrControll();
        nvrSearchService = new NvrSearchService();

        nvrSearchService.initNvr();
        nvrSearchService.startSearchQr();
    }

}