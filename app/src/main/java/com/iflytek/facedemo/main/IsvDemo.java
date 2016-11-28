package com.iflytek.facedemo.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import com.iflytek.cloud.VerifierListener;
import com.iflytek.cloud.VerifierResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iflytek.facedemo.R;
import com.iflytek.facedemo.passaty;

public class IsvDemo extends Activity implements OnClickListener {
	private static final String TAG = IsvDemo.class.getSimpleName();

	private static final int PWD_TYPE_NUM = 3;
	// 当前声纹密码类型，1、2、3分别为文本、自由说和数字密码
	private int mPwdType = PWD_TYPE_NUM;
	// 声纹识别对象
	private SpeakerVerifier mVerifier;
	// 声纹AuthId，用户在云平台的身份标识，也是声纹模型的标识
	// 请使用英文字母或者字母和数字的组合，勿使用中文字符
	private String mAuthId = "";
	// 数字声纹密码
	private String mNumPwd = "";
	// 数字声纹密码段，默认有5段

	private String[] mNumPwdSegs;
	private TextView mShowPwdTextView;
	private TextView mShowMsgTextView;
	private AlertDialog mTextPwdSelectDialog;
	private Toast mToast;
	private int judge;
	private String mname;
	private Button vertfy;
	private Button register;
	private Button getpass;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Intent i = getIntent();
		Bundle date = i.getExtras();
		mAuthId = date.getString("id");
		mname = date.getString("name");
		judge = date.getInt("pd");
		if(judge == 1){
			setContentView(R.layout.activity_yuyin_zhuce);
		}
		if(judge == 2){
			setContentView(R.layout.activity_yuyin_yanzheng);
		}
		initUi();

