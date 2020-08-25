package com.whu.healthapp.bluetooth.data;

import android.content.Context;
import android.widget.TextView;

import com.whu.healthapp.R;
import com.whu.healthapp.bean.jqb.TestDataBean;
import com.whu.healthapp.view.jqb.TestReportBriefSurfaceView;


public class HealthEvaluation {

	private int tiwen, xinlv, xueyang, gaoxueya, dixueya, pingjunya, xiudaiya, mailv;
	private int tiwenflag, xinlvflag, xueyangflag, xueyaflag, pingjunyaflag, mailvflag;
	private int zonghe, zonghescore = 100;

	private TextView tvScore ;
	private Context context ;
	private TestReportBriefSurfaceView sfvReport;
	private TestDataBean data ;
	private String testMsg ="";


	public String evalution(Context context, TestDataBean data, TextView tvScore, TestReportBriefSurfaceView sfvReport){
		this.context = context;
		this.tvScore = tvScore;
		this.sfvReport =sfvReport;
		this.data = data;
		xinlvflag = tiwenflag = xueyangflag  = xueyaflag = mailvflag = zonghe = TestReportBriefSurfaceView.PREFECT;
		tvScore.setText("优秀");
		tvScore.setTextColor(context.getResources().getColor(R.color.perfect));
		testMsg= "";
		zonghescore = 100;
		//评估
		switch (data.getKind()) {
			case TestDataBean.TEST_ALL:
				evalutionTiwen();
				evalutionXinlv();
				evalutionXueya();
				evalutionXueyang();
				if (zonghescore > 80) {
					zonghe = TestReportBriefSurfaceView.PREFECT;
					tvScore.setText("优秀");
					tvScore.setTextColor(context.getResources().getColor(R.color.perfect));
				} else if (zonghescore >= 60) {
					zonghe = TestReportBriefSurfaceView.GOOD;
					tvScore.setText("良好");
					tvScore.setTextColor(context.getResources().getColor(R.color.good));
				} else {
					tvScore.setText("严重");
					tvScore.setTextColor(context.getResources().getColor(R.color.serious));
					zonghe = TestReportBriefSurfaceView.SERIOUS;
				}
				sfvReport.setJudge(xinlvflag, tiwenflag, xueyangflag, zonghe, xueyaflag, mailvflag);
				//综合评分
				break;
			case TestDataBean.TEST_TIWEN:
				evalutionTiwen();
				sfvReport.setJudge(xinlvflag, tiwenflag, xueyangflag, zonghe, xueyaflag, mailvflag);
				break;
			case TestDataBean.TEST_XINLV:
				evalutionXinlv();
				sfvReport.setJudge(xinlvflag, tiwenflag, xueyangflag, zonghe, xueyaflag, mailvflag);
				break;
			case TestDataBean.TEST_XUEYA:
				evalutionXueya();
				break;
			case TestDataBean.TEST_XUEYANG:
				evalutionXueyang();
				sfvReport.setJudge(xinlvflag, tiwenflag, xueyangflag, zonghe, xueyaflag, mailvflag);
				break;
		}
		return testMsg ;
	}

