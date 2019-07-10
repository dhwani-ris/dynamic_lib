package com.dhwaniris.dynamicForm.db.dbhelper;

import android.os.Parcel;
import android.os.Parcelable;




public class ResourceUrlsBean  implements Parcelable {
    /**
     * label : image
     * url :
     */


    private String label;

    private String url;

    protected ResourceUrlsBean(Parcel in) {
        label = in.readString();
        url = in.readString();
    }

    public ResourceUrlsBean(){}
    public static final Creator<ResourceUrlsBean> CREATOR = new Creator<ResourceUrlsBean>() {
        @Override
        public ResourceUrlsBean createFromParcel(Parcel in) {
            return new ResourceUrlsBean(in);
        }

        @Override
        public ResourceUrlsBean[] newArray(int size) {
            return new ResourceUrlsBean[size];
        }
    };

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label);
        dest.writeString(url);
    }
}