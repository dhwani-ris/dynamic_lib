package com.dhwaniris.dynamicForm.NetworkModule;


public class AppConfing {

    public static final int SCHEMA_VERSION = 1;

    //amresh local
    //public static final String TEST_URL_AMRESH = "http://192.168.0.102:3000/api/v1/";

    /* // production server
     public static final String BASE_URL_DEV = "https://deltaapi.dhwaniris.com/api/v1/";
     //stg
     public static final String BASE_URL_STG_NEW = "https://stgdeltamobileapi.dhwaniris.com/api/v1/";
     //demo
     public static final String BASE_URL_DEMO = "https://demodeltamobileapi.dhwaniris.in/api/v1/";
 */
    public static final String FORM = "form/";


    public static final String DELTA_PREF_NAME = "delta_app_pref";
    public static final String DELTA_LANGUAGE_NAME = "delta_app_language_pref";



    public static final String USER_TOKEN = "user_token";
    public static final String FORM_STRUCTURE_TIMESTAMP = "FORM_STRUCTURE_TIMESTAMP";
    public static final String BASIC_DETAILS_FORM_TIMESTAMP = "BASIC_DETAILS_FORM_TIMESTAMP";
    public static final String GET_FILLED_FORM_TIMESTAMP = "GET_FILLED_FORM_TIMESTAMP";
    public static final String GET_UPDATE_TASK_TIMESTAMP = "GET_UPDATE_TASK_TIMESTAMP";
    public static final String GET_UPDATE_TASK_DATA_TIMESTAMP = "GET_UPDATE_TASK_DATA_TIMESTAMP";
    public static final String GET_ERROR_DATA_TIMESTAMP = "GET_ERROR_DATA_TIMESTAMP";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_MOBILE = "USER_MOBILE";
    public static final String IS_PASS_CHANGED = "IS_PASS_CHANGED";
    public static final String IS_PROFILE_COMPLETED = "IS_PROFILE_COMPLETED";
    public static final String LOGEDIN_STATUS = "LOGEDIN_STATUS";
    public static final String LANGUAGE_SELECTION = "LANGUAGE_SELECTION";
    public static final String LANGUAGE = "LANGUAGE";
    public static final String USER_MOBILE_LANGUAGE = "User_mobile_language";
    public static final String FCM_TOKEN = "fcm_token";
    public static final String DYNAMIC_ANSWER_TIMESTAMP = "dynamicanswertimestamp";
    public static final String DEFAULT_LANGUAGE = "en";


    public static final String QUS_TEXT = "1";
    public static final String QUS_NUMBER = "2";
    public static final String QUS_DROPDOWN = "3";
    public static final String QUS_MULTI_SELECT = "4";
    public static final String QUS_RADIO_BUTTONS = "5";
    public static final String QUS_IMAGE = "7";
    public static final String QUS_LABEL = "10";
    public static final String QUS_RECORD_AUDIO = "12";
    public static final String QUS_ADDRESS = "13";
    public static final String QUS_DATE = "14";
    public static final String QUS_AADHAAR = "15";
    public static final String QUS_MULTI_SELECT_LIMITED = "16";
    public static final String QUS_MULTI_SELECT_HIDE = "17";
    public static final String QUS_DROPDOWN_HIDE = "18";
    public static final String QUS_LOOPING = "20";
    public static final String QUS_LOOPING_MILTISELECT = "21";
    public static final String QUS_CONSENT = "22";
    public static final String QUA_UNIT_CONVERSION = "23";
    public static final String QUS_GEO_TRACE = "25";
    public static final String QUS_GEO_SHAPE = "26";
    public static final String QUS_VIEW_IMAGE_QUESTION = "40";
    public static final String QUS_GET_LOCTION = "19";


    public static final String VAL_REQUIRED = "1";
    public static final String VAL_REGEX = "2";
    public static final String VAL_ALERT_REGEX = "4";
    public static final String VAL_ALERT_MSG = "4.1";
    public static final String VAL_ALERT_IF_BETWEEN = "4.2";
    public static final String VAL_NOT_ABLE_TO_FILL = "3";
    public static final String VAL_OR_CASE_WITH_MULTIPLE_PARENT = "6";
    public static final String VAL_OR_CASE_WITH_GET_FILTER_OPTION = "7";
    public static final String VAL_VISIBLE_ON_PARENTS_HAS_DIFFERENT_VALUES = "8";
    public static final String VAL_FUTURE_DATE = "21";
    public static final String VAL_FUTURE_AND_TODAY = "22";
    public static final String VAL_PAST_DATE = "23";
    public static final String VAL_PAST_AND_TODAY = "24";
    public static final String VAL_DESELECT_ALL = "31";
    public static final String VAL_CHECKLIMIT = "32";
    public static final String VAL_ADD_INFO_IMAGE = "36";
    public static final String VAL_ADD_INFO_GPS = "37";
    public static final String VAL_ADD_INFO_TIME = "38";
    public static final String VAL_DYNAMIC_ORDER_CREATION_FOR_NESTED = "40";
    public static final String VAL_ADD_DEFAULT_OPTION_DY_AO = "41";
    public static final String VAL_ADD_DEFAULT_OPTION_WHEN_DY_AO_0 = "42";
    public static final String VAL_LABEL_AS_INSTRUCTION = "51";
    public static final String VAL_LABEL_AS_HTML_TEXT = "52";
    public static final String VAL_LABEL_AS_TV_IMAGE = "53";
    public static final String VAL_DYNAMIC_ANSWER_OPTION = "55";
    public static final String VAL_DELETE_ANSWER_OPTION = "56";
    public static final String VAL_VILLAGE_WISE_LIMIT = "57";
    public static final String VAL_MASTER_DISTRICT = "58";
    public static final String VAL_MASTER_BLOCK = "59";
    public static final String VAL_MASTER_VILLAGE = "60";

