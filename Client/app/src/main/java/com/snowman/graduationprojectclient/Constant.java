package com.snowman.graduationprojectclient;

public class Constant {

    /**
     * 接口相关常量
     */
    public interface Remote {
        String WEB_SOCKET_PAST_TIME_URL = "ws://120.76.58.156:8080/SerachDataWebSocket/";
        String WEB_SOCKET_SERVER_URL = "ws://120.76.58.156:8080/clientwebSocket";
        String SERVER_BASE_URL = "http://120.76.58.156:8080";
        String LONG_CONNECTION_URL = "";
    }

    /**
     * 编码相关常量
     */
    public interface Code {
        int BUFFER_LENGTH = 2048;
        int WIDTH = 1280;
        int HEIGHT = 720;
        int FRAME_RATE = 30;
        int TIMEOUT_USEC = 12000;
        String MIME_TYPE = "video/avc";
    }
}
