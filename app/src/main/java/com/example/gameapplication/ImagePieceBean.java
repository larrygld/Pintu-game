package com.example.gameapplication;

import android.graphics.Bitmap;

public class ImagePieceBean {

    private int index;  //表示当前第几块
    private Bitmap bitmap;  //当前图片

    public ImagePieceBean() {
    }

    //快捷键构造方法 Source 倒3
    public ImagePieceBean(int index, Bitmap bitmap) {
        this.index = index;
        this.bitmap = bitmap;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String toString() {
        return "ImagePiece [index=" + index + ", bitmap=" + bitmap + "]";
    }
}