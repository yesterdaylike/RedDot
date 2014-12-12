package com.zwh.mimimao.reddot;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ReseauView extends View {

	private Paint paint;
	private Paint paintGray;

	private int SIZE = 10;
	private int diameter;
	private int radius;

	private Random random;

	private int[] positionWidthOdd;
	private int[] positionWidthEven;
	private int[] positionHeight;

	private boolean[][] reseau;

	private Position curPostion;
	private ActionInterface mActionInterface;

	private int step = 0;
	
	boolean[] getReseau(){
		boolean [] result = new boolean[100];
		int i = 0;
		for (boolean[] line : reseau) {
			for (boolean b : line) {
				result[ i++ ] = b;
			}
		}

		return result;
	}

	void setReseau( boolean[] res ){

		for (int i = 0, k = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++, k++) {
				reseau[i][j] = res[k];
			}
		}
	}

	int[] getCurPostion(){
		return new int[]{curPostion.h, curPostion.w};
	}

	void setCurPostion(int h, int w){
		curPostion.h = h;
		curPostion.w = w;
	}

	int getStep(){
		return step;
	}

	void setStep( int s){
		step = s;
	}

	private int surOdd[][] = {
			{0,-1},
			{-1,-1},
			{-1,0},
			{-1,1},
			{0,1},
			{1,0},
	};

	private int surEven[][] = {
			{1,-1},
			{0,-1},
			{-1,0},
			{0,1},
			{1,1},
			{1,0},
	};

	public ReseauView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}

	private void initPaint(){
		paint=new Paint();
		paint.setAntiAlias(true); 
		paint.setDither(true);
		paint.setStyle( Paint.Style.STROKE );
		paint.setColor(Color.GRAY);

		paintGray=new Paint();
		paintGray.setAntiAlias(true); 
		paintGray.setDither(true);
		paintGray.setColor(0xffFFBD21);
	}

	void initPositionlist(int width, int height){
		//int width = getWidth();
		//int height = getHeight();
		
		positionWidthOdd = new int[SIZE];
		positionWidthEven = new int[SIZE];
		positionHeight = new int[SIZE];

		float heightDiameter;
		float heightTemp;

		if( width > height ){
			diameter = (int) (height / ( SIZE * 0.866f ) ) ;
			heightDiameter = diameter * 0.866f;
			radius = diameter >> 1;

		int blankLeaving = ( width - height ) / 2;
		int temp = - (diameter >> 2);
		heightTemp = temp;

		for(int i = 0; i < SIZE; i++){
			temp += diameter;
			heightTemp += heightDiameter;
			positionWidthOdd[i] = temp + blankLeaving;
			positionWidthEven[i] = temp + radius + blankLeaving;
			positionHeight[i] =  (int) heightTemp;
		}
		}
		else{
			diameter = width / SIZE;
			heightDiameter = diameter * 0.866f;
			radius = diameter >> 1;

		int blankLeaving = height - width;
		int temp = - (diameter >> 2);
		heightTemp = temp;

		for(int i = 0; i < SIZE; i++){
			temp += diameter;
			heightTemp += heightDiameter;
			positionWidthOdd[i] = temp;
			positionWidthEven[i] = temp + radius;
			positionHeight[i] =  (int) (heightTemp + blankLeaving);
		}
		}
		//mActionInterface.initView(diameter);
		initReseau();
	}

	private void initReseau(){
		random = new Random();

		reseau = new boolean[SIZE][SIZE];
		for(int i = 0; i < SIZE; i++){
			for (int j = 0; j < SIZE; j++) {
				reseau[i][j] = false;
				int ran = random.nextInt(5);
				if(ran == 1){
					reseau[i][j] = true;
				}
			}
		}

		curPostion = new Position(4, 4, null);
		reseau[4][4] = false;
	}

	public void reStart(){
		for(int i = 0; i < SIZE; i++){
			for (int j = 0; j < SIZE; j++) {
				reseau[i][j] = false;
				int ran = random.nextInt(5);
				if(ran == 1){
					reseau[i][j] = true;
				}
			}
		}

		curPostion = new Position(4, 4, null);
		reseau[4][4] = false;

		step = 0;

		this.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		/*if( null == positionWidthOdd ){
			initPositionlist();
		}*/

		int tempWidth;
		for (int w = 0; w < SIZE - 1; w++) {
			for (int h = 0; h < SIZE - 1; h++) {

				tempWidth = h%2==0? positionWidthEven[w] : positionWidthOdd[w];

				if(reseau[w][h]){
					canvas.drawCircle(tempWidth, positionHeight[h], radius-3, paintGray);
				}else{
					canvas.drawCircle(tempWidth, positionHeight[h], radius-3, paint);
				}
			}
		}

		tempWidth = curPostion.h%2==0? positionWidthEven[curPostion.w] : positionWidthOdd[curPostion.w];
		//canvas.drawCircle(tempWidth, positionHeight[curPostion.h], 3, paint);

		mActionInterface.moveView(tempWidth, positionHeight[curPostion.h], radius);
	}

	public int checkTouchPostion(float x, float y){
		int w, h;
		x += radius;
		y += radius;

		for ( h = 0; h < positionHeight.length; h++) {
			if(y < positionHeight[h]){
				h--;
				break;
			}
		}

		int []tempWidth = h%2==0? positionWidthEven : positionWidthOdd;

		for ( w = 0; w < tempWidth.length; w++) {
			if(x < tempWidth[w]){
				w--;
				break;
			}
		}

		if( w >= 0 && w < SIZE-1 && h >= 0 && h < SIZE-1 && !reseau[w][h] ){
			reseau[w][h] = true;
			catMove();
			this.invalidate();

			if( curPostion.w == 0 || curPostion.w == SIZE-2 || curPostion.h == 0 || curPostion.h == SIZE-2){
				mActionInterface.gameOver(false, step);
			}
		}
		return SIZE;
	}

	private Position getPath(int w, int h){
		//Stack<Position> stack = new Stack<Position>();
		Queue<Position> queue = new LinkedList<Position>();
		int[][] sur;
		int mw, mh;
		boolean checked[][] = new boolean[SIZE-1][SIZE-1];

		for(int i = 0; i < SIZE-1; i++){
			for (int j = 0; j < SIZE-1; j++) {
				checked[i][j] = false;
			}
		}

		Position postion = new Position(w, h, null);
		Position temp;
		queue.offer(postion);
		checked[w][h] = true;

		while (!queue.isEmpty()) {
			postion = queue.poll();
			sur = postion.h%2==0 ? surEven : surOdd;

			for (int[] is : sur) {
				mw = postion.w + is[0];
				mh = postion.h + is[1];

				if( mw < 0 || mw >= SIZE-1 || mh < 0 || mh >= SIZE-1){
					//到边界了
					return postion;
				}

				if(!reseau[mw][mh] && !checked[mw][mh]){
					//这不是墙  已经检测过了
					temp = new Position(mw, mh, postion);
					queue.offer(temp);
					checked[mw][mh] = true;
				}
			}
		}
		return null;
	}

	private void catMove(){
		Position pos = getPath(curPostion.w, curPostion.h);
		step ++;
		if( pos == null ){
			mActionInterface.gameOver(true, step);
			return;
		}

		while( pos.parent != null){
			if( pos.parent.w == curPostion.w && pos.parent.h == curPostion.h ){
				curPostion = pos;
				break;
			}
			pos = pos.parent;
		}

		/*if( curPostion.w == 0 || curPostion.w == SIZE-2 || curPostion.h == 0 || curPostion.h == SIZE-2){
			mActionInterface.gameOver(false, step);
		}*/
	}

	public class Position{
		int w;
		int h;
		Position parent;

		Position(int x, int y, Position p){
			this.w = x;
			this.h = y;
			this.parent = p;
		}
	}

	void setActionInterface(ActionInterface mAI){
		mActionInterface = mAI;
	}
}