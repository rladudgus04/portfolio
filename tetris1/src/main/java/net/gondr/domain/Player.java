package net.gondr.domain;

import java.util.Random;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import net.gondr.tetris1.App;

public class Player {
	private Point2D[][][] shape = new Point2D[7][][];
	private int current = 0;
	private int rotate = 0;
	private int nowColor = 0;
	
	private int next = 0; //다음모양
	private int nextColor = 0; //다음 색상
	
	private Color[] colorSet = new Color[7];
	
	private Random rnd;
	
	private int x = 5, y = 2;
	private Block[][] board;
	
	public Player(Block[][] board) {
		this.board = board;
		
		//작대기
		shape[0] = new Point2D[2][];
		shape[0][0] = getPointArray("0,-1:0,0:0,1:0,2");
		shape[0][1] = getPointArray("-1,0:0,0:1,0:2,0");
		//네모
		shape[1] = new Point2D[1][];
		shape[1][0] = getPointArray("0,0:1,0:0,1:1,1");
		//ㄴ모양
		shape[2] = new Point2D[4][];
		shape[2][0] = getPointArray("0,-2:0,-1:0,0:1,0");
		shape[2][1] = getPointArray("0,0:1,0:2,0:0,1");
		shape[2][2] = getPointArray("-1,0:0,0:0,1:0,2");
		shape[2][3] = getPointArray("-2,0:-1,0:0,0:0,-1");
		// 역 ㄴ 모양
		shape[3] = new Point2D[4][];
		shape[3][0] = getPointArray("0,-2:0,-1:0,0:-1,0");
		shape[3][1] = getPointArray("0,-1:0,0:1,0:2,0");
		shape[3][2] = getPointArray("0,0:1,0:0,1:0,2");
		shape[3][3] = getPointArray("-2,0:-1,0:0,0:0,1");
		
		shape[4] = new Point2D[2][];
		shape[4][0] = getPointArray("0,0:0,-1:1,-1:-1,0");
		shape[4][1] = getPointArray("0,0:1,0:1,1:0,-1");
		
		// ─┐_ 모양 역지렁이 모양
		shape[5] = new Point2D[2][];
		shape[5][0] = getPointArray("0,0:0,-1:-1,-1:1,0");
		shape[5][1] = getPointArray("0,0:0,-1:-1,0:-1,1");
		
		// ㅗ 모양
		shape[6] = new Point2D[4][];
		shape[6][0] = getPointArray("0,0:0,-1:1,0:-1,0");
		shape[6][1] = getPointArray("0,0:0,-1:1,0:0,1");
		shape[6][2] = getPointArray("0,0:1,0:-1,0:0,1");
		shape[6][3] = getPointArray("0,0:-1,0:0,-1:0,1");
		

		colorSet[0] = Color.ALICEBLUE;
		colorSet[1] = Color.AQUAMARINE;
		colorSet[2] = Color.BEIGE;
		colorSet[3] = Color.BLUEVIOLET;
		colorSet[4] = Color.CORAL;
		colorSet[5] = Color.CRIMSON;
		colorSet[6] = Color.DODGERBLUE;
		
		rnd = new Random();
		current = rnd.nextInt(shape.length); //랜덤한 블럭을 뽑고
		nowColor = current;
		
		next = rnd.nextInt(shape.length); //다음 블럭 뽑고
		nextColor = next;
		
		draw(false); //이 줄을 추가하고
	}

	//remove값이 true이면 화면에서 지우고 false이면 화면에 그려준다.
	private void draw(boolean remove) {
		for(int i = 0; i < shape[current][rotate].length; i++) {
			int bx = (int)shape[current][rotate][i].getX() + x;
			int by = (int)shape[current][rotate][i].getY() + y;
			board[by][bx].setData(!remove, colorSet[nowColor]); //제거나 색칠이냐
		}
	}
	
	private Point2D[] getPointArray(String pointStr) {
		//0,-1:0,0:0,1:0,2  작대기 블럭
		//0  1    0,0   0,1   0,2
		Point2D[] arr = new Point2D[4];
		String[] pointList = pointStr.split(":");  //콜론을 기준으로 점들을 나누고
		for(int i = 0; i < pointList.length; i++) {
			String[] point = pointList[i].split(","); //컴마를 기준으로 x,y나누고
			double x = Double.parseDouble(point[0]);
			double y = Double.parseDouble(point[1]);
			arr[i] = new Point2D(x, y);
		}
		return arr;
	}
	
	public void keyHandler(KeyEvent e) {
		int dx = 0, dy = 0; //이동 거리
		boolean rot = false; //회전여부
		if(e.getCode() == KeyCode.LEFT) {
			dx -= 1;
		}else if(e.getCode() == KeyCode.RIGHT) {
			dx += 1;
		}else if(e.getCode() == KeyCode.UP) {
			rot = true;
		}
		
		move(dx, dy, rot);
		
		if(e.getCode() == KeyCode.DOWN) {
			down();
		}else if(e.getCode() == KeyCode.SPACE) {
			while(!down()) {
				//do nothing;
			}
		}
		
	}
	
	private void move(int dx, int dy, boolean rot) {
		draw(true);
		x += dx;
		y += dy;
		if(rot) {
			rotate = (rotate + 1) % shape[current].length;
		}
		if(!checkPossible()) { //이동이 불가능함.
			x -= dx;
			y -= dy;
			if(rot) {
				rotate = rotate -1 < 0 ? shape[current].length - 1 : rotate -1;
			}
		}
		draw(false);
	} 
	
	public boolean down() {
		draw(true);
		y += 1;
		if(!checkPossible()) {
			y -= 1;
			draw(false);
			App.app.game.checkLineStatus(); // 해당 줄이 꽉찼는지 검사해
			getNextBlock();//다음 블럭 가져오고
			draw(false);
			return true;
		}
		draw(false);
		return false;
	}
	
	private void getNextBlock() {
		current = next;
		nowColor = nextColor;
		next = rnd.nextInt(shape.length);
		nextColor = next;
		
		x = 5;
		y = 2;
		rotate = 0;
		
		if(!checkPossible()) {
			App.app.game.setGameOver();
		}
	}
	
	private boolean checkPossible() {
		//블럭의 이동이 가능한지를 체크하는 매서드
		Point2D[] nb = shape[current][rotate];
		for(int i = 0; i < nb.length; i++) {
			int bx = (int)nb[i].getX() + x;
			int by = (int)nb[i].getY() + y;
			if(bx < 0 || by < 0 || bx >= 10 || by >= 20) {
				return false;
			}
			
			if(board[by][bx].getFill()) {
				return false;
			}
		}
		return true;
	}
	
	public void render(GraphicsContext gc, double width, double height) {
		Color color = colorSet[nextColor];
		Point2D[] block = shape[next][0];
		
		gc.clearRect(0, 0, width, height);
		double x = width / 2;
		double y = height / 2;
		double size = width / 4 - 10;
		if(next == 0) {
			y -= size;
		}
		
		for(int i = 0; i < block.length; i++) {
			double dx = x + block[i].getX() * size;
			double dy = y + block[i].getY() * size;
			gc.setFill(color.darker());
			gc.fillRoundRect(dx, dy, size, size, 4, 4);
			
			gc.setFill(color);
			gc.fillRoundRect(dx + 2, dy + 2, size - 4, size - 4, 4, 4);
		}
	}
}