	public void evalutionXinlv() {
		int xinlv = 0;
		int huxi = 0;
		xinlv = Integer.valueOf(data.getXinlv());
		huxi = Integer.valueOf(data.getHuxi());
		if (xinlv != 0) {
			if (xinlv < 60) {
				testMsg = testMsg + "心率为："+xinlv+",心动过缓，低于下限60，若伴有头晕等症状，则可能有心血管疾病。\n";
				zonghe = TestReportBriefSurfaceView.SERIOUS;
				xinlvflag = TestReportBriefSurfaceView.SERIOUS;
				zonghescore -= 10;
				tvScore.setText("严重");
				tvScore.setTextColor(context.getResources().getColor(R.color.serious));
			} else if (xinlv > 100) {
				testMsg = testMsg + "心率为："+xinlv+",心动过速，高于上限100，需确认是否持续性心动过速。\n";
				zonghe = TestReportBriefSurfaceView.SERIOUS;
				xinlvflag = TestReportBriefSurfaceView.SERIOUS;
				zonghescore -= 20;
				tvScore.setText("严重");
				tvScore.setTextColor(context.getResources().getColor(R.color.serious));
			} else {
				testMsg = testMsg + "心率正常。\n";
				zonghe = TestReportBriefSurfaceView.PREFECT;
				xinlvflag = TestReportBriefSurfaceView.PREFECT;
				tvScore.setText("优秀");
				tvScore.setTextColor(context.getResources().getColor(R.color.perfect));
			}
		}
		if(huxi!=0){
			if(huxi<12){
				testMsg = testMsg + "呼吸率为："+huxi+",呼吸过缓，低于下限12，需确认是否持续性呼吸过缓\n";
				zonghescore -= 10;
				if(xinlvflag == TestReportBriefSurfaceView.PREFECT){
					tvScore.setText("严重");
					tvScore.setTextColor(context.getResources().getColor(R.color.serious));
					zonghe = TestReportBriefSurfaceView.SERIOUS;
				}
			}else if(huxi>20){
				testMsg = testMsg + "呼吸率为："+huxi+",呼吸过速，高于上限20，需确认是否持续性呼吸过速\n";
				zonghescore -= 10;
				if(xinlvflag == TestReportBriefSurfaceView.PREFECT){
					tvScore.setText("严重");
					tvScore.setTextColor(context.getResources().getColor(R.color.serious));
					zonghe = TestReportBriefSurfaceView.SERIOUS;
				}
			}else{
				testMsg = testMsg + "呼吸率正常";
			}
		}
	}

	public void evalutionXueya() {
		gaoxueya = Integer.valueOf(data.getShousuoya());
		dixueya = Integer.valueOf(data.getShuzhangya());
		if (gaoxueya != 0 && dixueya != 0) {
			if (gaoxueya < 90 || dixueya < 60) {
				testMsg = testMsg + "低压为："+dixueya+",血压偏低，正常高压应不低于90，低压不低于60，请注意观察是否有其他不适。\n";
				xueyaflag = TestReportBriefSurfaceView.SERIOUS;
				zonghe = TestReportBriefSurfaceView.SERIOUS;
				zonghescore -= 20;
				tvScore.setText("严重");
				tvScore.setTextColor(context.getResources().getColor(R.color.serious));
			} else if ((gaoxueya > 130 && gaoxueya <= 140) || (dixueya > 85 && dixueya <= 90)) {
				testMsg = testMsg + "高压为："+gaoxueya+",临界高血压，可能发展为高血压，需密切关注。\n";
				xueyaflag = TestReportBriefSurfaceView.GOOD;
				zonghe = TestReportBriefSurfaceView.GOOD;
				tvScore.setText("良好");
				zonghescore -= 10;
				tvScore.setTextColor(context.getResources().getColor(R.color.good));
			} else if ((gaoxueya > 140 && gaoxueya <= 160) || (dixueya > 90 && dixueya <= 100)) {
				testMsg = testMsg + "高压为："+gaoxueya+",轻度高血压，请注意低盐低脂饮食，密切关注血压变化。\n";
				xueyaflag = TestReportBriefSurfaceView.GOOD;
				zonghe = TestReportBriefSurfaceView.GOOD;
				tvScore.setText("良好");
				zonghescore -= 10;
				tvScore.setTextColor(context.getResources().getColor(R.color.good));
			} else if ((gaoxueya > 160 && gaoxueya <= 180) || (dixueya > 100 && dixueya <= 110)) {
				testMsg = testMsg + "高压为："+gaoxueya+",中度高血压，请就医并遵医嘱服用相应药物。\n";
				xueyaflag = TestReportBriefSurfaceView.SERIOUS;
				zonghe = TestReportBriefSurfaceView.SERIOUS;
				tvScore.setText("严重");
				zonghescore -= 20;
				tvScore.setTextColor(context.getResources().getColor(R.color.serious));
			} else if (gaoxueya > 180 || dixueya > 110) {
				testMsg = testMsg + "高压为："+gaoxueya+",重度高血压，若确认为持续性重度高血压，请立即就医并采取相应医疗手段。\n";
				xueyaflag = TestReportBriefSurfaceView.SERIOUS;
				zonghe = TestReportBriefSurfaceView.SERIOUS;
				tvScore.setText("严重");
				zonghescore -= 20;
				tvScore.setTextColor(context.getResources().getColor(R.color.serious));
			} else {
				testMsg = testMsg + "血压正常。\n";
				xueyaflag = TestReportBriefSurfaceView.PREFECT;
				zonghe = TestReportBriefSurfaceView.PREFECT;
				tvScore.setText("优秀");

				tvScore.setTextColor(context.getResources().getColor(R.color.perfect));
			}
		}
	}

