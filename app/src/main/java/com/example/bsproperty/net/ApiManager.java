package com.example.bsproperty.net;

/**
 * Created by yezi on 2018/1/27.
 */

public class ApiManager {

    private static final String HTTP = "http://";
    private static final String IP = "192.168.55.103";
    private static final String PROT = ":8080";
    private static final String HOST = HTTP + IP + PROT;
    private static final String API = "/api";
    private static final String USER = "/user";
    private static final String SONG = "/song";
    private static final String LIKE = "/like";

    public static final String HEAD_PATH = HOST + API + USER;
    public static final String VOICE_PATH = HOST + API + SONG;

    public static final String REGISTER = HOST + API + USER + "/register";
    public static final String LOGIN = HOST + API + USER + "/login";
    public static final String MY_SONG = HOST + API + USER + "/";

    public static final String SONG_LIST = HOST + API + SONG + "/list";
    public static final String SONG_ADD = HOST + API + SONG + "/add";
    public static final String SONG_RANK = HOST + API + SONG + "/rank";
    public static final String SONG_SEARCH = HOST + API + SONG + "/search";


    public static final String LIKE_ADD = HOST + API + LIKE + "/add";
    public static final String LIKE_FIND = HOST + API + LIKE + "/find";
    public static final String LIKE_DEL = HOST + API + LIKE + "/del";
    public static final String LIKE_LIST = HOST + API + LIKE + "/list";

}
