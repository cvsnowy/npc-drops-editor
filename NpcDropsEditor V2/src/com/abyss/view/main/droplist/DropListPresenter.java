package com.abyss.view.main.droplist;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.inject.Inject;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog.Actions;
import org.controlsfx.dialog.Dialogs;

import com.abyss.npc.DropsManager;
import com.abyss.npc.NPC;
import com.abyss.tab.TabManager;
import com.abyss.view.PresenterModel;
import com.abyss.view.main.editor.table.NPCDropTablePresenter;
import com.abyss.view.npclist.NPCListPresenter;
import com.abyss.view.npclist.NPCListView;

public class DropListPresenter implements Initializable {
	@FXML
	Button addButton;
	@FXML
	Button deleteButton;
	@FXML
	Button copyButton;

	@FXML
	MenuItem copyMenuItem;
	@FXML
	MenuItem deleteMenuItem;

	@FXML
	TextField searchTextField;
	@FXML
	public ListView<NPC> dropListView;

	@Inject
	DropsManager dropsManager;

	@Inject
	PresenterModel prsenterModel;

	@Inject
	TabManager tabManager;

	private List<Tab> pendingTabDeleteList;

	private boolean filterMode;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		prsenterModel.setDropListPresenter(this);
		pendingTabDeleteList = new ArrayList<Tab>();
		dropListView.getSelectionModel().setSelectionMode(
				SelectionMode.MULTIPLE);
		dropListView.setItems(dropsManager.getMasterDropDefinitions());
		copyMenuItem.disableProperty().bind(copyButton.disabledProperty());
		deleteMenuItem.disableProperty().bind(deleteButton.disabledProperty());
		setupListFiltering();
		listenForListItemSelection();
	}

	@FXML
	public void dropListViewPressed(MouseEvent event) {
		if (event.getButton().equals(MouseButton.PRIMARY)) {
			if (event.getClickCount() == 2) {
				if (!dropListView.getSelectionModel().isEmpty()) {
					NPC selectedNPC = dropListView.getSelectionModel()
							.getSelectedItem();
					tabManager.createNewDropTab(selectedNPC);
				}

			}
		}
	}

	@FXML
	public void addButtonPressed(ActionEvent event) {
		loadNPCListChooser(false);
	}

	@FXML
	public void copyButtonPressed() {
		loadNPCListChooser(true);
	}

	@FXML
	public void deleteAllButtonPressed() {
		Action action = Dialogs.create().title("Delete All Definitions")
				.message("Delete all definitions?").showConfirm();
		if (action.equals(Actions.YES)) {
			deleteAllDefinitions();
		}
	}

	@FXML
	public void deleteButtonPressed(ActionEvent event) {
		ObservableList<NPC> selectedNPCs = dropListView.getSelectionModel()
				.getSelectedItems();
		Action action = Dialogs.create().title("Delete")
				.message("Deleting the following definitions: " + selectedNPCs)
				.showConfirm();

		if (action.equals(Actions.YES)) {
			deleteDefinitions(selectedNPCs);

		}

		// Old code, only supported single selection

		/*
		 * NPC selectedNPC = dropListView.getSelectionModel().getSelectedItem();
		 * 
		 * Action action = Dialogs .create() .masthead("Delete: [" +
		 * selectedNPC.toString() + "]") .message(
		 * "Are you sure you want to delete this npc drop definition?")
		 * .showConfirm();
		 * 
		 * if (action.equals(Actions.YES)) { deleteDefinition(selectedNPC);
		 * 
		 * } /
		 */
	}

	private void deleteDefinitions(ObservableList<NPC> npcsList) {
		for (Tab tab : prsenterModel.getDropEditorPresenter().dropToolPane
				.getTabs()) { // checks if there is a tab open and
								// closes it
			if (npcsList.contains(tabManager.getPresenter(tab).getNpc())) {
				pendingTabDeleteList.add(tab);
			}
		}

		// Finally closes all of the tabs in the pending delete list.
		pendingTabDeleteList.stream().forEach((tab) -> {
			tabManager.forceCloseTab(tab);
		});

		pendingTabDeleteList.clear();
		dropsManager.getMasterDropDefinitions().removeAll(npcsList);
		dropListView.requestFocus();

	}

	private void deleteAllDefinitions() {
		dropsManager.getMasterDropDefinitions().clear();
		tabManager.closeAllTabs(false);

	}

	private void setupListFiltering() {
		FilteredList<NPC> filteredItems = new FilteredList<>(
				dropsManager.getMasterDropDefinitions(), i -> true);
		searchTextField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					boolean disable = !newValue.isEmpty();

					filterMode = disable;

					addButton.setDisable(disable);

					filteredItems.setPredicate(item -> {
						if (newValue == null || newValue.isEmpty()) {
							return true;
						}

						if (item.getName().toLowerCase()
								.contains(newValue.toLowerCase())) {
							return true;
						}
						try {
							if (item.getId() == Integer.parseInt(newValue))
								return true;
						} catch (NumberFormatException ex) {
							return false;
						}

						return false;
					});
				});
		SortedList<NPC> sortedData = new SortedList<>(filteredItems);
		dropListView.setItems(sortedData);
	}

	private void listenForListItemSelection() {
		dropListView
				.getSelectionModel()
				.selectedItemProperty()
				.addListener((e, oldVal, newVal) -> {
					// user can't copy multiple drop definitions so disable that
					// too
						copyButton.setDisable(newVal == null
								|| dropListView.getSelectionModel()
										.getSelectedItems().size() > 1);
						deleteButton.setDisable(newVal == null);

					});
	}

	private void loadNPCListChooser(boolean copy) {
		String stageTitle = "Adding NPC...";
		NPCListView npcListView = new NPCListView();
		Scene scene = new Scene(npcListView.getView());
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.initOwner(dropListView.getScene().getWindow());
		stage.initModality(Modality.WINDOW_MODAL);

		if (!dropListView.getSelectionModel().isEmpty()) {
			NPCListPresenter presenter = (NPCListPresenter) npcListView
					.getPresenter();

			NPC selectedNPC = dropListView.getSelectionModel()
					.getSelectedItem();

			NPCDropTablePresenter viewingTablePresenter = prsenterModel
					.getDropEditorPresenter().getViewingTabPresenter();

			if (viewingTablePresenter != null
					&& viewingTablePresenter.tableNeedsSaving() && copy) {
				// If a user is trying to copy a drop table of a npc that they
				// are currently editing and they have not saved the changes
				// yet, then it will force save.
				viewingTablePresenter.saveDropTable(true);

			}

			if (copy) {
				stageTitle = "Copying: " + selectedNPC.toString();
				presenter.copyDrops(selectedNPC.getDrops().toArray());
			}
		}

		stage.setTitle(stageTitle);
		stage.show();
	}

	public void resetSearch() {
		filterMode = false;
		searchTextField.setText("");
	}

	public boolean inFilterMode() {
		return filterMode;
	}
}
