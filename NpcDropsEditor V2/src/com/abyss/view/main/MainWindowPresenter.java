package com.abyss.view.main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javax.inject.Inject;

import org.controlsfx.dialog.Dialogs;

import com.abyss.npc.DropsManager;
import com.abyss.tab.TabManager;
import com.abyss.util.FXMLUtils;
import com.abyss.view.PresenterModel;
import com.abyss.view.main.droplist.DropListView;
import com.abyss.view.main.editor.DropEditorView;
import com.abyss.view.main.editor.itemlist.ItemListView;
import com.abyss.view.main.editor.table.NPCDropTablePresenter;

public class MainWindowPresenter implements Initializable {
	@FXML
	AnchorPane dropListPane;
	@FXML
	AnchorPane dropTablePane;

	@FXML
	Label statusLabel;

	@FXML
	MenuItem openMenuItem;

	@Inject
	PresenterModel presenterModel;
	@Inject
	DropsManager dropsManager;
	@Inject
	TabManager tabManager;

	private StringProperty statusMessageProperty;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		presenterModel.setMainWindowPresenter(this);
		statusMessageProperty = new SimpleStringProperty("Welcome!");
		statusLabel.textProperty().bind(statusMessageProperty);
		constructDropListView();
		constructDropEditorView();

	}

	private void constructDropListView() {
		DropListView dropListView = new DropListView();
		Parent view = dropListView.getView();
		FXMLUtils.setAnchorPaneAnchors(view);
		dropListPane.getChildren().add(view);

	}

	private void constructDropEditorView() {
		DropEditorView dropEditorView = new DropEditorView();
		Parent view = dropEditorView.getView();
		FXMLUtils.setAnchorPaneAnchors(view);
		dropTablePane.getChildren().add(view);

	}

	@FXML
	public void openMenuItemPressed() {
		FileChooser chooser = new FileChooser();
		if (dropsManager.getLastDirectory() != null) {
			if (dropsManager.getLastDirectory().getParent() != null)
				chooser.setInitialDirectory(new File(dropsManager
						.getLastDirectory().getParent()));

		}
		chooser.getExtensionFilters().add(
				new ExtensionFilter("Binary Files", "*.d"));
		File dropFile = chooser.showOpenDialog(dropListPane.getScene()
				.getWindow());

		if (dropFile != null) {
			dropsManager.loadNpcDrops(dropFile);
		}
	}

	@FXML
	public void packDropMenuItemPressed() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Choose your Save Location");
		if (dropsManager.getLastDirectory() != null) {
			if (dropsManager.getLastDirectory().getParent() != null)
				chooser.setInitialDirectory(new File(dropsManager
						.getLastDirectory().getParent()));
		}

		chooser.setInitialFileName("packedDrops");

		chooser.getExtensionFilters().addAll(
				new ExtensionFilter("Packed File", ".d"));

		File file = chooser.showSaveDialog(dropListPane.getScene().getWindow());

		if (file != null) {
			for (NPCDropTablePresenter npcDropTablePresenter : tabManager
					.getOpenedTabsMap().values()) {
				npcDropTablePresenter.saveDropTable(false);
			}
			try {
				dropsManager.packNpcDrops(file);
			} catch (IOException e) {
				Dialogs.create().masthead("Error with Saving Operation")
						.showException(e);
			}

		}
	}

	private Stage itemListStage;

	@FXML
	public void itemListMenuItemPressed() {
		ItemListView itemListView = new ItemListView();
		Scene scene = new Scene(itemListView.getView());
		if (itemListStage == null) {
			itemListStage = new Stage();
			itemListStage.setScene(scene);
			itemListStage.setTitle("Item List 742");
			itemListStage.initOwner(dropListPane.getScene().getWindow());
		}
		itemListStage.show();
	}

	public void setStatusMessage(String msg) {
		statusMessageProperty.set(msg);
	}

}
