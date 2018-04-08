package com.moviebook.bean;

public class DataBean {
    /**
     * verify : C6149F66CEF5598C572FE2BA08EA4BF1
     * data : {"time":"6s","y":0.5189873417721519,"lab":"德国超黑啤酒","height":140,"id":0,"type":2,"width":260,"data":"iVBORw0KGgoAAAANSUhEUgAAAPAAAAASUVORK5CYII=","x":0.4942528735632184}
     */

    public String verify;
    public String data;

    public class ImageData {

        public String id;
        public String lab;
        public String type;
        public String data;
        public String x;
        public String y;
        public String width;
        public String height;
        public String time;

        @Override
        public String toString() {
            return "ImageData{" +
                    "id='" + id + '\'' +
                    ", lab='" + lab + '\'' +
                    ", type='" + type + '\'' +
                    ", data='" + data + '\'' +
                    ", x='" + x + '\'' +
                    ", y='" + y + '\'' +
                    ", width='" + width + '\'' +
                    ", height='" + height + '\'' +
                    ", time='" + time + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DataBean{" +
                "verify='" + verify + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

}
