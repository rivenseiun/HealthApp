package com.whu.healthapp.activity.asses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.whu.healthapp.R;
import com.whu.healthapp.activity.homepage.HomePagerActivity;
import com.whu.healthapp.assesment.SesameCreditPanel;
import com.whu.healthapp.assesment.SesameItemModel;
import com.whu.healthapp.assesment.SesameModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AssesLadyWeight extends AppCompatActivity {
    private SimpleDateFormat formater = new SimpleDateFormat("yyyy.MM.dd");
    private String ladyWeight;
    private TextView ladyWeightText;
    private Button viewLadyWeight;
    private String grade;
    private String comment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asses_lady_weight);
        ladyWeightText=(TextView)findViewById(R.id.lady_weight_value);
        viewLadyWeight = (Button)findViewById(R.id.btn_view_lady_weight);

        //测试数据
       // grade= 100+"";
        //comment = "您的健康状况非常良好，请继续保持！";

        //设定该页面显示4秒后自动跳转回主页面(便于主页更新数据)
        TimerTask task = new TimerTask(){
            public void run(){

                Intent intent = new Intent(AssesLadyWeight.this, HomePagerActivity.class);
                startActivity(intent);
                AssesLadyWeight.this.finish();

                //execute the task
            }
        };
        final Timer timer = new Timer();
        timer.schedule(task, 7000);



        Intent intent=getIntent();

        //体重数据及评估结果
        ladyWeight=intent.getStringExtra("ladyWeight");
        grade = intent.getStringExtra("grade");
        comment = intent.getStringExtra("comment");



        //跳转到报告页面
        viewLadyWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                Intent intent = new Intent(AssesLadyWeight.this, ReportActivity.class);
                intent.putExtra("type","您的孕妇体重评分：");
                intent.putExtra("grade",grade);
                intent.putExtra("comment",comment);
                startActivity(intent);
                finish();
            }
        });

        ladyWeightText.setText(ladyWeight+"kg");
        ((SesameCreditPanel) findViewById(R.id.panel)).setDataModel(getData());


    }



    private SesameModel getData() {
        SesameModel model = new SesameModel();
        model.setUserTotal(Integer.valueOf(grade));



        model.setUserTotal(Integer.parseInt(grade));
        if(model.getUserTotal() <= 20){
            model.setAssess("较差");
        }
        else if(model.getUserTotal()<= 40){
            model.setAssess("中等");
        }
        else if(model.getUserTotal()<= 60){
            model.setAssess("良好");
        }
        else if(model.getUserTotal()<= 80){
            model.setAssess("优秀");
        }
        else if(model.getUserTotal()<= 100){
            model.setAssess("极好");
        }
        model.setTotalMin(0);
        model.setTotalMax(100);
        model.setFirstText("孕妇体重评分");
        model.setFourText("评估时间:" + formater.format(new Date()));
        ArrayList<SesameItemModel> sesameItemModels = new ArrayList<SesameItemModel>();

        SesameItemModel ItemModel350 = new SesameItemModel();
        ItemModel350.setArea("较差");
        ItemModel350.setMin(0);
        ItemModel350.setMax(20);
        sesameItemModels.add(ItemModel350);

        SesameItemModel ItemModel550 = new SesameItemModel();
        ItemModel550.setArea("中等");
        ItemModel550.setMin(20);
        ItemModel550.setMax(40);
        sesameItemModels.add(ItemModel550);

        SesameItemModel ItemModel600 = new SesameItemModel();
        ItemModel600.setArea("良好");
        ItemModel600.setMin(40);
        ItemModel600.setMax(60);
        sesameItemModels.add(ItemModel600);

        SesameItemModel ItemModel650 = new SesameItemModel();
        ItemModel650.setArea("优秀");
        ItemModel650.setMin(60);
        ItemModel650.setMax(80);
        sesameItemModels.add(ItemModel650);

        SesameItemModel ItemModel700 = new SesameItemModel();
        ItemModel700.setArea("极好");
        ItemModel700.setMin(80);
        ItemModel700.setMax(100);
        sesameItemModels.add(ItemModel700);

        model.setSesameItemModels(sesameItemModels);
        return model;
    }
}
