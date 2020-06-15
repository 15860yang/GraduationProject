package com.snowman.wifi;

public interface Constant {
    int SERVER_PORT = 22222;

    /**
     * 接口相关常量
     */
    interface Remote {
        String WEB_SOCKET_SERVER_URL = "ws://120.76.58.156:8080/gatewebSocket/";
        String SERVER_BASE_URL = "http://120.76.58.156:8080";
    }

    /**
     * 编码相关常量
     */
    interface Code {
        int BUFFER_LENGTH = 2048;
        int WIDTH = 1280;
        int HEIGHT = 720;
        int FRAME_RATE = 30;
        int TIMEOUT_USEC = 12000;
        String MIME_TYPE = "video/avc";
    }

    int SLICE_LENGTH = 2048;
}
