package com.abyss;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.openrs.cache.CacheManager;

import com.abyss.resource.ItemNames;
import com.abyss.resource.NPCNames;
import com.abyss.view.main.MainWindowView;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		MainWindowView mainView = new MainWindowView();
		Scene scene = new Scene(mainView.getView());
		// final String uri =
		// getClass().getResource("Main.css").toExternalForm();
		// scene.getStylesheets().add(uri);
		primaryStage.setTitle("Npc Drops Editor");
		primaryStage.setScene(scene);
		primaryStage.show();
		initResources();
	}

	private void initResources() {
		try {
			CacheManager.init();
			ItemNames.loadItemNames();
			NPCNames.loadNPCNames();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void stop() {
		Injector.forgetAll();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
