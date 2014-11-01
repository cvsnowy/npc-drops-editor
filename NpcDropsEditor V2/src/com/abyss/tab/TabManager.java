package com.abyss.tab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.abyss.npc.NPC;
import com.abyss.view.PresenterModel;
import com.abyss.view.main.editor.table.NPCDropTablePresenter;
import com.abyss.view.main.editor.table.NPCDropTableView;

public class TabManager {

	@Inject
	PresenterModel model;

	private ObservableMap<Tab, NPCDropTablePresenter> openedTabsMap;

	@PostConstruct
	public void init() {
		openedTabsMap = FXCollections.observableHashMap();
	}

	/**
	 * Creates and opens a new tab containing the npc's drop table information.
	 * 
	 * @param npc
	 *            - the NPC to edit
	 */
	public final void createNewDropTab(NPC npc) {
		if (checkForTab(npc))
			return;
		final Tab tab = new Tab(npc.toString());
		NPCDropTableView defaultTable = new NPCDropTableView();
		NPCDropTablePresenter tablePresenter = (NPCDropTablePresenter) defaultTable
				.getPresenter();
		tablePresenter.populateDropList(npc);
		tab.setContent(defaultTable.getViewWithoutRootContainer());
		getDropToolPane().getTabs().add(tab);
		openedTabsMap.put(tab, tablePresenter);
		getDropToolPane().getSelectionModel().selectLast();

		model.getDropEditorPresenter().bindDeleteButton(
				tablePresenter.disableOnSelectionProperty);

		tab.setOnCloseRequest((e) -> {
			Tab closingTab = (Tab) e.getTarget();
			NPCDropTablePresenter presenter = getPresenter(closingTab);
			presenter.disableOnSelectionProperty.set(true);
			presenter.saveDropTable(false);
			handleOnTabClose(tab);
		});

	}

	/**
	 * Forces a tab to close. Any modifications done on the drop definition on
	 * will NOT be saved.
	 * 
	 * @param tab
	 *            - the tab to close
	 */
	public final void forceCloseTab(Tab tab) {
		tab.getTabPane().getTabs().remove(tab);
		openedTabsMap.remove(tab);

	}

	/**
	 * Handles the appropriate actions when the tab is closed.
	 * 
	 * @param tab
	 */
	public final void handleOnTabClose(Tab tab) {
		openedTabsMap.remove(tab);
	}

	/**
	 * Closes all tabs that are opened and also allows for saving.
	 * 
	 * @param save
	 *            - should we save all of the tabs?
	 */
	public final void closeAllTabs(boolean save) {
		if (!openedTabsMap.isEmpty()) {
			for (Tab tab : openedTabsMap.keySet()) {
				if (save)
					getPresenter(tab).saveDropTable(false);
				tab.getTabPane().getTabs().remove(tab);
				getPresenter(tab).disableOnSelectionProperty.set(true);
			}
			openedTabsMap.clear();
		}
	}

	/**
	 * Checks if there is a tab is already open with the same NPC and selects
	 * it.
	 * 
	 * @param npc
	 *            - The NPC to check for.
	 * @return true if there is a tab with the same NPC open.
	 */
	public final boolean checkForTab(NPC npc) {
		for (Tab openedTab : openedTabsMap.keySet()) {
			NPCDropTablePresenter presenter = openedTabsMap.get(openedTab);
			if (presenter.getNpc().equals(npc)) {
				getDropToolPane().getSelectionModel().select(openedTab);
				return true;
			}

		}
		return false;

	}

	public final int getOpenedTabsCount() {
		return openedTabsMap.size();
	}

	public final NPCDropTablePresenter getPresenter(Tab tab) {
		return openedTabsMap.get(tab);
	}

	public ObservableMap<Tab, NPCDropTablePresenter> getOpenedTabsMap() {
		return openedTabsMap;
	}

	private TabPane getDropToolPane() {
		return model.getDropEditorPresenter().dropToolPane;
	}

}
