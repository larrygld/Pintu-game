package com.example.gameapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

public class ImageSplitterUtil {

    /**
     * 传入bitmap,切成piece*piece块
     */
    public static List<ImagePieceBean> sqlitImage(Bitmap bitmap, int piece) {
        List<ImagePieceBean> ImagePieceBeans = new ArrayList<>();
        int width = bitmap.getWidth();//拿到图片宽高
        int height = bitmap.getHeight();
        int pieceWidth = Math.min(width, height) / piece;//得到每一块的宽度

        for (int i = 0; i < piece; i++) {//切第一行
            for (int j = 0; j < piece; j++) {//循环切第二,三行
                ImagePieceBean ImagePieceBean = new ImagePieceBean();
                ImagePieceBean.setIndex(j + i * piece);//第一次i为0,第0行 j++递增 0-6
                int x = j * pieceWidth;//第一次循环X,Y为0
                int y = i * pieceWidth;
                ImagePieceBean.setBitmap(Bitmap.createBitmap(bitmap, x, y, pieceWidth, pieceWidth));
                ImagePieceBeans.add(ImagePieceBean);
            }
        }
        return ImagePieceBeans;
    }
}