package net.gondr.views;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.gondr.util.JDBCUtil;

public class ScorePopupController {
	@FXML
	private TextField txtName;
	
	@FXML
	private Label lblScore;  //점수가 뜨는 라벨
	
	@FXML
	private Label lblError;  //에러메시지를 보여줄 라벨
	
	private Stage me;
	private int score = 0;
	
	public void setDialogStage(Stage value) {
		me = value;
	}
	
	public void setScore(int score) {
		this.score = score;
		lblScore.setText("당신의 점수는 " + score + "입니다");
		lblError.setText("");
	}
	
	public void record() {
		//게임 점수가 기록되도록 하는 핵심 매서드
		if(txtName.getText().isEmpty()) {
			lblError.setText("이름은 공백일 수 없습니다");
			return;
		}
		
		Connection con = JDBCUtil.getConnection();
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO tetris (name, score) VALUES ( ?, ? )";
		
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, txtName.getText());
			pstmt.setInt(2, score);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("DB 기록중 오류발생");
		}finally {
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
		
		me.close();
	}
}