	public void evalutionXueyang() {
		int xueyangbhd = 0;
		xueyangbhd = Integer.valueOf(data.getXueyangbhd());
		if (xueyangbhd != 0) {
			if (xueyangbhd < 95 && xueyangbhd >= 90) {
				zonghescore -= 10;
				xueyangflag = TestReportBriefSurfaceView.GOOD;
				testMsg = testMsg + "血氧饱和度为:"+xueyangbhd+",血氧饱和度低于正常值95，需注意是否有血脂过高等循环系统及呼吸系统疾病。\n";
			} else if (xueyangbhd < 90) {
				zonghescore -= 20;
				testMsg = testMsg + "血氧饱和度为:"+xueyangbhd+",血氧饱和度低于90，需注意是否有血脂过高等循环系统及呼吸系统疾病。\n";
				xueyangflag = TestReportBriefSurfaceView.SERIOUS;
			} else {
				xueyangflag = TestReportBriefSurfaceView.PREFECT;
				testMsg = testMsg + "血氧饱和度正常。\n";
			}
		}
		int mailv = Integer.valueOf(data.getMailv());
		if (mailv != 0) {
			if (mailv < 100 && mailv >= 60) {
				mailvflag = TestReportBriefSurfaceView.PREFECT;
				testMsg = testMsg + "脉率正常。\n";
			} else {
				zonghescore -= 20;
				mailvflag = TestReportBriefSurfaceView.SERIOUS;
				testMsg = testMsg + "脉率为:"+mailv+"脉率不在60~100，属于不正常。\n";
			}
		}

		if (mailvflag == TestReportBriefSurfaceView.PREFECT && xueyangflag == TestReportBriefSurfaceView.PREFECT) {
			zonghe = TestReportBriefSurfaceView.PREFECT;
			tvScore.setText("优秀");
			tvScore.setTextColor(context.getResources().getColor(R.color.perfect));
		} else {
			zonghe = TestReportBriefSurfaceView.SERIOUS;
			tvScore.setText("严重");
			tvScore.setTextColor(context.getResources().getColor(R.color.serious));

		}
	}

	public void evalutionTiwen() {
		float tiwen = 0;
		tiwen = Float.valueOf(data.getTiwen());
		if (tiwen != 0) {
			if (tiwen < 36) {
				zonghescore -= 20;
				testMsg = testMsg + "体温为："+tiwen+",体温过低，需要保暖并判断是否由疾病引起。\n";
				zonghe = TestReportBriefSurfaceView.SERIOUS;
				tiwenflag = TestReportBriefSurfaceView.SERIOUS;
				tvScore.setText("严重");
				tvScore.setTextColor(context.getResources().getColor(R.color.serious));
			} else if (tiwen >= 37.5 && tiwen <= 38.5) {
				zonghescore -= 10;
				testMsg = testMsg + "体温为："+tiwen+",体温偏高，多饮水，保持空气流通，判断是否由疾病引起。\n";
				zonghe = TestReportBriefSurfaceView.GOOD;
				tiwenflag = TestReportBriefSurfaceView.GOOD;
				tvScore.setText("良好");
				tvScore.setTextColor(context.getResources().getColor(R.color.good));
			} else if (tiwen > 38.5) {
				zonghescore -= 20;
				testMsg = testMsg + "体温为："+tiwen+",体温过高，需采取降温措施并就医。\n";
				zonghe = TestReportBriefSurfaceView.SERIOUS;
				tiwenflag = TestReportBriefSurfaceView.SERIOUS;
				tvScore.setText("严重");
				tvScore.setTextColor(context.getResources().getColor(R.color.serious));
			} else {
				testMsg = testMsg + "体温正常。\n";
				zonghe = TestReportBriefSurfaceView.PREFECT;
				tiwenflag = TestReportBriefSurfaceView.PREFECT;
				tvScore.setText("优秀");
				tvScore.setTextColor(context.getResources().getColor(R.color.perfect));
			}
		}
	}

	

}
