package com.iflytek.facedemo.main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.FaceRequest;
import com.iflytek.cloud.RequestListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.facedemo.R;
import com.iflytek.facedemo.util.FaceUtil;

public class FaceDemo extends Activity implements OnClickListener {
	private final int REQUEST_PICTURE_CHOOSE = 1;
	private final int REQUEST_CAMERA_IMAGE = 2;
	private Bitmap mImage = null;
	private byte[] mImageData = null;
	// authid为6-18个字符长度，用于唯一标识用户
	private String mAuthid = null;
	private int panduan;
	private Toast mToast;
	// 进度对话框
	private Button verify;
	private Button register;
	private String mAuthname;
	private ProgressDialog mProDialog;
	// 拍照得到的照片文件
	private File mPictureFile;
	// FaceRequest对象，集成了人脸识别的各种功能
	private FaceRequest mFaceRequest;
	@SuppressLint("ShowToast")

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent i = getIntent();
		Bundle date = i.getExtras();
		mAuthid = date.getString("id");
		mAuthname = date.getString("name");
		panduan = date.getInt("pd");
		if(panduan == 1){
			setContentView(R.layout.activity_face_zhuce);
		}
		if(panduan == 2){
			setContentView(R.layout.activity_face_yanzheng);
		}

		SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));
		if(panduan == 1) {
			register = (Button) findViewById(R.id.online_reg);
			register.setOnClickListener(FaceDemo.this);
		}
		if(panduan == 2) {
			verify = (Button) findViewById(R.id.online_verify);
			verify.setOnClickListener(FaceDemo.this);
		}
		findViewById(R.id.online_pick).setOnClickListener(FaceDemo.this);
		findViewById(R.id.online_camera).setOnClickListener(FaceDemo.this);
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		mProDialog = new ProgressDialog(this);
		mProDialog.setCancelable(true);
		mProDialog.setTitle("请稍后");
		
		mProDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// cancel进度框时,取消正在进行的操作
				if (null != mFaceRequest) {
					mFaceRequest.cancel();
				}
			}
		});
		
		mFaceRequest = new FaceRequest(this);
	}

	private void register(JSONObject obj) throws JSONException {
		int ret = obj.getInt("ret");
		if (ret != 0) {
			showTip("注册失败");
			return;
		}
		if ("success".equals(obj.get("rst"))) {
			showTip("注册成功");
			Intent z = new Intent(FaceDemo.this,IsvDemo.class);
			Bundle s = new Bundle();
			s.putString("id",mAuthid);
			s.putString("name", mAuthname);
			s.putInt("pd",panduan);
			z.putExtras(s);
			startActivity(z);
		} else {
			showTip("注册失败");
		}
	}

	private void verify(JSONObject obj) throws JSONException {
		int ret = obj.getInt("ret");
		if (ret != 0) {
			showTip("验证失败");
			return;
		}
		if ("success".equals(obj.get("rst"))) {
			if (obj.getBoolean("verf")) {
				showTip("人脸识别通过，请进行下一步操作");
                Intent z = new Intent(FaceDemo.this,IsvDemo.class);
				Bundle s = new Bundle();
				s.putString("id",mAuthid);
				s.putString("name", mAuthname);
				s.putInt("pd",panduan);
				z.putExtras(s);
				startActivity(z);
			} else {
				showTip("验证不通过");
			}
		} else {
			showTip("验证失败");
		}
	}
	
	private RequestListener mRequestListener = new RequestListener() {

		@Override
		public void onEvent(int eventType, Bundle params) {
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
			if (null != mProDialog) {
				mProDialog.dismiss();
			}

			try {
				String result = new String(buffer, "utf-8");
				Log.d("FaceDemo", result);
				
				JSONObject object = new JSONObject(result);
				String type = object.optString("sst");
				if ("reg".equals(type)) {
					register(object);
				} else if ("verify".equals(type)) {
					verify(object);
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO: handle exception
			}
		}
		@Override
		public void onCompleted(SpeechError error) {
			if (null != mProDialog) {
				mProDialog.dismiss();
			}
			if (error != null) {
				switch (error.getErrorCode()) {
				case ErrorCode.MSP_ERROR_ALREADY_EXIST:
					showTip("authid已经被注册，请更换后再试");

					break;
				default:
					showTip(error.getPlainDescription(true));
					break;
				}
			}
		}
	};

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

		case R.id.online_pick:
			YoYo.with(Techniques.Swing)
					.duration(700)
					.playOn(findViewById(R.id.online_pick));
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_PICK);
			startActivityForResult(intent, REQUEST_PICTURE_CHOOSE);
			break;

		case R.id.online_reg:
			YoYo.with(Techniques.Swing)
					.duration(700)
					.playOn(findViewById(R.id.online_reg));
			if (TextUtils.isEmpty(mAuthid)) {
				showTip("authid不能为空");
				return;
			}
			
			if (null != mImageData) {
				mProDialog.setMessage("注册中...");
				mProDialog.show();
				// 设置用户标识，格式为6-18个字符（由字母、数字、下划线组成，不得以数字开头，不能包含空格）。
				// 当不设置时，云端将使用用户设备的设备ID来标识终端用户。
				mFaceRequest.setParameter(SpeechConstant.AUTH_ID, mAuthid);
				mFaceRequest.setParameter(SpeechConstant.WFR_SST, "reg");
				mFaceRequest.sendRequest(mImageData, mRequestListener);
			} else {
				showTip("请选择图片后再注册");
			}
			break;

		case R.id.online_verify:
			YoYo.with(Techniques.Swing)
					.duration(700)
					.playOn(findViewById(R.id.online_verify));
			if (TextUtils.isEmpty(mAuthid)) {
				showTip("authid不能为空");
				return;
			} 
			
			if (null != mImageData) {
				mProDialog.setMessage("验证中...");
				mProDialog.show();
				// 设置用户标识，格式为6-18个字符（由字母、数字、下划线组成，不得以数字开头，不能包含空格）。
				// 当不设置时，云端将使用用户设备的设备ID来标识终端用户。
				mFaceRequest.setParameter(SpeechConstant.AUTH_ID, mAuthid);
				mFaceRequest.setParameter(SpeechConstant.WFR_SST, "verify");
				mFaceRequest.sendRequest(mImageData, mRequestListener);
			} else {
				showTip("请选择图片后再验证");
			}
			break;
		case R.id.online_camera:
			// 设置相机拍照后照片保存路径
			YoYo.with(Techniques.Swing)
					.duration(700)
					.playOn(findViewById(R.id.online_camera));
			mPictureFile = new File(Environment.getExternalStorageDirectory(), 
					"picture" + System.currentTimeMillis()/1000 + ".jpg");
			// 启动拍照,并保存到临时文件
			Intent mIntent = new Intent();
			mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			mIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPictureFile));
			mIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			startActivityForResult(mIntent, REQUEST_CAMERA_IMAGE);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		String fileSrc = null;
		if (requestCode == REQUEST_PICTURE_CHOOSE) {
			if ("file".equals(data.getData().getScheme())) {
				// 有些低版本机型返回的Uri模式为file
				fileSrc = data.getData().getPath();
			} else {
				// Uri模型为content
				String[] proj = {MediaStore.Images.Media.DATA};
				Cursor cursor = getContentResolver().query(data.getData(), proj,
						null, null, null);
				cursor.moveToFirst();
				int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				fileSrc = cursor.getString(idx);
				cursor.close();
			}
			// 跳转到图片裁剪页面
			FaceUtil.cropPicture(this,Uri.fromFile(new File(fileSrc)));
		} else if (requestCode == REQUEST_CAMERA_IMAGE) {
			if (null == mPictureFile) {
				showTip("拍照失败，请重试");
				return;
			}
			fileSrc = mPictureFile.getAbsolutePath();
			updateGallery(fileSrc);
			// 跳转到图片裁剪页面
			FaceUtil.cropPicture(this,Uri.fromFile(new File(fileSrc)));
		} else if (requestCode == FaceUtil.REQUEST_CROP_IMAGE) {
			// 获取返回数据
			Bitmap bmp = data.getParcelableExtra("data");
			// 若返回数据不为null，保存至本地，防止裁剪时未能正常保存
			if(null != bmp){
				FaceUtil.saveBitmapToFile(FaceDemo.this, bmp);
			}
			// 获取图片保存路径
			fileSrc = FaceUtil.getImagePath(FaceDemo.this);
			// 获取图片的宽和高
			Options options = new Options();
			options.inJustDecodeBounds = true;
			mImage = BitmapFactory.decodeFile(fileSrc, options);
			// 压缩图片
			options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
					(double) options.outWidth / 1024f,
					(double) options.outHeight / 1024f)));
			options.inJustDecodeBounds = false;
			mImage = BitmapFactory.decodeFile(fileSrc, options);
			// 若mImageBitmap为空则图片信息不能正常获取
			if(null == mImage) {
				showTip("图片信息无法正常获取！");
				return;
			}
			// 部分手机会对图片做旋转，这里检测旋转角度
			int degree = FaceUtil.readPictureDegree(fileSrc);
			if (degree != 0) {
				// 把图片旋转为正的方向
				mImage = FaceUtil.rotateImage(degree, mImage);
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			//可根据流量及网络状况对图片进行压缩
			mImage.compress(Bitmap.CompressFormat.JPEG, 80, baos);
			mImageData = baos.toByteArray();
			((ImageView) findViewById(R.id.online_img)).setImageBitmap(mImage);
		}
		
	}

	@Override
	public void finish() {
		if (null != mProDialog) {
			mProDialog.dismiss();
		}
		super.finish();
	}
	private void updateGallery(String filename) {
		MediaScannerConnection.scanFile(this, new String[] {filename}, null,
				new MediaScannerConnection.OnScanCompletedListener() {
					
					@Override
					public void onScanCompleted(String path, Uri uri) {

					}
				});
	}
	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}
}
