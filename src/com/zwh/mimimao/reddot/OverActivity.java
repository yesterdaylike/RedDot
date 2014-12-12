package com.zwh.mimimao.reddot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class OverActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener{

	private TextView messageTextView;
	private TextView summayTextView;

	private AdView adView;
	private static String BANNER_AD_UNIT_ID = "ca-app-pub-4026226502733510/7646771386";

	//GoogleApiClient mGoogleApiClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create the Google Api Client with access to Plus and Games
		/*mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
		.addApi(Games.API).addScope(Games.SCOPE_GAMES)
		.build();
		Log.e("zhengwenhui", "GoogleApiClient Builder");*/

		setContentView(R.layout.over);

		LinearLayout adLayout = (LinearLayout) findViewById(R.id.ad_layout);

		adView = new AdView(this);
		adView.setAdUnitId(BANNER_AD_UNIT_ID);
		adView.setAdSize(AdSize.SMART_BANNER );

		adLayout.addView(adView);

		// 启动一般性请求。
		AdRequest adRequest = new AdRequest.Builder().build();

		/*AdRequest adRequest = new AdRequest.Builder()
		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // 模拟器
		.addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4") // 我的Galaxy Nexus测试手机
		.build();*/

		// 在adView中加载广告请求。
		adView.loadAd(adRequest);

		messageTextView = (TextView)findViewById(R.id.message);
		summayTextView = (TextView)findViewById(R.id.summay);

		Intent intent = getIntent();
		boolean success = intent.getBooleanExtra(MainActivity.SUCCESS_FAIL, false);
		int steps = intent.getIntExtra(MainActivity.STEP, 0);

		if( success ){
			messageTextView.setText(R.string.congratulations);
			summayTextView.setText(String.valueOf(steps));
			summayTextView.append(getString(R.string.captured));
		}else{
			messageTextView.setText(R.string.game_over);
			summayTextView.setText(R.string.slipped);
		}
	}

	public void onClickButton(View view){
		Sound.palySound(this, Sound.BTNPRESS);

		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
		view.startAnimation(hyperspaceJumpAnimation);

		switch (view.getId()) {
		case R.id.try_again:
			tryAgain();
			break;
		case R.id.share:
			shareImageAndText("I found a fun game!");
			break;
		case R.id.leader_board:
			startLeaderBoard();
			break;
		case R.id.rate:
			rate();
			break;

		default:
			break;
		}
	}

	private void startLeaderBoard(){
		/*startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
		        LEADERBOARD_ID), REQUEST_LEADERBOARD);*/
	}

	private void rate(){
		//String mAddress = "market://details?id=" + getPackageName(); 
		String mAddress = "https://play.google.com/store/apps/details?id=com.zwh.mimimao.reddot"; 
		Intent marketIntent = new Intent("android.intent.action.VIEW");  
		marketIntent.setData(Uri.parse(mAddress ));
		startActivity(marketIntent );
	}

	private void tryAgain(){
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
	}

	private void shareImageAndText(String text){
		String sdcardPath = Environment.getExternalStorageDirectory().getPath();
		FileCopyFromAssetsToSD(this, "message.png");

		Intent intent = new Intent();
		//intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI"));
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("image/png");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.putExtra("Kdescription", text);
		intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+sdcardPath+"/message.png"));
		intent.putExtra(Intent.EXTRA_TEXT, text);
		startActivity(intent);
	}

	public void FileCopyFromAssetsToSD(Context context, String fileName){
		int BUFFER_LEN = 1024;
		AssetManager assetManager = context.getAssets();
		InputStream is;
		FileOutputStream fos;

		try {
			is = assetManager.open(fileName);

			File out = new File(Environment.getExternalStorageDirectory(), fileName);
			byte[] buffer = new byte[BUFFER_LEN];
			fos = new FileOutputStream(out);
			int read = 0;

			while ((read = is.read(buffer, 0, BUFFER_LEN)) >= 0) {
				fos.write(buffer, 0, read);
			}

			fos.flush();
			fos.close();
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		//mGoogleApiClient.connect();
	}

	@Override
	public void onResume() {
		super.onResume();
		adView.resume();
	}

	@Override
	public void onPause() {
		adView.pause();
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		//mGoogleApiClient.disconnect();
	}

	@Override
	public void onDestroy() {
		adView.destroy();
		super.onDestroy();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		Log.v("zhengwenhui", "GoogleApiClient onConnectionFailed");
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		Log.v("zhengwenhui", "GoogleApiClient onConnected");
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		Log.v("zhengwenhui", "GoogleApiClient onConnectionSuspended");
	}
}
