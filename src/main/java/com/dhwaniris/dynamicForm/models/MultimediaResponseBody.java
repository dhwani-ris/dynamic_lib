package com.dhwaniris.dynamicForm.models;







public class MultimediaResponseBody {

    public MultimediaRequestBody getData() {
        return data;
    }

    public void setData(MultimediaRequestBody data) {
        this.data = data;
    }


    private MultimediaRequestBody data;

    @Override
    public String toString()
    {
        return "ClassPojo [data = "+data+"]";
    }

}
