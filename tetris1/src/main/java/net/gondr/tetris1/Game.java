package net.gondr.tetris1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import net.gondr.domain.Block;
import net.gondr.domain.Player;
import net.gondr.domain.ScoreVO;
import net.gondr.util.JDBCUtil;

public class Game {
	private GraphicsContext gc;
	public Block[][] board;  //게임판 2차원 배열
	
	private double width;  //게임판의 너비와 높이
	private double height;
	
	private AnimationTimer mainLoop; //게임 루프
	private long before; //이전 프레임의 시간
	
	private Player player;
	private double blockDownTime = 0;
	
	private int score = 0;
	
	private Label scoreLabel;
	private Canvas nextCanvas;
	private double nbWidth;
	private double nbHeight;
	private GraphicsContext nbGC;
	
	private boolean gameOver = false;
	private ObservableList<ScoreVO> list;
	public Game(Canvas canvas, Canvas nextCanvas, 
			Label scoreLabel, ObservableList<ScoreVO> list) {
		this.nextCanvas = nextCanvas;
		this.scoreLabel = scoreLabel;
		//Top10스코어 기록용 리스트 가지고 있기.
		this.list = list;
		
		//다음 블럭 그려줄 캔버스를 설정
		this.nbWidth = this.nextCanvas.getWidth();
		this.nbHeight = this.nextCanvas.getHeight();
		this.nbGC = this.nextCanvas.getGraphicsContext2D();
		
		width = canvas.getWidth();
		height = canvas.getHeight();
		
		double size = (width - 4) / 10;
		
		board = new Block[20][10];
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 10; j++) {
				board[i][j] = new Block(j * size + 2, i * size + 2, size);
			}
		}
		this.gc = canvas.getGraphicsContext2D();
		
		mainLoop = new AnimationTimer() {
			@Override
			public void handle(long now) {
				update( (now - before) / 1000000000d);  //0이 9개 끝에다가 d 붙여
				before = now;
				render();
			}
		};
		before = System.nanoTime();
		player = new Player(board);
		gameOver = true;
		
		reloadTopScore();
	}
	
	public void reloadTopScore() {
		list.clear();
		Connection con = JDBCUtil.getConnection();
		if(con == null) {
			System.out.println("데이터베이스 연결 에러");
			return;
		}
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM tetris ORDER BY score DESC LIMIT 0, 10";
		
		try {
			pstmt = con.prepareStatement(sql); //sql을 준비하고
			rs = pstmt.executeQuery(); //바로 실행한다.
			
			while(rs.next()) {
				ScoreVO temp = new ScoreVO();
				temp.setId(rs.getInt("id"));
				temp.setName(rs.getString("name"));
				temp.setScore(rs.getInt("score"));
				list.add(temp);
			}
			
		} catch (Exception e) {
			System.out.println("데이터베이스 값 처리중 오류 발생");
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
	}
	
	public void gameStart() {
		score = 0;
		gameOver = false;
		mainLoop.start();
		player = new Player(board);
		
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 10; j++) {
				board[i][j].setData(false, Color.WHITE);
			}
		}
	}
	
	//매초 실행되는 루프 매서드 
	public void update(double delta) {
		if(gameOver) return;
		blockDownTime += delta;		
		
		double limit = 0.5 - score / 100d < 0.1 ? 0.1 : 0.5 - score / 100d;
		
		
		if(blockDownTime >= limit) {
			player.down(); //한칸 내리기
			blockDownTime = 0;
		}
	}
	
	//한 줄이 가득 찼는지를 검사하는 매서드
	public void checkLineStatus() {
		for(int i = 19; i >= 0; i--) {
			boolean clear = true; //해당 줄이 꽉찼다고 가정하고
			//라인이 꽉찼는지를 검사하는 부분
			for(int j = 0; j < 10; j++) {
				if(!board[i][j].getFill()) {
					clear = false;
					break;
				}
			}
			
			//만약 라인이 꽉 찼다면 수행할 부분
			if(clear) {
				score++;
				for(int j = 0; j < 10; j++) {
					board[i][j].setData(false, Color.WHITE);
				}
				
				for(int k = i - 1; k >= 0; k--) {
					for(int j = 0; j < 10; j++) {
						board[k+1][j].copyData(board[k][j]);
					}
				}
				
				for(int j = 0; j < 10; j++) {
					board[0][j].setData(false, Color.WHITE);
				}
				
				//중요한 것!
				i++;
			}
			
		}
	}
	
	//매 프레임마다 화면을 그려주는 매서드
	public void render() {
		gc.clearRect(0, 0, width, height);
		gc.setStroke(Color.rgb(0,0,0));
		gc.setLineWidth(2);
		gc.strokeRect(0, 0, width, height);
		
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 10; j++) {
				board[i][j].render(gc);
			}
		}
		
		scoreLabel.setText("Score : " + score);
		
		if(gameOver) {
			gc.setFont(new Font("Arial", 30));
			gc.setTextAlign(TextAlignment.CENTER);
			gc.strokeText("Game Over", width / 2, height / 2);
		}
		
		player.render(nbGC, nbWidth, nbHeight);
	}
	
	//키보드 이벤트를 처리해주는 매서드
	public void keyHandler(KeyEvent e) {
		if(gameOver) return;
		player.keyHandler(e);
	}
	
	public void setGameOver() {
		gameOver = true;
		render();
		mainLoop.stop();
		
		App.app.openPopup(score);
		
		reloadTopScore();
	}
	
}









