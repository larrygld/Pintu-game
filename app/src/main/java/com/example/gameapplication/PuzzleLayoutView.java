package com.example.gameapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class PuzzleLayoutView extends RelativeLayout implements View.OnClickListener {

    //表示将其切成2*2拼图（默认4块）
    private int mColumn = 2;
    //容器的内边距
    private int mPadding;
    //每个块块的边距(横,纵  3:表示间距为3dp)
    private int mMargin = 3;
    //存储ImageView
    private ImageView[] mGamePintuItems;
    //Item的宽度(一致)
    private int mItemWidth;
    //游戏的图片
    private Bitmap mBitmap;
    //切图后的存储
    private List<ImagePieceBean> mItemBitmaps;
    //操作次数
    private boolean once;
    //容器宽度(游戏面板 高宽一致)
    private int mWidth;
    //设置游戏是否成功
    private boolean isGameSuccess;
    //设置游戏是否失败
    private boolean isGameOver;

    private int photoNum = 1;

//    private ImageView imageView;


    //获取图片对象
    public int getLevel(){
        return this.level;
    }


    public GamePintuListner mListner;

    public PuzzleLayoutView(Context context) {
        this(context, null);
    }

    public PuzzleLayoutView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PuzzleLayoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3,
                getResources().getDisplayMetrics());//将dp转化为px,或xp转化为px
        mPadding = min(getPaddingLeft(), getPaddingRight(), getPaddingTop(), getPaddingBottom());
    }

    //接口方法
    public interface GamePintuListner {

        void nextLevel(int nextLevel);//下一关

        void timechanged(int currentTime);//关卡时间

        void gameover();//游戏结束
    }

    public void setOnGamePintuListner(GamePintuListner mListner) {
        this.mListner = mListner;
    }

    private int level = 1;
    private static final int TIME_CHANGED = 0X123;
    private static final int NEXT_LEVEL = 0X124;


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case TIME_CHANGED:
                    if (isGameSuccess || isGameOver)
                        return;
                    if (mListner != null) {
                        mListner.timechanged(mTime);
                        //时间结束后,游戏结束
                        if (mTime == 0) {
                            isGameOver = true;
                            mListner.gameover();
                        }
                    }
                    mTime--;
                    //延迟1秒发送
                    handler.sendEmptyMessageDelayed(TIME_CHANGED, 1000);
                    break;
                case NEXT_LEVEL:
                    level = level + 1;//切换到下一关
                    if (mListner != null) {
                        mListner.nextLevel(level);
                    } else {
                        nextLevel();
                    }
                default:
                    break;
            }
        }
    };


    private boolean isTimeEnabled = false;
    private int mTime;

    /**
     * 设置是否启动时间  (默认不启动)
     *
     * @param isTimeEnabled
     */
    public void setTimeEnabled(boolean isTimeEnabled) {
        this.isTimeEnabled = isTimeEnabled;
    }

    /**
     * 获取当前布局的大小(正方形)
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //取宽和高中的最小值
        mWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());
        if (!once) {
            //调用进行切图,以及排序(方法)
            initBitmap();
            //调用设置ImageView(Item)的宽高等属性(方法)
            initItem();
            //判断是否开启时间(方法调用)
            checkTimeEnable();
            once = true;
        }
        setMeasuredDimension(mWidth, mWidth);//强制调用使面板为正方形
    }

    /**
     * 判断是否开启时间
     */
    private void checkTimeEnable() {
        if (isTimeEnabled) {
            //根据当前等级设置时间
            countTimeBaseLevel();
            //通知线程更新关卡时间
            handler.sendEmptyMessage(TIME_CHANGED);
        }

    }

    private void countTimeBaseLevel() {
        mTime = (int) Math.pow(2, level) * 60;//第一关120秒 第二关:240 第三关:480
    }

    /**
     * 进行切图,以及排序方法
     */
    private void initBitmap() {
        //将图片引入，各个关卡的图片的图片会进行切换
        if (level == 1) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo1);
        }else if (level == 2) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo2);
        }else if (level == 3) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo3);
        }else if (level == 4) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo4);
        }else if (level == 5) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo5);
        }


       //注意此处的导包
        mItemBitmaps = ImageSplitterUtil.sqlitImage(mBitmap, mColumn);//返回长度为4 (2*2)
        //使用sort进行乱排序
        Collections.sort(mItemBitmaps, new Comparator<ImagePieceBean>() {
            public int compare(ImagePieceBean a, ImagePieceBean b) {//注意此处的a,b
                //是否大于0.5具有不确定性
                return Math.random() > 0.5 ? 1 : -1;
            }
        });
    }

    /**
     * 设置ImageView(Item)的宽高等属性方法
     */
    private void initItem() {
        //容器的宽度-Item内边距 =所有小块块加起来的/Item个数(宽度)  2:左边和右边边距
        mItemWidth = (mWidth - mPadding * 2 - mMargin * (mColumn - 1)) / mColumn;
        mGamePintuItems = new ImageView[mColumn * mColumn];//界面块块个数相*
        //生成我们的Item,设置Rule(Item间的关系,高矮等)
        for (int i = 0; i < mGamePintuItems.length; i++) {
            ImageView item = new ImageView(getContext());
            /**
             * item点击事件
             */
            item.setOnClickListener(this);
            item.setImageBitmap(mItemBitmaps.get(i).getBitmap());//此前以进行过乱排序
            mGamePintuItems[i] = item;//保存Item
            item.setId(i + 1);
            //在Item的tag中存储了index
            item.setTag(i + "_" + mItemBitmaps.get(i).getIndex());
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mItemWidth, mItemWidth);
            //[设置游戏规则]
            //设置Item间横向间隙,通过rightMargin
            //不是最后一列
            if ((i + 1) % mColumn != 0) {
                lp.rightMargin = mMargin;
            }
            //不是第一列
            if (i % mColumn != 0) {
                lp.addRule(RelativeLayout.RIGHT_OF, mGamePintuItems[i - 1].getId());
            }
            //如果不是第一行,设置topMargin和rule
            if (i + 1 > mColumn) {
                lp.topMargin = mMargin;
                lp.addRule(RelativeLayout.BELOW, mGamePintuItems[i - mColumn].getId());
            }
            addView(item, lp);//添加到RelativeLayout中
        }
    }

    /**
     * 当过关失败,时间停止时调用此方法(重新开始此关卡)
     */
    public void restart() {
        isGameOver = false;//重置当前关卡
        mColumn--;
        nextLevel();
    }

    public void nextLevel() {
        this.removeAllViews();//移除当前所有View
        mAnimLayout = null;
        mColumn++;//由2*2 变为3*3游戏面版
        isGameSuccess = false;//游戏未成功(新的开始)
        checkTimeEnable();//下一关时间重新计算
        initBitmap();
        initItem();
    }

    /**
     * 获取多个参数的最小值
     */
    private int min(int... params) {//...传多个参数
        int min = params[0];//获取最小的
        for (int param : params) {//发现最小的则赋值
            if (param < min) {
                min = param;
            }
        }
        return min;
    }

    /**
     * 点击事件
     */
    private ImageView mFirst;//点击的IItem
    private ImageView mSecond;

    public void onClick(View v) {
        if (isAniming)
            return;
        //两次点击同一个Item
        if (mFirst == v) {
            mFirst.setColorFilter(null);
            mFirst = null;
            return;
        }
        if (mFirst == null) {
            mFirst = (ImageView) v;
            mFirst.setColorFilter(Color.parseColor("#5551c4d4"));//设置选中Item时的颜色(55为半透明)
        } else {
            mSecond = (ImageView) v;
            //交换我们的Item
            exchangeView();
        }
    }

    /**
     * 动画层
     */
    private RelativeLayout mAnimLayout;
    //设置图片进行切换时用户疯狂点击
    private boolean isAniming;

    /**
     * 交换我们的Item
     */
    private void exchangeView() {
        mFirst.setColorFilter(null);//去除颜色状态(高亮)
        //调用构造我们的动画层方法
        setUpAnimLayout();
        //进行图片的交换
        ImageView first = new ImageView(getContext());
        final Bitmap firstBitmap = mItemBitmaps.get(getImageIdByTag((String) mFirst.getTag())).getBitmap();
        first.setImageBitmap(firstBitmap);
        LayoutParams lp = new LayoutParams(mItemWidth, mItemWidth);
        lp.leftMargin = mFirst.getLeft() - mPadding;
        lp.topMargin = mFirst.getTop() - mPadding;
        first.setLayoutParams(lp);
        mAnimLayout.addView(first);//添加至动画层
        ImageView second = new ImageView(getContext());
        final Bitmap secondBitmap = mItemBitmaps.get(getImageIdByTag((String) mSecond.getTag())).getBitmap();
        second.setImageBitmap(secondBitmap);
        LayoutParams lp2 = new LayoutParams(mItemWidth, mItemWidth);
        lp2.leftMargin = mSecond.getLeft() - mPadding;
        lp2.topMargin = mSecond.getTop() - mPadding;
        second.setLayoutParams(lp2);
        mAnimLayout.addView(second);//添加至动画层

        //设置动画
        TranslateAnimation animFirst = new TranslateAnimation(0, mSecond.getLeft() - mFirst.getLeft(),
                0, mSecond.getTop() - mFirst.getTop());
        animFirst.setDuration(500);//设置动画时间
        animFirst.setFillAfter(true);//设置动画结束的位置
        first.startAnimation(animFirst);//启动动画

        TranslateAnimation animSecond = new TranslateAnimation(0, -mSecond.getLeft() + mFirst.getLeft(),
                0, -mSecond.getTop() + mFirst.getTop());
        animSecond.setDuration(500);//设置动画时间
        animSecond.setFillAfter(true);//设置动画结束的位置
        second.startAnimation(animSecond);//启动动画

        /**
         * 监听动画事件
         */
        animFirst.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                mFirst.setVisibility(View.INVISIBLE);//隐藏动画
                mSecond.setVisibility(View.INVISIBLE);
                isAniming = true;
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {

                String firstTag = (String) mFirst.getTag();
                String secondTag = (String) mSecond.getTag();
                mFirst.setImageBitmap(secondBitmap);
                mSecond.setImageBitmap(firstBitmap);
                mFirst.setTag(secondTag);
                mSecond.setTag(firstTag);
                mFirst.setVisibility(View.VISIBLE);//显示隐藏的图片
                mSecond.setVisibility(View.VISIBLE);
                //此处为空,并不是将对象设置为null 而是将mFirst与Bitmap对象链接的线断开
                mFirst = mSecond = null;//回到初始状态
                mAnimLayout.removeAllViews();//移除动画层的两个View
                //调用判断游戏成功时的方法
                checkSuccess();
                isAniming = false;
            }
        });
    }

    /**
     * 判断游戏是否成功
     */
    private void checkSuccess() {

        boolean isSuccess = true;
        for (int i = 0; i < mGamePintuItems.length; i++) {
            ImageView imageView = mGamePintuItems[i];
            //getImageIndex:上面的方法名(注意此处方法名)
            if (getImageIndex((String) imageView.getTag()) != i) {
                isSuccess = false;
            }
        }
        if (isSuccess) {
            isGameSuccess = true;
            handler.removeMessages(TIME_CHANGED);//进入下一关时的时间
            Log.e("TAG", "SUCCESS");
            Toast.makeText(getContext(), "Success,level up 游戏升级!!!", Toast.LENGTH_LONG).show();
            handler.sendEmptyMessage(NEXT_LEVEL);
        }
    }

    /**
     * 根据tag获取Id
     */
    public int getImageIdByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[0]);//拿ID
    }

    public int getImageIndex(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[1]);//拿ID
    }

    /**
     * 构造我们的动画层
     */
    private void setUpAnimLayout() {
        if (mAnimLayout == null) {
            mAnimLayout = new RelativeLayout(getContext());
            addView(mAnimLayout);//添加到游戏面板中
        }
    }
}
