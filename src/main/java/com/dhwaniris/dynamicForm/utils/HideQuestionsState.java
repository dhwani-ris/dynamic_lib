package com.dhwaniris.dynamicForm.utils;


public class HideQuestionsState {


    int order;

    boolean isRequied;


    boolean isMatchPattren = false;


    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isRequied() {
        return isRequied;
    }

    public void setRequied(boolean requied) {
        isRequied = requied;
    }

    public boolean isMatchPattren() {
        return isMatchPattren;
    }

    public void setMatchPattren(boolean matchPattren) {
        isMatchPattren = matchPattren;
    }
}
