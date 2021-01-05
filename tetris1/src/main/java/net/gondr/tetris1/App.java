package net.gondr.tetris1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.gondr.views.ScorePopupController;

public class App extends Application 
{
	public static App app;
	public Game game = null;
	
	private Stage popupStage;
	private ScorePopupController spController;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		app = this;
		System.out.println(this);
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/net/gondr/views/Main.fxml"));
			AnchorPane ap = (AnchorPane) loader.load();
			
			Scene scene = new Scene(ap);
			
			scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
				if(game != null) {
					game.keyHandler(e);
				}
			});
			
			FXMLLoader spLoader = new FXMLLoader();
			spLoader.setLocation(getClass().getResource("/net/gondr/views/ScorePopup.fxml"));
			
			popupStage = new Stage();
			popupStage.setTitle("게임 기록");
			//모달창으로 만들고 부모를 primaryStage 로 기록한다.
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.initOwner(primaryStage);
			
			AnchorPane popup = spLoader.load();
			Scene popupScene = new Scene(popup);
			popupStage.setScene(popupScene);
			
			spController = spLoader.getController();
			spController.setDialogStage(popupStage);
			
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("프로그램 로딩중 오류 발생");
		}		
	}
    
	public static void main(String[] args) {
		launch(args);
	}
	
	public void openPopup(int score) {
		spController.setScore(score);
		popupStage.show();
	}
}