    public static final String VAL_UNIT_LENGTH = "71";
    public static final String VAL_UNIT_AREA = "72";
    public static final String VAL_UNIT_TEMPERATURE = "73";
    public static final String VAL_UNIT_TIME = "74";
    public static final String VAL_UNIT_MASS = "75";
    public static final String VAL_UNIT_VOLUME = "76";
    public static final String VAL_UNIT_SPEED = "77";




    public static final String CONNECT_TO_WIFI = "WIFI";
    public static final String CONNECT_TO_MOBILE = "MOBILE";
    public static final String NOT_CONNECT = "NOT_CONNECT";
    final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String SYNC_START = "sync";


    public static final String REST_SHOULD_BE_LESS_THAN = "3";
    public static final String REST_SHOULD_BE_LESS_THAN_EQUAL = "4";
    public static final String REST_SHOULD_BE_GRATER_THAN = "6";
    public static final String REST_SHOULD_BE_GRATER_THAN_EQUAL = "7";
    public static final String REST_CALL_FOR_EXPRESSION = "8";
    public static final String REST_CALL_FOR_ADD = "9.1";
    public static final String REST_CALL_FOR_SUB = "9.2";
    public static final String REST_CALL_FOR_MUL = "9.3";
    public static final String REST_CALL_FOR_DIVD = "9.4";
    public static final String REST_GET_ANS_SUM_LOOPING = "10";
    public static final String REST_DID_RELATION = "11";
    public static final String REST_VALUE_AS_TITLE_OF_CHILD = "12";
    public static final String REST_MULTI_ANS_VISIBILITY_IF_NO_ONE_SELECTED = "13";
    public static final String REST_CLEAR_CHILD_ANS_DID_OPTIONS = "14";
    public static final String REST_GET_ANS_OPTION = "15";
    public static final String REST_GET_ANS_OPTION_FILTER = "15.1";
    public static final String REST_GET_ANS_OPTION_LOOPING = "16";
    public static final String REST_GET_ANS_OPTION_LOOPING_FILTER = "16.1";
    public static final String REST_CLEAR_DID_CHILD = "17";


    public static final String BLANK_TITLE = "___";


    //child parent
    public static final String CP_isFILLED = "1";
    public static final String CP_isSELECTED = "3";
    public static final String USER_LOGOUT = "user_logout";
    public static final String APP_VERSION = "app_version";
    public static final String SYNC_RUNNING = "SYNC_RUNNING";
    public static final String LATEST_VERSION_STATUS = "latest_version_status";
    public static final String LATEST_VERSION = "latest_version";


    // Error </code>
    public static final String END_POINT = "MOBILE";
    public static final String FILE_NOT_FOUND_EXCEPTION_ERROR = "AND0001";
    public static final String IO_EXCEPTION_ERROR = "AND0002";
    public static final String OUT_OF_MEMORY_EXCEPTION_ERROR = "AND0003";
    public static final String NAME_NOT_FOUND_EXCEPTION_ERROR = "AND0004";
    public static final String NULL_POINTER_EXCEPTION_ERROR = "AND0005";
    public static final String UNEXPECTED_ERROR = "AND0007";
    public static final String SAVE_TIME_ERROR = "AND0009";
    public static final String DIVISSION_BY_ZERO = "AND00010";
    public static final String BAD_REQUEST = "500";

    public static final int SUBMITTED = 1;
    public static final int NEW = 0;
    public static final int DRAFT = 2;
    public static final int NEW_FORM = 3;
    public static final int SYNCED = 4;
    public static final int SYNCED_BUT_EDITABLE = 5;
    public static final int EDITABLE_DARFT = 6;
    public static final int EDITABLE_SUBMITTED = 7;
    public static final int FAILED = 8;
    public static final int REJECTED_DUPLICATE = 9;
    public static final int REJECTED_DY_NOT_FOUND = 10;
    public static final String FETCH_FORM_FIRST_TIME = "fetch_form_first_time";
    public static final String IS_REQUEST_FOR_SYNC = "is_forms_for_synced";
    public static final String DRAFT_SAVED = "draft_saved";
    public static final String TEMP_QUESTION = "tempQuestion";

    public static final String FEC_IMAGE = "image";
    public static final String LOCATION = "location";
    public static final String IMAGE = "image";

    public static final String LOCALFOLDERPATH = "/storage/emulated/0/TataTrust_Delta/";
    public static final String ARG_SECTION_NUMBER = "section_number";


    public static final int TYPE_ONE_PROJECT_NAME = 1;
    public static final int TYPE_TWO_FORM_DETAILS = 2;

}
