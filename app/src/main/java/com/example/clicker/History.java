package com.example.clicker;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class History extends AppCompatActivity {

    List<Integer> list = new ArrayList<>();
    CalendarView calendarview;
    ImageView acc_imageView;
    TextView acc_textView;
    TextView textView_click;
    private static final int acc0_limit = 10;
    private static final int acc1_limit = 100;
    private static final int acc2_limit = 666;
    private static final int acc3_limit = 888;
    int[] sdata = {2, 4, 8, 16};

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LitePal.initialize(this);
        LitePal.getDatabase();
        setBaseTheme();
        setContentView(R.layout.activity_history);

        calendarview = (CalendarView) findViewById(R.id.calendar_view);
        acc_imageView = findViewById(R.id.history_acc_image);
        acc_textView = findViewById(R.id.history_acc_text);
        textView_click = findViewById(R.id.history_clicker_text);

        calendarview.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                List<HistoryData> dataList = LitePal
                        .where("year=? and month=? and day=?", Integer.toString(year), Integer.toString(month), Integer.toString(dayOfMonth))
                        .find(HistoryData.class);

                if (dataList.size() == 0) {
                    Log.d("show", "new");
                    ui_change(-1,0);
                } else {
                    Log.d("show", "exist");
                    HistoryData temp = dataList.get(0);
                    int i_count = temp.getI_count();
                    if (i_count < acc0_limit){
                        ui_change(-1,i_count);}
                    else if(i_count<acc1_limit){
                        ui_change(0,i_count);
                    }else if(i_count<acc2_limit){
                        ui_change(1,i_count);
                    }else if(i_count<acc3_limit){
                        ui_change(2,i_count);
                    }else {
                        ui_change(3,i_count);
                    }
                }

                list.add(dayOfMonth);
                if (list.size() >= 4) {
                    int ii = 0;
                    for (Integer data : list) {
                        if (data != sdata[ii])
                            break;
                        ii++;
                    }

                    if (ii == 4) {
                        SharedPreferences pre = getSharedPreferences("First6", MODE_PRIVATE);
                        SharedPreferences.Editor pre_edit = getSharedPreferences("First6", MODE_PRIVATE).edit();
                        int max_acc = pre.getInt("max_acc", -1);
                        if (max_acc < 3)
                            max_acc++;
                        pre_edit.putInt("max_acc", max_acc);
                        pre_edit.apply();
                        Toast.makeText(History.this, "成就已解锁", Toast.LENGTH_SHORT).show();
                    }
                    list.clear();
                }
            }
        });

        Calendar calendar_now = Calendar.getInstance();//取得当前时间的年月日 时分秒.
        int year = calendar_now.get(Calendar.YEAR);
        int month = calendar_now.get(Calendar.MONTH);
        int day = calendar_now.get(Calendar.DAY_OF_MONTH);

        List<HistoryData> dataList = LitePal
                .where("year=? and month=? and day=?", Integer.toString(year), Integer.toString(month), Integer.toString(day))
                .find(HistoryData.class);

        if (dataList.size() == 0) {
            Log.d("show", "new");
            ui_change(-1,0);
        } else {
            Log.d("show", "exist");
            HistoryData temp = dataList.get(0);
            int i_count = temp.getI_count();
            if (i_count < acc0_limit){
                ui_change(-1,i_count);}
            else if(i_count<acc1_limit){
                ui_change(0,i_count);
            }else if(i_count<acc2_limit){
                ui_change(1,i_count);
            }else if(i_count<acc3_limit){
                ui_change(2,i_count);
            }else {
                ui_change(3,i_count);
            }
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void ui_change(int i,int count) {
        acc_textView.setVisibility(View.VISIBLE);
        acc_imageView.setVisibility(View.VISIBLE);
        switch (i) {
            case 0:
                acc_textView.setText("七步之内");
                acc_textView.setTextColor(getColor(R.color.teal_700));
                acc_imageView.setBackground(getDrawable(R.drawable.acc_00));
                textView_click.setTextColor(getColor(R.color.teal_700));
                textView_click.setText(Integer.toString(count));
                break;
            case 1:
                acc_textView.setText("无情铁手");
                acc_textView.setTextColor(getColor(R.color.purple_700));
                acc_imageView.setBackground(getDrawable(R.drawable.acc_1));
                textView_click.setTextColor(getColor(R.color.purple_700));
                textView_click.setText(Integer.toString(count));
                break;
            case 2:
                acc_textView.setText("无情铁手2.0");
                textView_click.setTextColor(getColor(R.color.red));
                acc_textView.setTextColor(getColor(R.color.red));
                acc_imageView.setBackground(getDrawable(R.drawable.acc_2));
                textView_click.setText(Integer.toString(count));
                break;
            case 3:
                acc_textView.setText("无影斩");
                textView_click.setTextColor(getColor(R.color.org));
                acc_textView.setTextColor(getColor(R.color.org));
                acc_imageView.setBackground(getDrawable(R.drawable.acc_3));
                textView_click.setText(Integer.toString(count));
                break;
            default:
                acc_imageView.setVisibility(View.INVISIBLE);
                acc_textView.setVisibility(View.INVISIBLE);
                textView_click.setText(Integer.toString(count));
                textView_click.setTextColor(Color.BLACK);
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

}