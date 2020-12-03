package com.kaygb.fly_game.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kaygb.fly_game.R;
import com.kaygb.fly_game.activity.GameOverActivity;
import com.kaygb.fly_game.activity.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GameView extends View  implements SensorEventListener {


    // 传感器
    public SensorManager mSensorManager;
    public Sensor sensor;

    //Game 状态
    public static final int STATUS_RUN = 1; //运行中
    public static final int STATUS_PAUSE = 2; //暂停
    public static final int STATUS_OVER = 3; //结束
    public int status = STATUS_OVER; //默认为结束状态


    //bitmap KEY R.ID
    public HashMap<String, Bitmap> drawableBitmap = new HashMap<String, Bitmap>();

    //精灵
    public List<Sprite> sprites = new ArrayList<Sprite>();

    //字体大小
    public int font_score_size = 12;
    public int font_draw_size = 20;
    public int border_size = 2;

    //绘制继续矩形画布
    public Rect continueRect = new Rect();

    //帧数
    public int frame = 0;

    //得分
    public int scor = 0;
    public int cancasH;
    public int cancasW;

    //画笔
    public Paint paint = null;
    public Paint fontPaint = null;

    //屏幕密度
    public int density = (int) getResources().getDisplayMetrics().density;

    //主要战机
    public PlaneSprite planeSprite = null;

    public void init(Context context) {
        //初始化属性
        //画笔
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        //字体画笔
        fontPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
        fontPaint.setColor(0xff000000);
        font_score_size = (int) fontPaint.getTextSize();
        font_score_size *= density;
        font_draw_size *= density;
        fontPaint.setTextSize(font_score_size);
        border_size *= density;
        // 获取传感器管理对象
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    public GameView(Context context) {
        super(context);
        init(context);

    }

    //必须提供否则会无法启动
    public GameView(Context context, AttributeSet addrs) {
        super(context, addrs);
        init(context);
    }

    public void run(HashMap<String, Integer> bitmapIds) {
        //状态 为 运行中
        status = STATUS_RUN;
        //生成 Bitmap hasMap
        Iterator iterator = bitmapIds.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            drawableBitmap.put(entry.getKey().toString(), BitmapFactory.decodeResource(getResources(), (int) entry.getValue()));
        }
        //生成战机对象
        planeSprite = new PlaneSprite(drawableBitmap.get("plane"), density, paint);

        // 注册监听
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        //绘制UI
        postInvalidate();

    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (STATUS_RUN == status) {
            drawGameRun(canvas);
        } else if (STATUS_PAUSE == status) {
            drawGamePause(canvas);
        } else if (STATUS_OVER == status) {
            drawGameOver(canvas);
        }
    }

    public void drawGameRun(Canvas canvas)
    {
        //将战机移动到屏幕下方中间 最开始时
        if (0 == frame) {
            planeSprite.moveTo((canvas.getWidth() - planeSprite.width) / 2, (canvas.getHeight() - planeSprite.height / 2 - 100));
        }
        cancasW = canvas.getWidth();
        cancasH = canvas.getHeight();
        frame++;
        //添加精灵 每隔 30 帧
        if(0 != frame % 30)
        {
            addSprites(canvas);
        }
        drawSprites(canvas);
        destroySprites();
        planeSprite.draw(canvas);
        //重绘UI1
        postInvalidate();
//        if(score ==10){
//////            sprite.draw(canvas);
////            Paint paint1 = new Paint();
////            paint.setColor(Color.BLUE);
////            paint.setTextSize(100);
////            canvas.drawText("游戏结束 ", 50, 100, paint1);
////        }
    }

    public void addSprites(Canvas canvas)
    {
        Sprite sprite = null;
        if(0 == frame % 5 &&score !=10)
        {
            int rand = (int)(Math.random() * 100);
            //发放子弹
            sprite = new BulletSprite(drawableBitmap.get("blue_bullet"), density, paint);
            //移动到战机中间
            sprite.moveTo(planeSprite.x + planeSprite.width / 2, planeSprite.y);
            sprites.add(sprite);
            //放大 boss
            if(2 > rand)
            {
                sprite = new BossSprite(drawableBitmap.get("d1"), density, paint);
                //位置于顶部随机
                sprite.moveTo((int)(Math.random() * (canvas.getWidth() - sprite.width)), 0);
                sprites.add(sprite);
            }
            //放小兵
            if(10 > rand)
            {
                sprite = new SmallSprite(drawableBitmap.get("d2"), density, paint);
                //位置于顶部随机
                sprite.moveTo((int)(Math.random() * (canvas.getWidth() - sprite.width)), 0);
                sprites.add(sprite);
            }
        }
    }
public int score = 0;
    public void drawSprites(Canvas canvas)
    {
        List<Sprite> newSprite = new ArrayList<Sprite>();
        Iterator<Sprite> iterator = sprites.iterator();
        Iterator<Sprite> bulletIterator = null;
        //双重循环检测是否打中
        while(iterator.hasNext())
        {
            Sprite sprite = iterator.next();
            //检测不是爆炸精灵的碰撞
            if(! (sprite instanceof ExplosionSprite))
            {
                //所有炮弹精灵的 iterator
                bulletIterator = sprites.iterator();
                while (bulletIterator.hasNext())
                {
                    Sprite bulletSprite = bulletIterator.next();
                    if (bulletSprite instanceof BulletSprite)
                    {
                        //当2个 iterator 的对象不同时检测
                        if (sprite != bulletSprite)
                        {
                            //比较两者的矩形区域是否重叠
                            if (null != bulletSprite.bitmap && null != sprite.bitmap && Rect.intersects(sprite.getDescRect(), bulletSprite.getDescRect()))
                            {
                                //生命值 减1 当为0 时销毁对象
                                sprite.life--;
                                if (0 >= sprite.life)
                                {
                                    //销毁物体
                                    sprite.bitmap = null;
                                    //创建爆炸效果图
                                    Sprite expSprite = new ExplosionSprite(drawableBitmap.get("explosion"), density, paint);
                                    //调整位置
                                    expSprite.moveTo(sprite.x, sprite.y);
                                    newSprite.add(expSprite);
                                    score++;
                                    if(score == 10){
                                        // 达到10分时邮箱状态为停止
                                        status = STATUS_OVER;
                                    }



                                }

                                //销毁炮弹
                                bulletSprite.bitmap = null;

                            }
                        }
                    }
                }
            }

            // 实时更新分数
            sprite.draw(canvas);
            Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setTextSize(100);
            canvas.drawText("score: "+score, 50, 100, paint);
        }
        //添加爆炸效果图 到 sprites 下次就会绘制出来
        sprites.addAll(newSprite);
    }

    public void destroySprites()
    {
        Iterator<Sprite> iterator = sprites.iterator();
        while(iterator.hasNext())
        {
            Sprite entry = iterator.next();
            //当 bitmap 为 null 说明需要清理
            if(null == entry.bitmap)
            {
                iterator.remove();
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        //事件合成 滑动 单击 暂时不做
        //移动玩家战机
        int x = (int)event.getX() - planeSprite.width / 2;
        int y = (int)event.getY() - planeSprite.height / 2;
        Log.e("ZB","Y: "+(int) y+"  X: "+(int) x );
        planeSprite.moveTo(x, y);
        return true;
    }
    public void drawGamePause(Canvas canvas)
    {

    }

    public void drawGameOver(Canvas canvas)
    {
            Intent intent = new Intent(getContext(),GameOverActivity.class);
            getContext().startActivity(intent);

    }

    public void destroy()
    {

    }

    public void pause()
    {

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        float max = sensor.getMaximumRange();//获取最大值范围
        if (values.length >= 3) {
            float x = values[0];
            float y = values[1];
            float z = values[2];
//            Log.e("CANW",cancasW+"");
//            Log.e("CANH",planeSprite.y+"");
//            Log.e("XYZ","Y: "+(int) y+"  X: "+(int) x + "  Z: "+ (int) z);
//            Log.e("PS",planeSprite.width+"");
            if (x>2 && planeSprite.x>= 0) { //获取传感器值并移动相应的距离
                planeSprite.moveTo(planeSprite.x - (int)x,planeSprite.y);
            }
            if (x< -2 && planeSprite.x <= cancasW - planeSprite.width) {
                planeSprite.moveTo(planeSprite.x - (int)x,planeSprite.y);
            }
            if (y>2 && planeSprite.y <= cancasH - planeSprite.height) {
                planeSprite.moveTo(planeSprite.x,planeSprite.y + (int)y);
            }
            if (y< -2 && planeSprite.y >= 0) {
                planeSprite.moveTo(planeSprite.x,planeSprite.y + (int)y);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
