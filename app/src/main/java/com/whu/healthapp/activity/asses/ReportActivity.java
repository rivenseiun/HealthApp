package com.whu.healthapp.activity.asses;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.whu.healthapp.R;
import com.whu.healthapp.activity.homepage.HomePagerActivity;
import com.whu.healthapp.activity.test.display.BodyTDsiplayActivity;

public class ReportActivity extends AppCompatActivity {

    private TextView typeView,gradeView,commentView;
    private String type,grade,comment;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_report);

        typeView =(TextView)findViewById(R.id.report_type);
        gradeView =(TextView)findViewById(R.id.report_grade);
        commentView =(TextView)findViewById(R.id.report_comment);
        back = (Button)findViewById(R.id.report_back);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        grade = intent.getStringExtra("grade");
        comment = intent.getStringExtra("comment");

        typeView.setText(type);
        gradeView.setText(grade);
        commentView.setText(comment);

        //返回按钮
        back = (Button)findViewById(R.id.report_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, HomePagerActivity.class);
                startActivity(intent);
                ReportActivity.this.finish();
            }
        });;





    }
}
