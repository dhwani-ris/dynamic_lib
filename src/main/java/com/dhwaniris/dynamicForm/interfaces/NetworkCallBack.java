package com.dhwaniris.dynamicForm.interfaces;


public interface NetworkCallBack {
    void onSuccess(Object result);
    void onFailure(String message, int code);
}
