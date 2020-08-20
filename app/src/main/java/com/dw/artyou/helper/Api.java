package com.dw.artyou.helper;

public class Api {

    public static String BASE_URL = "https://webapi-staging.artyou.global/";

    public static String SOCIAL_LOGIN = BASE_URL + "users/social";

    public static String REGISTER = BASE_URL + "auth/register";
    public static String LOGIN = BASE_URL + "auth/login";
    public static String GET_STORYES = BASE_URL + "storyes/public";
    public static String GET_OBJECT_TYPE = BASE_URL + "objects/medias";
    public static String CREATE_OBJECT = BASE_URL + "objects";
    public static String CREATE_STORY = BASE_URL + "storyes";
    public static String GET_OBJECTS = BASE_URL + "objects";
    public static String UPLOAD_PROFILE = BASE_URL + "users/avatar";
    public static String ADD_NUMBER = BASE_URL + "users/telephones";
    public static String CHANGE_PASSWORD = BASE_URL + "auth/confirm_password";
    public static String FORGOT_PASSWORD = BASE_URL + "auth/request_password";
    public static String REQUEST_CODE = BASE_URL + "auth/phones/request";
    public static String CHANGE_DETAILS = BASE_URL + "users";
    public static String CHANGE_CURRENCY = BASE_URL + "users/currency";
    public static String VERIFY_CODE = BASE_URL + "auth/phones/confirm";

    public static String REPORT_STORY = BASE_URL + "storyes/report";
    public static String DELETE_STORY = BASE_URL + "storyes/";
    public static String UPLOAD_FILE = BASE_URL + "utils/file-upload";

    public static String getImage(String name) {
        return "https://staging-media-cdn.artyou.global/" + name;
    }

    public static String addExperience(String objectId) {
        return BASE_URL + "experiences/" + objectId;
    }

    public static String getStoryDetail(String id) {
        return BASE_URL + "storyes/public/" + id;
    }

    public static String deletePhone(String id) {
        return BASE_URL + "users/telephones/" + id;
    }

}
