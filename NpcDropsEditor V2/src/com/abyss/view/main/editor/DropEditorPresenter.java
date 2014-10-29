package com.abyss.view.main.editor;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TabPane;

import javax.inject.Inject;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog.Actions;
import org.controlsfx.dialog.Dialogs;

import com.abyss.tab.TabManager;
import com.abyss.view.PresenterModel;
import com.abyss.view.main.editor.table.NPCDropTablePresenter;

public class DropEditorPresenter implements Initializable {

	@FXML
	public TabPane dropToolPane;

	@FXML
	Button deleteButton;
	@FXML
	Button deleteAllButton;
	@FXML
	Button addButton;
	@FXML
	Button closeAllButton;
	@FXML
	Button saveButton;
	@FXML
	Button saveAllButton;

	@FXML
	CheckBox quickSaveCheckBox;

	@Inject
	PresenterModel presenterModel;

	@Inject
	TabManager tabManager;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		presenterModel.setDropEditorPresenter(this);

		dropToolPane
				.getSelectionModel()
				.selectedItemProperty()
				.addListener((e, oldVal, newVal) -> {
					// TODO redo ugly
						if (newVal != null) {
							if (tabManager.getPresenter(newVal) != null)
								bindDeleteButton(tabManager
										.getPresenter(newVal).disableOnSelectionProperty);
							deleteAllButton.setDisable(false);
							addButton.setDisable(false);
							closeAllButton.setDisable(false);
							saveButton.setDisable(false);
							saveAllButton.setDisable(false);
						} else {
							deleteAllButton.setDisable(true);
							closeAllButton.setDisable(true);
							saveButton.setDisable(true);
							addButton.setDisable(true);
							saveAllButton.setDisable(true);
						}
					});
	}

	@FXML
	public void closeAllButtonPressed() {
		if (tabManager.getOpenedTabsCount() > 1) {
			Action action = Dialogs.create()
					.message("Are you sure you want to close all of the tabs?")
					.showConfirm();
			if (action.equals(Actions.YES)) {
				tabManager.closeAllTabs(true);
			}
		} else {
			tabManager.closeAllTabs(true);
		}
	}

	@FXML
	public void deleteButtonPressed() {
		getViewingTabPresenter().removeSelectedItems();
	}

	@FXML
	public void saveButtonPressed() {
		getViewingTabPresenter().saveDropTable(quickSaveCheckBox.isSelected());
	}

	@FXML
	public void addButtonPressed() {
		getViewingTabPresenter().addNewDropItem();
	}

	@FXML
	public void deleteAllButtonPressed(ActionEvent event) {
		if (getViewingTabPresenter().getModifiedDrops().isEmpty()) {
			event.consume();
			return;
		}

		Action action = Dialogs.create()
				.message("Are you sure you want to delete all items?")
				.showConfirm();

		if (action.equals(Actions.YES)) {
			getViewingTabPresenter().removeAllItems();
		}
	}

	@FXML
	public void saveAllButtonPressed() {
		for (NPCDropTablePresenter npcDropTablePresenter : tabManager
				.getOpenedTabsMap().values()) {
			npcDropTablePresenter.saveDropTable(true);
		}
	}

	public void bindDeleteButton(BooleanProperty property) {
		deleteButton.disableProperty().bind(property);
	}

	public NPCDropTablePresenter getViewingTabPresenter() {
		return tabManager.getPresenter(dropToolPane.getSelectionModel()
				.getSelectedItem());
	}

}
