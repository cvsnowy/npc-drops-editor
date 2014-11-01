package com.abyss.view.npclist;

import java.net.URL;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.controlsfx.dialog.Dialogs;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import com.abyss.control.NumberTextField;
import com.abyss.item.Item;
import com.abyss.npc.DropsManager;
import com.abyss.npc.NPC;
import com.abyss.npc.NPCDrop;
import com.abyss.resource.NPCNames;
import com.abyss.tab.TabManager;
import com.abyss.view.PresenterModel;

public class NPCListPresenter implements Initializable {
	@FXML
	TextField searchNpcTextField;

	@FXML
	NumberTextField npcIndexTextField;

	@FXML
	Button finishButton;

	@FXML
	ListView<NPC> npcListView;

	@Inject
	DropsManager dropsManager;

	@Inject
	PresenterModel presenterModel;

	@Inject
	TabManager tabManager;

	private Object[] npcDropCopy;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		for (int id : NPCNames.getNpcNames().keySet())
			npcListView.getItems().add(new NPC(id));
		setupNpcIdTextField();
		npcListView.getSelectionModel().selectedItemProperty()
				.addListener((e, oldVal, newVal) -> {
					if (newVal != null) {
						npcIndexTextField.setText(newVal.getId() + "");
					} else {
						npcIndexTextField.setText("");
					}

				});
		setupListFiltering();
	}

	private void setupNpcIdTextField() {
		npcIndexTextField.setPromptText("Enter your id here...");
		npcIndexTextField.setNumericOnly(true);
		npcIndexTextField.textProperty().addListener((e, oldVal, newVal) -> {
			finishButton.setDisable(newVal.isEmpty());

		});
	}

	@FXML
	public void addNpcClickAction(MouseEvent e) {
		if (e.getButton().equals(MouseButton.PRIMARY)) {
			if (e.getClickCount() == 2) {
				finish();
			}
		}

	}

	@FXML
	public void closeAction() {
		Stage stage = (Stage) npcListView.getScene().getWindow();
		stage.close();
	}

	@FXML
	public void finish() {
		Stage stage = (Stage) npcListView.getScene().getWindow();
		if (addNpc())
			stage.close();
	}

	private boolean addNpc() {
		boolean sucessStatus = true;

		NPC newNpc = new NPC(Integer.parseInt(npcIndexTextField.getText()),
				new NPCDrop[] { new NPCDrop(new Item(526), 100, 1, 1, false) });

		if (npcDropCopy != null) {
			NPCDrop[] copiedDrops = new NPCDrop[npcDropCopy.length];
			System.arraycopy(npcDropCopy, 0, copiedDrops, 0, npcDropCopy.length);
			newNpc = new NPC(Integer.parseInt(npcIndexTextField.getText()),
					copiedDrops);
		}

		for (NPC npc : dropsManager.getMasterDropDefinitions()) {
			if (npc.getId() == newNpc.getId()) {
				Dialogs.create()
						.title("Duplicate Found!")
						.masthead("NPC: [" + npc.toString() + "]")
						.message(
								"This NPC already exists! Please try another npc.")
						.showError();
				sucessStatus = false;
				break;
			}
		}

		if (sucessStatus) {
			if (presenterModel.getDropListPresenter().isFilteringList())
				presenterModel.getDropListPresenter().resetSearch();
			dropsManager.getMasterDropDefinitions().add(newNpc);
			int lastIndex = dropsManager.getMasterDropDefinitions().size() - 1;
			presenterModel.getDropListPresenter().dropListView.getSelectionModel()
					.clearAndSelect(lastIndex);
			presenterModel.getDropListPresenter().dropListView.scrollTo(lastIndex);
			presenterModel.getDropListPresenter().dropListView.requestFocus();
			tabManager.createNewDropTab(newNpc);

		}

		return sucessStatus;
	}

	private void setupListFiltering() {
		FilteredList<NPC> filteredItems = new FilteredList<>(
				npcListView.getItems(), i -> true);
		searchNpcTextField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					filteredItems.setPredicate(item -> {
						if (newValue == null || newValue.isEmpty()) {
							return true;
						}

						if (item.getName().toLowerCase()
								.contains(newValue.toLowerCase())) {
							return true;
						}
						return false;
					});
				});
		SortedList<NPC> sortedData = new SortedList<>(filteredItems);
		npcListView.setItems(sortedData);
	}

	public void copyDrops(Object[] drops) {
		this.npcDropCopy = drops;
	}

}
