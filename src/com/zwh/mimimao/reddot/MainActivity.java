package com.zwh.mimimao.reddot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends Activity implements ActionInterface{
	private ReseauView mReseauView;
	private DotView mRedDotView;
	private int REQUESTCODE = 93;

	private TextView stepsTextView;
	private TextView bestScoreTextView;

	private int bestStep = 0;

	static String SHARED_PREFERENCES_REDDOT = "SHARED_PREFERENCES_REDDOT";
	static String BEST_STEP = "BEST_STEP";
	static String SUCCESS_FAIL = "SUCCESS_FAIL";
	static String STEP = "STEP";
	private SharedPreferences sharedPreferences;
	private Editor prefsPrivateEditor;

	private static InterstitialAd interstitial;
	private static String INTERSTITIALAD_AD_UNIT_ID = "ca-app-pub-4026226502733510/1240310985";

	private static int countSuccess = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play);

		firstInsert();

		stepsTextView = (TextView) findViewById(R.id.steps);
		bestScoreTextView = (TextView) findViewById(R.id.best_score);

		mRedDotView = (DotView) findViewById(R.id.red_dot);
		mReseauView = (ReseauView) findViewById(R.id.reseau_view);
		mReseauView.setActionInterface(this);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mReseauView.initPositionlist(dm.widthPixels, dm.heightPixels);

		mReseauView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Sound.palySound(MainActivity.this, Sound.MOVE);
					mReseauView.checkTouchPostion(event.getX(), event.getY());
					break;

				default:
					break;
				}

				return false;
			}
		});

		if( savedInstanceState != null ){
			int s = savedInstanceState.getInt("red_dot_step");
			int[] position = savedInstanceState.getIntArray("red_dot_cur_postion");
			boolean[] reseau = savedInstanceState.getBooleanArray("red_dot_reseau");

			mReseauView.setStep(s);
			mReseauView.setCurPostion(position[0], position[1]);
			mReseauView.setReseau(reseau);
		}

		sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_REDDOT, Context.MODE_PRIVATE);  
		prefsPrivateEditor = sharedPreferences.edit();
		initSteps();

		// 制作插页式广告。
		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId(INTERSTITIALAD_AD_UNIT_ID);

		// 创建广告请求。
		AdRequest interstitialAdRequest = new AdRequest.Builder().build();

		// 开始加载插页式广告。
		interstitial.loadAd(interstitialAdRequest);
		interstitial.setAdListener(adListener);
	}

	AdListener adListener = new AdListener() {

		@Override
		public void onAdClosed() {
			// TODO Auto-generated method stub
			Log.i("zhengwenhui", "onAdClosed");
			super.onAdClosed();
			AdRequest interstitialAdRequest = new AdRequest.Builder().build();
			interstitial.loadAd(interstitialAdRequest);
			startActivity();
		}

		@Override
		public void onAdFailedToLoad(int errorCode) {
			// TODO Auto-generated method stub
			Log.i("zhengwenhui", "onAdFailedToLoad");
			super.onAdFailedToLoad(errorCode);
		}

		@Override
		public void onAdLeftApplication() {
			// TODO Auto-generated method stub
			Log.i("zhengwenhui", "onAdLeftApplication");
			super.onAdLeftApplication();
		}

		@Override
		public void onAdLoaded() {
			// TODO Auto-generated method stub
			Log.i("zhengwenhui", "onAdLoaded");
			super.onAdLoaded();
		}

		@Override
		public void onAdOpened() {
			// TODO Auto-generated method stub
			Log.i("zhengwenhui", "onAdOpened");
			super.onAdOpened();
		}
	};

	@Override
	public void gameOver(boolean success, int step) {
		// TODO Auto-generated method stub

		if( success && ( bestStep == 0 || step < bestStep ) ){
			bestStep = step;
			bestScoreTextView.setText(R.string.best_score);
			bestScoreTextView.append(String.valueOf(bestStep));

			prefsPrivateEditor.putInt(BEST_STEP, step);
			prefsPrivateEditor.commit();
			//Games.Leaderboards.submitScore(mGoogleApiClient, LEADERBOARD_ID, bestStep);
		}

		mStep = step;
		mSuccess = success;

		if (success) {
			Sound.palySound(MainActivity.this, Sound.SUCCESS);
		}
		else{
			Sound.palySound(MainActivity.this, Sound.FAIL);
		}

		countSuccess++;
		if( countSuccess % 3 == 0 && interstitial.isLoaded() ){
			interstitial.show();
		}
		else{
			haderStartActivity();
		}
	}

	private int mStep;
	private boolean mSuccess;

	private void haderStartActivity(){
		final Handler handler=new Handler();
		Runnable runnable = new Runnable()
		{
			public void run() 
			{
				startActivity();
				handler.removeCallbacks(this);
			}
		};

		handler.postDelayed(runnable, 500);
	}

	private void startActivity(){
		Intent intent = new Intent(this, OverActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		intent.putExtra(STEP, mStep);
		intent.putExtra(SUCCESS_FAIL, mSuccess);
		startActivityForResult(intent, REQUESTCODE);
	}

	private boolean start = true;

	@Override
	public void moveView( int x,  int y,  int radius) {
		// TODO Auto-generated method stub
		if(start){
			mRedDotView.setPosition(x, y, radius - 2);
			start = false;
		}else{
			mRedDotView.catMove(x, y, radius - 2);
		}

		stepsTextView.setText(R.string.steps);
		stepsTextView.append(String.valueOf(mReseauView.getStep()));
	}

	private void initSteps(){
		bestStep = sharedPreferences.getInt(BEST_STEP, 0);

		bestScoreTextView.setText(R.string.best_score);
		bestScoreTextView.append(String.valueOf(bestStep));

		stepsTextView.setText(R.string.steps);
		stepsTextView.append(String.valueOf(mReseauView.getStep()));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUESTCODE) {
			if (resultCode == RESULT_OK) {
				mReseauView.reStart();
				start = true;
				initSteps();
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save the user's current game state
		savedInstanceState.putInt("red_dot_step", mReseauView.getStep());
		savedInstanceState.putIntArray("red_dot_cur_postion", mReseauView.getCurPostion());
		savedInstanceState.putBooleanArray("red_dot_reseau", mReseauView.getReseau());
		super.onSaveInstanceState(savedInstanceState);
	}

	private void firstInsert(){
		SharedPreferences setting = getSharedPreferences("red_dot", 0);
		Boolean user_first = setting.getBoolean("FIRST",true);
		if(user_first){
			setting.edit().putBoolean("FIRST", false).commit();
			showHelpDialog();
		}
	}

	private void showHelpDialog(){
		AlertDialog.Builder builder = new Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		builder.setPositiveButton(android.R.string.ok, null);

		ImageView imageView = new ImageView(this);
		builder.setView(imageView);

		imageView.setImageResource(R.drawable.help_animation);
		builder.create().show();

		AnimationDrawable frameAnimation = (AnimationDrawable)imageView.getDrawable();
		frameAnimation.setVisible(true, true);
		frameAnimation.start();
	}
}
