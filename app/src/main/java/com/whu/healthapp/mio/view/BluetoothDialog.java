package com.whu.healthapp.mio.view;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.whu.healthapp.R;


public class BluetoothDialog extends Dialog implements
		View.OnClickListener {
	Context context;

	public interface BluetoothDialogListener {
		void callBack(boolean b);
	}

	private BluetoothDialogListener bluetoothDialogListener = null;
	private TextView okTextView;
	private TextView cancelTextView;
	private boolean isOpen = false;

	public BluetoothDialog(Context context) {
		super(context);
		this.context = context;
	}

	public BluetoothDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.bluetooth_dialog);
		this.setCanceledOnTouchOutside(false);
		okTextView = (TextView) findViewById(R.id.bluetooth_dialog_ok);
		cancelTextView = (TextView) findViewById(R.id.bluetooth_dialog_cancel);
		okTextView.setOnClickListener(this);
		cancelTextView.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bluetooth_dialog_ok:
			Log.v("TAG", "ok");
			okTextView.setBackgroundColor(context.getResources().getColor(
					R.color.blueviolet));
			okTextView.setTextColor(context.getResources().getColor(
					R.color.white));
			openBluetooth();
			this.dismiss();
			break;
		case R.id.bluetooth_dialog_cancel:
			Log.v("TAG", "cancel");
			cancelTextView.setBackgroundColor(context.getResources().getColor(
					R.color.blueviolet));
			cancelTextView.setTextColor(context.getResources().getColor(
					R.color.white));
			this.dismiss();
			break;

		default:
			break;
		}

	}

	private void openBluetooth() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(getContext(), "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_SHORT)
					.show();
			this.dismiss();
		} else {
			if (!mBluetoothAdapter.isEnabled()) {
				mBluetoothAdapter.enable();
				isOpen=true;
//				bluetoothDialogListener.callBack(isOpen);
			}
		}

	}

	public BluetoothDialogListener getBluetoothDialogListener() {
		return bluetoothDialogListener;
	}

	public void setBluetoothDialogListener(
			BluetoothDialogListener bluetoothDialogListener) {
		this.bluetoothDialogListener = bluetoothDialogListener;
	}

}
