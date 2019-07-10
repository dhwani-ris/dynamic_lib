package com.dhwaniris.dynamicForm.models;



import java.util.List;




public class MultimediaRequestBody {



    private List<FilesToUpload> filesList;

    public List<FilesToUpload> getFilesList() {
        return filesList;
    }

    public void setFilesList(List<FilesToUpload> filesList) {
        this.filesList = filesList;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [images = "+filesList+"]";
    }
}
