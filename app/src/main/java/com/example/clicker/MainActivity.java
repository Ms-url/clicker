package com.example.clicker;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.clicker.widget.rain.ISummoner;
import com.example.clicker.widget.rain.RainView;
import com.example.clicker.widget.rain.raindrop.DrawableRaindrop;
import com.example.clicker.widget.rain.raindrop.IRainDrop;
import com.example.clicker.widget.rain.shape.PolygonShape;
import com.example.clicker.widget.rain.shape.StarShape;

import org.litepal.LitePal;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button button;
    ImageView acc_imageView;
    TextView acc_textView;
    TextView textView;
    RainView rainView;
    ConstraintLayout constraintLayout;
    private int i;
    private int setting;
    private static final int acc0_limit = 100;
    private static final int acc1_limit = 1000;
    private static final int acc2_limit = 6666;
    private static final int acc3_limit = 8888;
    private static final int[] acc_list = {acc0_limit, acc1_limit, acc2_limit, acc3_limit, 0};

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseTheme();
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        acc_imageView = findViewById(R.id.accomplishment_image);
        acc_textView = findViewById(R.id.accomplishment_text);
        rainView = findViewById(R.id.rain_view);
        constraintLayout = findViewById(R.id.root);
        rainView.setMaxRaindropCount(24);
        rainView.setRaindropCreator(new RaindropCreator());

        SharedPreferences pre = getSharedPreferences("First6", MODE_PRIVATE);
        SharedPreferences.Editor pre_edit = getSharedPreferences("First6", MODE_PRIVATE).edit();

        // 时间
        Calendar calendar_now = Calendar.getInstance();//取得当前时间的年月日 时分秒.
        int day_now = calendar_now.get(Calendar.DAY_OF_MONTH);
        int day = pre.getInt("day", 0);
        setting = pre.getInt("acc_set", -1);

        if (day_now == day) {
            i = pre.getInt("i", 0);
        } else {
            i = 0;
            pre_edit.putInt("i", 0);
            pre_edit.putInt("day", day_now);
        }
        pre_edit.apply();

        textView.setText("+ " + i);
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
            @Override
            public void onClick(View view) {
                i++;
                textView.setText("+ " + i);
                if (i == acc0_limit || i == acc1_limit || i == acc2_limit || i == acc3_limit) {

                    SharedPreferences.Editor pre_edit = getSharedPreferences("First6", MODE_PRIVATE).edit();
                    switch (i) {
                        case acc0_limit:
                            pre_edit.putInt("acc_set", 0);
                            if (i >= acc_list[pre.getInt("max_acc", acc_list.length - 1)])
                                pre_edit.putInt("max_acc", 0);
                            break;
                        case acc1_limit:
                            pre_edit.putInt("acc_set", 1);
                            if (i >= acc_list[pre.getInt("max_acc", acc_list.length - 1)])
                                pre_edit.putInt("max_acc", 1);
                            break;
                        case acc2_limit:
                            pre_edit.putInt("acc_set", 2);
                            if (i >= acc_list[pre.getInt("max_acc", acc_list.length - 1)])
                                pre_edit.putInt("max_acc", 2);
                            break;
                        case acc3_limit:
                            pre_edit.putInt("acc_set", 3);
                            if (i >= acc_list[pre.getInt("max_acc", acc_list.length - 1)])
                                pre_edit.putInt("max_acc", 3);
                            break;
                        default:
                    }
                    pre_edit.putInt("i", i);
                    pre_edit.apply();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                    finish();

                }
            }
        });


        if (setting != -1) {
            rainView.setVisibility(View.VISIBLE);
            acc_textView.setVisibility(View.VISIBLE);
            acc_imageView.setVisibility(View.VISIBLE);
            switch (setting) {
                case 0:
                    ui_change(acc0_limit);
                    break;
                case 1:
                    ui_change(acc1_limit);
                    break;
                case 2:
                    ui_change(acc2_limit);
                    break;
                case 3:
                    ui_change(acc3_limit);
                    break;
            }
            rainView.fall();
            new Thread(() -> {
                try {
                    Thread.sleep(10000);
                    Log.e("stop", "stop");
                    rainView.setVisibility(View.INVISIBLE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            ObjectAnimator animator = ObjectAnimator.ofFloat(acc_imageView, "alpha", 0, 1);
            ObjectAnimator animator_fb = ObjectAnimator.ofFloat(acc_textView, "alpha", 0, 1);
            animator_fb.setDuration(2000);
            animator.setDuration(2000);
            animator.start();
            animator_fb.start();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void ui_change(int i) {
        switch (i) {
            case acc0_limit:
                acc_textView.setText("七步之内");
                acc_textView.setTextColor(getColor(R.color.teal_700));
                acc_imageView.setBackground(getDrawable(R.drawable.acc_00));
                button.setText("我就是个按钮");
                textView.setTextColor(getColor(R.color.teal_700));
                break;
            case acc1_limit:
                acc_textView.setText("无情铁手");
                acc_textView.setTextColor(getColor(R.color.purple_700));
                acc_imageView.setBackground(getDrawable(R.drawable.acc_1));
                button.setText("我真的就是个按钮");
                textView.setTextColor(getColor(R.color.purple_700));
                break;
            case acc2_limit:
                acc_textView.setText("无情铁手2.0");
                textView.setTextColor(getColor(R.color.red));
                acc_textView.setTextColor(getColor(R.color.red));
                button.setText("我真的只是一个按钮");
                acc_imageView.setBackground(getDrawable(R.drawable.acc_2));
                break;
            case acc3_limit:
                acc_textView.setText("无影斩");
                textView.setTextColor(getColor(R.color.org));
                acc_textView.setTextColor(getColor(R.color.org));
                acc_imageView.setBackground(getDrawable(R.drawable.acc_3));
                button.setText("七步之内无招胜有招");
                break;
            default:
        }

    }

    private void setBaseTheme() {
        SharedPreferences pre = getSharedPreferences("First6", MODE_PRIVATE);
        int themeType = pre.getInt("acc_set", -1);
        int themeId;
        switch (themeType) {
            case 0:
                themeId = R.style.acc_0;
                setTheme(themeId);
                break;
            case 1:
                themeId = R.style.acc_1;
                setTheme(themeId);
                break;
            case 2:
                themeId = R.style.acc_2;
                setTheme(themeId);
                break;
            case 3:
                themeId = R.style.acc_3;
                setTheme(themeId);
                break;
            default:

        }

    }


    //该方法用于创建显示Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option, menu);
        return true;
    }

    //在选项菜单打开以后会调用这个方法，设置menu图标显示（icon）
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    //该方法对菜单的item进行监听
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:
                Intent intent = new Intent(MainActivity.this, History.class);
                save_data(i);
                startActivity(intent);
                break;
            case R.id.menu2:
                initPopWindow_pan();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void initPopWindow_pan() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popWindow_pan = layoutInflater.inflate(R.layout.pop_acc, null);
        // PopupWindow 实例，传入 View 及宽高
        final PopupWindow popupWindow = new PopupWindow(popWindow_pan,
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.popWin_anim_style);
        popupWindow.showAtLocation(constraintLayout, Gravity.BOTTOM, 0, 260);

        RadioGroup group = popWindow_pan.findViewById(R.id.radio_group);

        ImageView imageView0 = popWindow_pan.findViewById(R.id.acc_image0);
        ImageView imageView1 = popWindow_pan.findViewById(R.id.acc_image1);
        ImageView imageView2 = popWindow_pan.findViewById(R.id.acc_image2);
        ImageView imageView3 = popWindow_pan.findViewById(R.id.acc_image3);
        TextView textView0 = popWindow_pan.findViewById(R.id.acc_text0);
        TextView textView1 = popWindow_pan.findViewById(R.id.acc_text1);
        TextView textView2 = popWindow_pan.findViewById(R.id.acc_text2);
        TextView textView3 = popWindow_pan.findViewById(R.id.acc_text3);

        SharedPreferences pre = getSharedPreferences("First6", MODE_PRIVATE);
        int max_acc = pre.getInt("max_acc", acc_list.length - 1);
        Log.d("acc_level",Integer.toString(max_acc));
        switch (max_acc) {
            default:
                imageView0.setBackground(getDrawable(R.drawable.ic_lock_foreground));
                textView0.setText("成就未解锁");
                textView0.setTextColor(getColor(R.color.w2));
            case 0:
                imageView1.setBackground(getDrawable(R.drawable.ic_lock_foreground));
                textView1.setText("成就未解锁");
                textView1.setTextColor(getColor(R.color.w2));
            case 1:
                imageView2.setBackground(getDrawable(R.drawable.ic_lock_foreground));
                textView2.setText("成就未解锁");
                textView2.setTextColor(getColor(R.color.w2));
            case 2:
                imageView3.setBackground(getDrawable(R.drawable.ic_lock_foreground));
                textView3.setText("成就未解锁");
                textView3.setTextColor(getColor(R.color.w2));
            case 3:
        }

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                SharedPreferences.Editor pre_edit = getSharedPreferences("First6", MODE_PRIVATE).edit();
                int t_set = -1;
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.radio_acc0:
                        t_set = 0;
                        break;
                    case R.id.radio_acc1:
                        t_set = 1;
                        break;
                    case R.id.radio_acc2:
                        t_set = 2;
                        break;
                    case R.id.radio_acc3:
                        t_set = 3;
                        break;
                    default:
                        break;
                }
                if (t_set <= max_acc) {
                    Log.d("acc","unlock:: t_set:"+t_set+" max_acc:"+max_acc);
                    pre_edit.putInt("acc_set", t_set);
                    pre_edit.apply();
                } else {
                    Toast.makeText(MainActivity.this, "成就未解锁", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final WindowManager.LayoutParams lp = MainActivity.this.getWindow().getAttributes();
        lp.alpha = 0.7f;//代表透明程度，范围为0 - 1.0f
        MainActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        MainActivity.this.getWindow().setAttributes(lp);
        /**
         * 退出popupWindow时取消暗背景
         */
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lp.alpha = 1.0f;
                MainActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                MainActivity.this.getWindow().setAttributes(lp);
                SharedPreferences.Editor pre_edit = getSharedPreferences("First6", MODE_PRIVATE).edit();
                pre_edit.putInt("i", i);
                pre_edit.apply();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                finish();
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        SharedPreferences.Editor pre2 = getSharedPreferences("First6", MODE_PRIVATE).edit();
        pre2.putInt("i", i);
        pre2.apply();

        save_data(i);
        return super.onKeyDown(keyCode, event);
    }

    public static void save_data(int i){
        Calendar calendar_now = Calendar.getInstance();//取得当前时间的年月日 时分秒.
        int year = calendar_now.get(Calendar.YEAR);
        int month = calendar_now.get(Calendar.MONTH);
        int day = calendar_now.get(Calendar.DAY_OF_MONTH);

        List<HistoryData> dataList = LitePal
                .where("year=? and month=? and day=?", Integer.toString(year), Integer.toString(month),Integer.toString(day))
                .find(HistoryData.class);

        if (dataList.size()==0){
            Log.d("save","new");
            HistoryData historyData = new HistoryData(
                    i,
                    calendar_now.get(Calendar.YEAR),
                    calendar_now.get(Calendar.MONTH),
                    calendar_now.get(Calendar.DAY_OF_MONTH));
            historyData.save();
        }else {
            Log.d("save","exist");
            HistoryData temp = dataList.get(0);
            temp.setI_count(i);
            temp.save();
        }
    }

    /**
     * ////////////////////////////////// 内部类
     */
    class RaindropCreator implements ISummoner.IRaindropCreator {

        Random mRandom = new Random();

        @Override
        public void injectRaindrops(ISummoner summoner) {
            List<IRainDrop> raindrops = summoner.getRaindrops();
            if (raindrops.size() < summoner.getMaxRaindropCount()) {

                DrawableRaindrop raindrop;
                Drawable drawable = null;

                ShapeDrawable shapeDrawable = new ShapeDrawable();
                shapeDrawable.getPaint().setColor(getRandomColor());
                drawable = shapeDrawable;
                raindrop = new DrawableRaindrop(drawable);
                raindrop.setRaindropHeight((int) dp2px(20));
                raindrop.setRaindropWidth((int) dp2px(20));
                shapeDrawable.setShape(getRandomShape());

                int x = mRandom.nextInt(rainView.getMeasuredWidth());
                int y = mRandom.nextInt(rainView.getMeasuredHeight());
                raindrop.setInitPosition(new PointF(x, y));

                raindrop.setSpeedY(mRandom.nextInt(10) + 5);

                raindrop.setRaindropRotationSpeed(mRandom.nextInt(2) + 1);
                raindrop.setLoop(true);
                raindrops.add(raindrop);
            }
        }


        Shape getRandomShape() {
            Shape shape = new RectShape();
            int i = mRandom.nextInt(6);
            switch (i) {
                case 0:
                    shape = new RoundRectShape(new float[]{15, 15, 15, 15, 15, 15, 15, 15}, null, null);
                    break;
                case 1:
                    shape = new RectShape();
                    break;
                case 2:
                    break;
                case 3:
                    shape = new PolygonShape(new float[]{120, 240, 360});
                    break;
                case 4:
                    shape = new StarShape(new float[]{-120, -60, 0, 60, 120, 180});
                    break;
                case 5:
                    shape = new StarShape(new float[]{-144, -108, -72, -36, 0, 36, 72, 108, 144, 180});
                    break;
            }
            return shape;
        }

        int getRandomColor() {
            int color = Color.RED;
            int i = mRandom.nextInt(5);
            switch (i) {
                case 0:
                    color = Color.GREEN;
                    break;
                case 1:
                    color = Color.YELLOW;
                    break;
                case 2:
                    color = Color.RED;
                    break;
                case 3:
                    color = Color.BLUE;
                    break;
                case 4:
                    color = Color.GRAY;
                    break;

            }
            return color;
        }

        private float dp2px(float dp) {
            return getResources().getDisplayMetrics().density * dp + 0.5f;
        }
    }

}