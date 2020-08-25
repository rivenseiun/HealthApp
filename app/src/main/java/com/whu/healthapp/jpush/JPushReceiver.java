package com.whu.healthapp.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.apkfuns.logutils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
		LogUtils.d("[JPushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			//SDK 向 JPush Server 注册所得到的注册 全局唯一的 ID ，可以通过此 ID 向对应的客户端发送消息和通知。
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
        }

		//收到了自定义消息 Push
		else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			LogUtils.d("[JPushReceiver] 接收到推送下来的消息");
			//收到消息后传递给具体的页面作出响应
        	processCustomMessage(context, bundle);
        }

		//收到了通知 Push。
		else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			LogUtils.d("[JPushReceiver] 接收到推送下来的通知");
			//通知栏的Notification ID，可以用于清除Notification
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			//保存服务器推送下来的通知的标题。
			String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
			//保存服务器推送下来的通知内容。
			String content = bundle.getString(JPushInterface.EXTRA_ALERT);
			//保存服务器推送下来的附加字段。这是个 JSON 字符串。
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			//保存服务器推送下来的内容类型。
			String type = bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
			//富媒体通知推送下载的HTML的文件路径,用于展现WebView。
			String fileHtml = bundle.getString(JPushInterface.EXTRA_RICHPUSH_HTML_PATH);
			//富媒体通知推送下载的图片资源的文件名,多个文件名用 “，” 分开。 与 “JPushInterface.EXTRA_RICHPUSH_HTML_PATH” 位于同一个路径。
			String fileStr = bundle.getString(JPushInterface.EXTRA_RICHPUSH_HTML_RES);
			if(fileStr != null) {
				String[] fileNames = fileStr.split(",");
			}
			//唯一标识
			String id = bundle.getString(JPushInterface.EXTRA_MSG_ID);
        	
        }

		//用户点击了通知。
		else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			LogUtils.d("[JPushReceiver] 打开了推送下来的通知");
			//保存服务器推送下来的通知的标题。
			String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
			//保存服务器推送下来的通知内容。
			String content = bundle.getString(JPushInterface.EXTRA_ALERT);
			//保存服务器推送下来的附加字段。这是个 JSON 字符串。
			String type = bundle.getString(JPushInterface.EXTRA_EXTRA);
			//通知栏的Notification ID，可以用于清除Notification
			int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			//唯一标识调整消息的 ID, 可用于上报统计等。
			String file = bundle.getString(JPushInterface.EXTRA_MSG_ID);


//        	//打开自定义的Activity
//        	Intent i = new Intent(context, LoginActivity.class);
//			i.putExtras(bundle);
//        	//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//        	context.startActivity(i);
        	
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
			LogUtils.d( "[JPushReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        	
        }

		//用户接受Rich Push Javascript 回调函数的intent
		else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			LogUtils.d( "[JPushReceiver]" + intent.getAction() +" JPUSH连接 ： "+connected);
        } else {
			LogUtils.d( "[JPushReceiver] 没有绑定的连接 - " + intent.getAction());
        }
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
					LogUtils.i( "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next().toString();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					LogUtils.e( "获取额外消息失败");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	//收到消息后调用的事件
	private void processCustomMessage(Context context, Bundle bundle) {
		//保存服务器推送下来的消息的标题。
		String title = bundle.getString(JPushInterface.EXTRA_TITLE);
		//保存服务器推送下来的消息内容。
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		//保存服务器推送下来的附加字段。这是个 JSON 字符串。
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		//保存服务器推送下来的内容类型。
		String type = bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
		//富媒体通消息推送下载后的文件路径和文件名。
		String file = bundle.getString(JPushInterface.EXTRA_RICHPUSH_FILE_PATH);
		//唯一标识消息的 ID, 可用于上报统计等。
		String id = bundle.getString(JPushInterface.EXTRA_MSG_ID);



	}
}
