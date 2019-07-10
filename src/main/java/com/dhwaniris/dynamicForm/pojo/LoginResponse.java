package com.dhwaniris.dynamicForm.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class LoginResponse {


    @SerializedName("timestamp")
    private int timestamp;
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("token")
    private String token;
    @SerializedName("data")
    private DataBean data;

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        @SerializedName("_id")
        private String _id;
        @SerializedName("name")
        private String name;
        @SerializedName("mobile")
        private String mobile;
        @SerializedName("userLevel")
        private String userLevel;
        @SerializedName("modifiedAt")
        private String modifiedAt;
        @SerializedName("email")
        private String email;
        @SerializedName("isPasswordChangedOnce")
        private boolean isPasswordChangedOnce;
        @SerializedName("alt_mobile")
        private String alt_mobile;
        @SerializedName("languages")
        private List<LanguagesBean> languages;


        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getUserLevel() {
            return userLevel;
        }

        public void setUserLevel(String userLevel) {
            this.userLevel = userLevel;
        }

        public String getModifiedAt() {
            return modifiedAt;
        }

        public void setModifiedAt(String modifiedAt) {
            this.modifiedAt = modifiedAt;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public boolean isPasswordChangedOnce() {
            return isPasswordChangedOnce;
        }

        public void setPasswordChangedOnce(boolean passwordChangedOnce) {
            isPasswordChangedOnce = passwordChangedOnce;
        }

        public String getAlt_mobile() {
            return alt_mobile;
        }

        public void setAlt_mobile(String alt_mobile) {
            this.alt_mobile = alt_mobile;
        }

        public List<LanguagesBean> getLanguages() {
            return languages;
        }

        public void setLanguages(List<LanguagesBean> languages) {
            this.languages = languages;
        }


        public static class LanguagesBean {
            /**
             * code : en
             * name : English
             * _id : 59c20e1c4b243f354086d973
             */


            @SerializedName("code")
            private String code;
            @SerializedName("name")
            private String name;
            @SerializedName("_id")
            private String _id;

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String get_id() {
                return _id;
            }

            public void set_id(String _id) {
                this._id = _id;
            }
        }

    }
}
