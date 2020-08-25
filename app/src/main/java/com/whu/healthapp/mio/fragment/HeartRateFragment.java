package com.whu.healthapp.mio.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.wahoofitness.common.log.Logger;
import com.wahoofitness.connector.HardwareConnectorEnums.SensorConnectionState;
import com.wahoofitness.connector.capabilities.Capability;
import com.wahoofitness.connector.capabilities.Capability.CapabilityType;
import com.wahoofitness.connector.capabilities.Heartrate;
import com.wahoofitness.connector.capabilities.Heartrate.Data;
import com.wahoofitness.connector.conn.connections.SensorConnection;
import com.wahoofitness.connector.conn.connections.params.ConnectionParams;
import com.whu.healthapp.R;
import com.whu.healthapp.activity.asses.AssesBlood;
import com.whu.healthapp.bean.user.User;
import com.whu.healthapp.mio.service.HardwareConnectorService;
import com.whu.healthapp.mio.utils.Constant;
import com.whu.healthapp.mio.utils.HeartrateData;
import com.whu.healthapp.mio.utils.MyDBHelp;
import com.whu.healthapp.mio.utils.SleepModel;
import com.whu.healthapp.mio.view.BluetoothDialog;
import com.whu.healthapp.mio.view.CancelView;
import com.whu.healthapp.mio.view.DrawChart;
import com.whu.healthapp.mio.view.ProcessView;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HeartRateFragment extends HardwareConnectorFragment {
	private final Heartrate.Listener mHeartrateListener = new Heartrate.Listener() {

		@Override
		public void onHeartrateData(Data data) {
			if (moveState == 1) {
				startMove();
				processView.setVisibility(View.GONE);
				cancelView.setVisibility(View.VISIBLE);
				RotateAnimation animation = new RotateAnimation(0f, 45f,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				animation.setDuration(1000);// 设置动画持续时间
				animation.setFillAfter(true);
				cancelView.setAnimation(animation);
				animation.startNow();
				drawChart.setVisibility(View.VISIBLE);
				heartraTextView.setVisibility(View.VISIBLE);
			}
			Heartrate heartrate = (Heartrate) getCapability(CapabilityType.Heartrate);
			data = heartrate.getHeartrateData();
			int rate = data.getHeartrateBpm();
			drawChart.startInvalidate(rate);
			heartraTextView.setText(rate + " bmp");
			time.setToNow();
			if (tempMinute == -1) {
				tempYear = time.year;
				tempMonth = time.month+1;
				tempDay = time.monthDay;
				tempHour = time.hour;
				tempMinute = time.minute;
				Log.v("sql", "TTTTTTTTTTTTTTT****************");
			}
			Log.v("sql", "time.minute-->" + time.minute);
			Log.v("sql", "tempTime.minute-->" + tempMinute);
			if (tempMinute == time.minute) {
				linkedList.add(rate);
				Log.v("sql", "tempTime.minute == time.minute   &&  add");
			} else {
				Log.v("sql", "sendmsg");
				// new RateUploadTask().execute();
				heartrateData = new HeartrateData(tempYear, tempMonth, tempDay,
						tempHour, tempMinute, new LinkedList<Integer>(
						linkedList));
				Message msg = Message.obtain();
				msg.what = Constant.HEARTATE_DB_SAVE;
				msg.obj = heartrateData;
				dbHandler.sendMessage(msg);
				tempYear = time.year;
				tempMonth = time.month+1;
				tempDay = time.monthDay;
				tempHour = time.hour;
				tempMinute = time.minute;
				linkedList.clear();
				linkedList.add(rate);
			}
		}

		@Override
		public void onHeartrateDataReset() {
		}
	};
	private MyDBHelp myDBHelp = null;
	private ContentValues contentValues = null;
	private LinkedList<Integer> linkedList = null;
	private Time time = null;
	private int tempYear = -1;
	private int tempMonth = -1;
	private int tempDay = -1;
	private int tempHour = -1;
	private int tempMinute = -1;

	private HeartrateData heartrateData;
	private View rootView;
	private List<ConnectionParams> mDiscoveredConnectionParams = new ArrayList<ConnectionParams>();
	private static final Logger L = new Logger(DiscoverFragment.class);

	private final Set<ConnectionParams> mSavedConnectionParams = new HashSet<ConnectionParams>();
	private WindowManager wm;
	private LinearLayout textViewLayout;
	private LinearLayout circleLayout;
	private ProcessView processView;
	private TextView titleTextView;
	private TextView heartraTextView;
	private DrawChart drawChart;
	private CancelView cancelView;
	private RelativeLayout mainLayout;
	private Button button;
	private RelativeLayout.LayoutParams layoutParams;
	private int rules = RelativeLayout.CENTER_IN_PARENT;
	private int xMove = 300;
	private int yMove = -300;
	private int moveState = 1;

	private int circleLayout_width = 0;
	private int circleLayout_height = 0;
	private int circleLayout_top = 0;
	private int circleLayout_left = 0;
	private int l = 0;
	private int t = 0;
	private int r = 0;
	private int b = 0;

	private boolean discovering = false;
	private ConnectionParams mConnectionParams = null;
	private SensorConnection sensorConnection;
	private Handler dbHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case Constant.HEARTATE_DB_SAVE:
					Log.v("sql", "handler begin");
					HeartrateData hData = (HeartrateData) msg.obj;
					String phoneNumber = "18720083869";
					int averageRate = 0;
					int state = 0;
					String dateTime = hData.getYear() + "-" + hData.getMonth()
							+ "-" + hData.getDay() + " " + hData.getHour() + ":"
							+ hData.getMinute();
					Log.v("sql", "hData.getLinkedList().size()"
							+ hData.getLinkedList().size());
					if (hData.getLinkedList().size() != 0) {
						Iterator<Integer> iterator = hData.getLinkedList()
								.iterator();
						int i = 0;
						int sumRate = 0;
						while (iterator.hasNext()) {
							i++;
							sumRate += iterator.next();
						}
						averageRate = sumRate / i;
						if (averageRate < 30 || averageRate > 150) {
							state = 1;
						}
						String insertString = "insert into heart_rate(phoneNumber,time,rate,state) values ("
								+ phoneNumber
								+ ","
								+ "'"
								+ dateTime
								+ "'"
								+ ","
								+ averageRate + "," + state + ");";
						SQLiteDatabase db = myDBHelp.getWritableDatabase();
						db.execSQL(insertString);
						Log.v("sql", insertString);
					}

					break;

				default:
					break;
			}

		};
	};
	private Handler listenerHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case Constant.HEARTATE_CONNECT_SUCCESS:
					getHeartrateCap().addListener(mHeartrateListener);
					break;

				default:
					break;
			}

		};

	};

	@Override
	public void onAttach(Activity activity) {
		Log.v("tag", "HeartRate:onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v("tag", "HeartRates:onCreate");
		myDBHelp = new MyDBHelp(getActivity());
		linkedList = new LinkedList<Integer>();
		time = new Time();
		mSavedConnectionParams.clear();
		SharedPreferences sharedPreferences = getActivity()
				.getSharedPreferences("PersistentConnectionParams", 0);
		for (Object entry : sharedPreferences.getAll().values()) {
			ConnectionParams params = ConnectionParams
					.fromString((String) entry);
			mSavedConnectionParams.add(params);
		}
		L.i("onCreate", mSavedConnectionParams.size(),
				"saved ConnectionParams loaded");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		Log.v("tag", "HeartRate:onCreateView");
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.frag_heartrate, container,
					false);
		}
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.v("tag", "HeartRate:onActivityCreate");
		LayoutInflater inflater = getActivity().getLayoutInflater();

		circleLayout = (LinearLayout) inflater.inflate(
				R.layout.frag_heartrate_centeritem, null);
		textViewLayout = (LinearLayout) circleLayout
				.findViewById(R.id.heartrate_circle_textview);
		processView = (ProcessView) circleLayout
				.findViewById(R.id.heartrate_circle_processview);
		titleTextView = (TextView) getActivity().findViewById(
				R.id.heartrate_title_textview);
		heartraTextView = (TextView) getActivity().findViewById(
				R.id.heartrate_show_text);
		cancelView = (CancelView) circleLayout
				.findViewById(R.id.heartrate_circle_cancelview);
		mainLayout = (RelativeLayout) getActivity().findViewById(
				R.id.frag_heartrate_layout);
		drawChart = (DrawChart) getActivity().findViewById(
				R.id.heartrate_circle_drawchart);
		layoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		button = (Button) getActivity().findViewById(R.id.upload);
		layoutParams.addRule(rules);
		circleLayout.setLayoutParams(layoutParams);
		mainLayout.addView(circleLayout);
		WindowManager wm = (WindowManager) getActivity().getSystemService(
				Context.WINDOW_SERVICE);
		int screenWidth = wm.getDefaultDisplay().getWidth();
		drawChart.setCircleLayout(circleLayout);
		drawChart.setFragLayout(mainLayout);

		circleLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!isBluetoothOn()) {
					BluetoothDialog dialog = new BluetoothDialog(getActivity(),
							R.style.dialog_style);
					dialog.show();
				}
				if (moveState == 1) {
					textViewLayout.setVisibility(View.GONE);
					processView.setVisibility(View.VISIBLE);
					enableDiscovery(true);
				} else {
					getHeartrateCap().removeListener(mHeartrateListener);
					disconnectSensor(mConnectionParams);
					enableDiscovery(false);
					startRevsMove();

				}

			}
		});
		super.onActivityCreated(savedInstanceState);
	}

	private void refresh() {
		Log.v("tag", "refresh()");
		discovering = isDiscovering();
		Log.v("tag", "discovering=" + discovering);
		mDiscoveredConnectionParams.clear();
		mDiscoveredConnectionParams.addAll(mSavedConnectionParams);

		for (SensorConnection connectedDevices : getSensorConnections()) {
			ConnectionParams connectedParams = connectedDevices
					.getConnectionParams();
			Log.v("tag", " connectedDevices.getConnectionParams()1");
			if (!mDiscoveredConnectionParams.contains(connectedParams)) {
				mDiscoveredConnectionParams.add(connectedParams);
				Log.v("tag",
						"mDiscoveredConnectionParams.add(connectedParams)-->"
								+ connectedParams);
			}
		}
		for (ConnectionParams discoveredParams : getDiscoveredConnectionParams()) {
			Log.v("tag", "getDiscoveredConnectionParams()");
			if (!mDiscoveredConnectionParams.contains(discoveredParams)) {
				mDiscoveredConnectionParams.add(discoveredParams);
				Log.v("tag",
						"mDiscoveredConnectionParams.add(connectedParams)2-->"
								+ discoveredParams);
			}
		}

		Collections.sort(mDiscoveredConnectionParams,
				new Comparator<ConnectionParams>() {

					@Override
					public int compare(ConnectionParams lhs,
									   ConnectionParams rhs) {
						return lhs.getName().compareTo(rhs.getName());
					}
				});

	}

	public void saveConnectionParams() {

		SharedPreferences sharedPreferences = getActivity()
				.getSharedPreferences("PersistentConnectionParams", 0);
		Editor editor = sharedPreferences.edit();
		editor.clear();
		int count = 0;
		for (ConnectionParams params : mSavedConnectionParams) {
			editor.putString("" + count++, params.serialize());
		}
		editor.commit();
		L.i("onDestroy", mSavedConnectionParams.size(),
				"ConnectionParams saved");
	}

	private boolean isBluetoothOn() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter != null) {
			// 如果蓝牙设备未开启
			if (!adapter.isEnabled()) {
				return false;
			}
			return true;
		}
		return false;

	}

	public ConnectionParams getItem(int position) {
		return mDiscoveredConnectionParams.get(position);
	}

	private void setXYMove() {
		wm = (WindowManager) getActivity().getSystemService(
				Context.WINDOW_SERVICE);
		int screenHeigth = wm.getDefaultDisplay().getHeight();
		int screenWidth = wm.getDefaultDisplay().getWidth();
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		titleTextView.measure(w, h);
		circleLayout.measure(w, h);
		int titleHeight = titleTextView.getMeasuredHeight();
		int titleWidth = titleTextView.getMeasuredWidth();
		Rect rect = new Rect();
		getActivity().getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(rect);
		int statusBarHeight = rect.top;
		int left = circleLayout.getLeft();
		int top = circleLayout.getTop();
		yMove = -(top - statusBarHeight - titleHeight);
		xMove = screenWidth
				- (int) (100 + circleLayout.getMeasuredWidth() * 0.3) - left;
	}

	private void startMove() {
		setXYMove();
		moveState = 2;
		// circleLayout.setBackgroundColor(Color.TRANSPARENT);
		// textViewLayout.setVisibility(View.GONE);
		// circleImageView.setVisibility(View.VISIBLE);
		Animation mScaleAnimation = new ScaleAnimation(1f, 0.3f, 1f, 0.3f);
		mScaleAnimation.setDuration(1000);

		Animation mTranslateAnimation = new TranslateAnimation(0, xMove, 0,
				yMove);
		mTranslateAnimation.setDuration(1000);
		AnimationSet mAnimationSet = new AnimationSet(true);
		mAnimationSet.addAnimation(mScaleAnimation);
		mAnimationSet.setFillAfter(true);
		mAnimationSet.setInterpolator(new DecelerateInterpolator());
		mAnimationSet.addAnimation(mTranslateAnimation);
		circleLayout.startAnimation(mAnimationSet);
		mTranslateAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationEnd(Animation arg0) {

				circleLayout_left = circleLayout.getLeft();
				circleLayout_top = circleLayout.getTop();
				circleLayout_width = circleLayout.getWidth();
				circleLayout_height = circleLayout.getHeight();
				circleLayout.clearAnimation();
				l = circleLayout_left + xMove;
				t = circleLayout_top + yMove;
				r = (int) (circleLayout_left + xMove + circleLayout_width * 0.3);
				b = (int) (circleLayout_top + yMove + circleLayout_height * 0.3);
				layoutParams.removeRule(rules);
				layoutParams.height = b - t;
				layoutParams.width = r - l;
				layoutParams.leftMargin = l;
				layoutParams.topMargin = t;
				circleLayout.setLayoutParams(layoutParams);
			}
		});
	}

	private void startRevsMove() {
		xMove = -xMove;
		yMove = -yMove;
		moveState = 1;
		cancelView.clearAnimation();
		cancelView.setVisibility(View.GONE);
		Animation mScaleAnimation = new ScaleAnimation(1f, (float) (10f / 3),
				1f, (float) (10f / 3));
		mScaleAnimation.setDuration(1000);
		mScaleAnimation.setFillAfter(true);

		Animation mTranslateAnimation = new TranslateAnimation(0, xMove, 0,
				yMove);
		mTranslateAnimation.setDuration(1000);
		AnimationSet mAnimationSet = new AnimationSet(false);
		mAnimationSet.addAnimation(mScaleAnimation);
		mAnimationSet.setFillAfter(true);
		mAnimationSet.setInterpolator(new BounceInterpolator());
		mAnimationSet.addAnimation(mTranslateAnimation);
		circleLayout.startAnimation(mAnimationSet);
		mTranslateAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				drawChart.setVisibility(View.GONE);
				heartraTextView.setVisibility(View.GONE);
				int left = circleLayout.getLeft();
				int top = circleLayout.getTop();
				int right = circleLayout.getRight();
				int bottom = circleLayout.getBottom();
				circleLayout.clearAnimation();
				layoutParams.width = LayoutParams.WRAP_CONTENT;
				layoutParams.height = LayoutParams.WRAP_CONTENT;
				layoutParams.setMargins(left, top, right, bottom);
				layoutParams.addRule(rules);
				circleLayout.setLayoutParams(layoutParams);
				textViewLayout.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.e("tag", "HeartRate:onDestroy");
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		Log.e("tag", "HeartRate:onPause");
		super.onPause();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.e("tag", "HeartRate:onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Log.e("tag", "HeartRate:onStop");
		super.onStop();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		Log.e("tag", "HeartRate:onResum");
		super.onResume();

	}

	@Override
	public void onDeviceDiscovered(ConnectionParams params) {
		refresh();
		if (discovering) {
			refresh();
			Toast.makeText(getActivity(), "发现MIO手环，正在连接请稍后...",
					Toast.LENGTH_SHORT).show();
			// params = getItem(0);
			this.mConnectionParams = getItem(0);
			Log.v("params", "param11" + this.mConnectionParams.toString());
			mSavedConnectionParams.add(params);
			sensorConnection = getSensorConnection(params);
			refresh();
			sensorConnection = connectSensor(params);

		} else {
			Toast.makeText(getActivity(), "抱歉，未发现MIO手环！！！", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void onDiscoveredDeviceLost(ConnectionParams params) {
		refresh();
		Toast.makeText(getActivity(), "onDiscoveredDeviceLost",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onHardwareConnectorServiceConnected(
			HardwareConnectorService hardwareConnectorService) {
		refresh();

	}

	@Override
	public void onSensorConnectionStateChanged(
			SensorConnection sensorConnection, SensorConnectionState state) {
		Toast.makeText(getActivity(), "onSensorConnectionStateChanged",
				Toast.LENGTH_SHORT).show();
		Log.v("params", "333sensorConnection.getConnectionParams()="
				+ sensorConnection);
		Log.v("params", "333sensorConnection.getConnectionParams()="
				+ getSensorConnection(mConnectionParams));
		Log.v("params", "333sensorConnection.getConnectionParams()="
				+ (getSensorConnection(mConnectionParams) == sensorConnection));
		Log.v("params", "333sensorConnection.getConnectionParams()="
				+ sensorConnection.getConnectionParams());
		Log.v("params", "333mConnectionParams=" + mConnectionParams);
		refresh();
		switch (sensorConnection.getConnectionState()) {
			case CONNECTED:
				Toast.makeText(getActivity(), "CONNECTED", Toast.LENGTH_SHORT)
						.show();
				// getHeartrateCap().addListener(mHeartrateListener);
				Message msg = Message.obtain();
				msg.what = Constant.HEARTATE_CONNECT_SUCCESS;
				listenerHandler.sendEmptyMessageDelayed(msg.what, 2500);
				break;
			case CONNECTING:
				Toast.makeText(getActivity(), "CONNECTING", Toast.LENGTH_SHORT)
						.show();
				break;
			case DISCONNECTED:
				Toast.makeText(getActivity(), "DISCONNECTED", Toast.LENGTH_SHORT)
						.show();
				break;
			case DISCONNECTING:
				Toast.makeText(getActivity(), "DISCONNECTING", Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();

				break;

		}
	}

	@Override
	public void onFirmwareUpdateRequired(SensorConnection sensorConnection,
										 String currentVersionNumber, String recommendedVersion) {
		refresh();

	}

	public void onNewCapabilityDetected(SensorConnection sensorConnection,
										CapabilityType capabilityType) {
		Toast.makeText(getActivity(), "onNewCapabilityDetected",
				Toast.LENGTH_SHORT).show();
		// if (isConnected) {
		// Message msg = Message.obtain();
		// msg.what = Constant.HEARTATE_CONNECT_SUCCESS;
		// handler.sendEmptyMessageDelayed(msg.what, 2500);
		// }
	}

	private Heartrate getHeartrateCap() {
		return (Heartrate) getCapability(CapabilityType.Heartrate);
	}

	protected Capability getCapability(CapabilityType capabilityType) {
		sensorConnection = getSensorConnection(mConnectionParams);
		if (sensorConnection != null) {
			Log.v("params", "111sensorConnection not null");
			if (sensorConnection.getCurrentCapability(capabilityType) != null) {
				Log.v("params",
						"222sensorConnection.getCurrentCapability(capabilityType) not null");
				return sensorConnection.getCurrentCapability(capabilityType);
			} else {
				Log.v("params",
						"222sensorConnection.getCurrentCapability(capabilityType) null");
				return null;
			}
		} else {
			Log.v("params", "111sensorConnection not null");
			return null;
		}
	}

	protected SensorConnection getSensorConnection() {
		return getSensorConnection(mConnectionParams);
	}

	public static String toJSONString(String[][] data) {

		JSONArray array = new JSONArray();
		for (int i = 0; i < data.length; i++) {
			JSONArray jsonarray = new JSONArray();// json数组，里面包含的内容为所有对象
			jsonarray.put(data[i][0]);
			jsonarray.put(data[i][1]);
			array.put(jsonarray);
		}
		return array.toString();
	}
}