		// 初始化SpeakerVerifier，InitListener为初始化完成后的回调接口
		mVerifier = SpeakerVerifier.createVerifier(IsvDemo.this, new InitListener() {
			@Override
			public void onInit(int errorCode) {
				if (ErrorCode.SUCCESS == errorCode) {
					showTip("引擎初始化成功");
				} else {
					showTip("引擎初始化失败，错误码：" + errorCode);
				}
			}
		});
	}
	@SuppressLint("ShowToast")
	private void initUi() {
		mShowPwdTextView = (TextView) findViewById(R.id.showPwd);
		mShowMsgTextView = (TextView) findViewById(R.id.showMsg);
		getpass = (Button) findViewById(R.id.isv_getpassword);
		if(judge == 1){
			register = (Button) findViewById(R.id.isv_register);
			register.setOnClickListener(IsvDemo.this);
		}
		if(judge == 2){
			vertfy = (Button) findViewById(R.id.isv_verify);
			vertfy.setOnClickListener(IsvDemo.this);
		}
		getpass.setOnClickListener(IsvDemo.this);
		mToast = Toast.makeText(IsvDemo.this, "", Toast.LENGTH_SHORT);
		mToast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);

	}

	private void initTextView(){
		mNumPwd = null;
		AssetManager manager=getAssets();
		Typeface tf=Typeface.createFromAsset(manager,"fonts/ll.TTF");
		mShowMsgTextView.setTypeface(tf);
		mShowPwdTextView.setTypeface(tf);
		mShowPwdTextView.setText("");
		mShowMsgTextView.setText("");

	}

	private void performModelOperation(String operation, SpeechListener listener) {
		// 清空参数
		mVerifier.setParameter(SpeechConstant.PARAMS, null);
		mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);

		// 设置auth_id，不能设置为空
		mVerifier.sendRequest(operation, mAuthId, listener);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.isv_getpassword:
			// 获取密码之前先终止之前的注册或验证过程

			mVerifier.cancel();
			initTextView();
			YoYo.with(Techniques.Swing)
					.duration(700)
					.playOn(findViewById(R.id.isv_getpassword));
			// 清空参数
			mVerifier.setParameter(SpeechConstant.PARAMS, null);
			mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);
			mVerifier.getPasswordList(mPwdListenter);
			if(judge == 1) {
				showTip("获取成功,请先注册");
			}
			if(judge == 2){
				showTip("获取成功,请进入验证");
			}
			break;
		case R.id.isv_register:
			// 清空参数
			YoYo.with(Techniques.Swing)
					.duration(700)
					.playOn(findViewById(R.id.isv_register));
			mVerifier.setParameter(SpeechConstant.PARAMS, null);
			mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
					Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/test.pcm");
			 if (mPwdType == PWD_TYPE_NUM) {
				// 数字密码注册需要传入密码
				if (TextUtils.isEmpty(mNumPwd)) {
					showTip("请获取密码后进行操作");
					return;
				}
				mVerifier.setParameter(SpeechConstant.ISV_PWD, mNumPwd);
				mVerifier.setParameter(SpeechConstant.ISV_RGN, "2");
				((TextView) findViewById(R.id.showPwd)).setText(
						mNumPwd.substring(0, 8));
				mShowMsgTextView.setText("剩余1遍");
				 YoYo.with(Techniques.DropOut)
						 .duration(700)
						 .playOn(findViewById(R.id.showPwd));
				 YoYo.with(Techniques.DropOut)
						 .duration(700)
						 .playOn(findViewById(R.id.showMsg));
			}
			// 设置auth_id，不能设置为空
			mVerifier.setParameter(SpeechConstant.AUTH_ID, mAuthId);
			// 设置业务类型为注册
			mVerifier.setParameter(SpeechConstant.ISV_SST, "train");
			// 设置声纹密码类型
			mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);
			// 开始注册
			mVerifier.startListening(mRegisterListener);
			break;
		case R.id.isv_verify:
			// 清空提示信息
			((TextView) findViewById(R.id.showMsg)).setText("");
			// 清空参数
			YoYo.with(Techniques.Swing)
					.duration(700)
					.playOn(findViewById(R.id.isv_verify));
			mVerifier.setParameter(SpeechConstant.PARAMS, null);
			mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
					Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/verify.pcm");
			mVerifier = SpeakerVerifier.getVerifier();
			// 设置业务类型为验证
			mVerifier.setParameter(SpeechConstant.ISV_SST, "verify");
			 if (mPwdType == PWD_TYPE_NUM) {
				// 数字密码注册需要传入密码
				String verifyPwd = mVerifier.generatePassword(8);
				mVerifier.setParameter(SpeechConstant.ISV_PWD, verifyPwd);
				((TextView) findViewById(R.id.showPwd)).setText("请读出："
						+ verifyPwd);
				 YoYo.with(Techniques.DropOut)
						 .duration(700)
						 .playOn(findViewById(R.id.showPwd));
				 YoYo.with(Techniques.DropOut)
						 .duration(700)
						 .playOn(findViewById(R.id.showMsg));
			}
			// 设置auth_id，不能设置为空
			mVerifier.setParameter(SpeechConstant.AUTH_ID, mAuthId);
			mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);
			// 开始验证
			mVerifier.startListening(mVerifyListener);
			break;
		default:
			break;
		}
	}

	private SpeechListener mPwdListenter = new SpeechListener() {
		@Override
		public void onEvent(int eventType, Bundle params) {
		}
		@Override
		public void onBufferReceived(byte[] buffer) {
			String result = new String(buffer);
			switch (mPwdType) {
			case PWD_TYPE_NUM:
				StringBuffer numberString = new StringBuffer();
				try {
					JSONObject object = new JSONObject(result);
					if (!object.has("num_pwd")) {
						initTextView();
						return;
					}
					JSONArray pwdArray = object.optJSONArray("num_pwd");
					numberString.append(pwdArray.get(0));
					for (int i = 1; i < pwdArray.length(); i++) {
						numberString.append("-" + pwdArray.get(i));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				mNumPwd = numberString.toString();
				mNumPwdSegs = mNumPwd.split("-");
				break;
			default:
				break;
			}
		}
		@Override
		public void onCompleted(SpeechError error) {
			if (null != error && ErrorCode.SUCCESS != error.getErrorCode()) {
				showTip("获取失败：" + error.getErrorCode());
			}
		}
	};
	
	private SpeechListener mModelOperationListener = new SpeechListener() {
		
		@Override
		public void onEvent(int eventType, Bundle params) {
		}
		
		@Override
		public void onBufferReceived(byte[] buffer) {
			
			String result = new String(buffer);
			try {
				JSONObject object = new JSONObject(result);
				String cmd = object.getString("cmd");
				int ret = object.getInt("ret");
				
				if ("del".equals(cmd)) {
					if (ret == ErrorCode.SUCCESS) {
						showTip("删除成功");
					} else if (ret == ErrorCode.MSP_ERROR_FAIL) {
						showTip("删除失败，模型不存在");
					}
				} else if ("que".equals(cmd)) {
					if (ret == ErrorCode.SUCCESS) {
						showTip("模型存在");
					} else if (ret == ErrorCode.MSP_ERROR_FAIL) {
						showTip("模型不存在");
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void onCompleted(SpeechError error) {
			
			if (null != error && ErrorCode.SUCCESS != error.getErrorCode()) {
				showTip("操作失败：" + error.getPlainDescription(true));
			}
		}
	};

	private VerifierListener mVerifyListener = new VerifierListener() {

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			showTip("当前正在说话，音量大小：" + volume);
			Log.d(TAG, "返回音频数据："+data.length);
		}

		@Override
		public void onResult(VerifierResult result) {
			mShowMsgTextView.setText(result.source);
			
			if (result.ret == 0) {
				// 验证通过
				mShowMsgTextView.setText("验证通过");
				Intent l = new Intent(IsvDemo.this,passaty.class);
				Bundle s = new Bundle();
				s.putString("id",mAuthId);
				s.putString("name",mname);
				l.putExtras(s);
				startActivity(l);
			}
			else{
				// 验证不通过
				switch (result.err) {
				case VerifierResult.MSS_ERROR_IVP_GENERAL:
					mShowMsgTextView.setText("内核异常");
					break;
				case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
					mShowMsgTextView.setText("出现截幅");
					break;
				case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
					mShowMsgTextView.setText("太多噪音");
					break;
				case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
					mShowMsgTextView.setText("录音太短");
					break;
				case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
					mShowMsgTextView.setText("验证不通过，您所读的文本不一致");
					break;
				case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
					mShowMsgTextView.setText("音量太低");
					break;
				case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
					mShowMsgTextView.setText("音频长达不到自由说的要求");
					break;
				default:
					mShowMsgTextView.setText("验证不通过");
					break;
				}
			}
		}
		// 保留方法，暂不用
		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle arg3) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			//	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			//		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			//		Log.d(TAG, "session id =" + sid);
			//	}
		}

		@Override
		public void onError(SpeechError error) {
			
			switch (error.getErrorCode()) {
			case ErrorCode.MSP_ERROR_NOT_FOUND:
				mShowMsgTextView.setText("模型不存在，请先注册");
				break;

			default:
				showTip("onError Code："	+ error.getPlainDescription(true));
				break;
			}
		}

		@Override
		public void onEndOfSpeech() {
			// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
			showTip("结束说话");
		}

		@Override
		public void onBeginOfSpeech() {
			// 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
			showTip("开始说话");
		}
	};
	
	private VerifierListener mRegisterListener = new VerifierListener() {

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			showTip("当前正在说话，音量大小：" + volume);
			Log.d(TAG, "返回音频数据："+data.length);
		}

		@Override
		public void onResult(VerifierResult result) {
			((TextView)findViewById(R.id.showMsg)).setText(result.source);
			
			if (result.ret == ErrorCode.SUCCESS) {
				switch (result.err) {
				case VerifierResult.MSS_ERROR_IVP_GENERAL:
					mShowMsgTextView.setText("内核异常");
					break;
				case VerifierResult.MSS_ERROR_IVP_EXTRA_RGN_SOPPORT:
					showTip("训练达到最大次数");
					break;
				case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
					showTip("出现截幅");
					break;
				case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
					showTip("太多噪音");
					break;
				case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
					showTip("录音太短");
					break;
				case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
					showTip("训练失败，您所读的文本不一致");
					break;
				case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
					showTip("音量太低");
					break;
				case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
					mShowMsgTextView.setText("音频长达不到自由说的要求");
				default:
					break;
				}
				
				if (result.suc == result.rgn) {
					mShowMsgTextView.setText("注册成功");
					Intent l = new Intent(IsvDemo.this,makesp.class);
					Bundle s = new Bundle();
					s.putString("id",mAuthId);
					s.putString("name",mname);
					l.putExtras(s);
					startActivity(l);
//					if (PWD_TYPE_NUM == mPwdType) {
//						mResultEditText.setText("您的数字密码声纹ID：\n" + result.vid);
//	}
				} else {

					int nowTimes = result.suc + 1;
					int leftTimes = result.rgn - nowTimes;
					 if (PWD_TYPE_NUM == mPwdType) {

						mShowPwdTextView.setText(mNumPwdSegs[nowTimes - 1]);
					}


					mShowMsgTextView.setText("剩余" + leftTimes + "遍");
				}
			}else {
				mShowMsgTextView.setText("注册失败，请重新开始。");
			}
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle arg3) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			//	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			//		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			//		Log.d(TAG, "session id =" + sid);
			//	}
		}

		@Override
		public void onError(SpeechError error) {
			
			if (error.getErrorCode() == ErrorCode.MSP_ERROR_ALREADY_EXIST) {
				showTip("模型已存在，如需重新注册，请先删除");
			} else {
				showTip("onError Code：" + error.getPlainDescription(true));
			}
		}

		@Override
		public void onEndOfSpeech() {
			showTip("结束说话");
		}

		@Override
		public void onBeginOfSpeech() {
			showTip("开始说话");
		}
	};
	
	@Override
	public void finish() {
		if (null != mTextPwdSelectDialog) {
			mTextPwdSelectDialog.dismiss();
		}
		super.finish();
	}
	
	@Override
	protected void onDestroy() {		
		if (null != mVerifier) {
			mVerifier.stopListening();
			mVerifier.destroy();
		}
		super.onDestroy();
	}

	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}
	
}
