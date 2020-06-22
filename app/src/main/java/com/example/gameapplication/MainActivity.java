package com.example.gameapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.logging.Level;

public class MainActivity extends AppCompatActivity {

    private PuzzleLayoutView puzzleLayoutView;
    private TextView mLevel, mTime;
    private ImageView imageView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        final Intent intent = new Intent(MainActivity.this, MusicService.class);

        imageView = findViewById(R.id.controlMusic);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MusicService.isplay == false){
                    startService(intent);
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.start));
                }else{
                    stopService(intent);
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.stop));
                }
            }
        });



        mLevel = this.findViewById(R.id.id_level);
        mTime = this.findViewById(R.id.id_time);
        puzzleLayoutView = this.findViewById(R.id.puzzle_layout_view);
        puzzleLayoutView.setTimeEnabled(true);
        //监听事件
        puzzleLayoutView.setOnGamePintuListner(new PuzzleLayoutView.GamePintuListner() {
            public void timechanged(int currentTime) {
                //此处为int 注意加""
                mTime.setText(currentTime + "秒");
            }

            public void nextLevel(final int nextLevel) {
                //弹出提示框
                new AlertDialog.Builder(MainActivity.this).setTitle("游戏信息")
                        .setMessage("游戏升级").setPositiveButton("进入下一关",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //游戏结束后,调用下一关
                                changePhoto();
                                puzzleLayoutView.nextLevel();
                                mLevel.setText("第" + +nextLevel + "关");
                            }
                        }).show();
            }

            public void gameover() {
                //弹出提示框
                new AlertDialog.Builder(MainActivity.this).setTitle("游戏信息")
                        .setMessage("游戏结束!").setPositiveButton("是否继续该关卡?",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                puzzleLayoutView.restart();//重新启动
                            }
                        }).setNegativeButton("是否放弃该游戏!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
            }
        });
    }

    @Override
    protected void onStart() {
        startService(new Intent(MainActivity.this, MusicService.class));
        super.onStart();
    }

    @Override
    protected void onStop() {
        stopService(new Intent(MainActivity.this, MusicService.class));
        super.onStop();
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(MainActivity.this).setTitle("是否退出游戏？")
                .setPositiveButton("否",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                puzzleLayoutView.restart();//重新启动
                                dialog.dismiss();
                            }
                        }).setNegativeButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).show();
//        super.onBackPressed();

    }

    public void changePhoto() {

        ImageView imageView;
        PuzzleLayoutView puzzleLayoutView;
        imageView = findViewById(R.id.gamephoto);


        puzzleLayoutView = findViewById(R.id.puzzle_layout_view);
        int level = puzzleLayoutView.getLevel();
        switch (level){
            case 1:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.photo1));
                break;
            case 2:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.photo2));
                break;
            case 3:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.photo3));
                break;
            case 4:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.photo4));
                break;
            case 5:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.photo5));
                break;

        }

    }

    public void exit_Game(View view) {
        new AlertDialog.Builder(MainActivity.this).setTitle("是否退出游戏？")
                .setPositiveButton("否",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        puzzleLayoutView.restart();//重新启动
                        dialog.dismiss();
                    }
                }).setNegativeButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).show();

    }


}
