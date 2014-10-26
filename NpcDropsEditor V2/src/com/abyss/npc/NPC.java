package com.abyss.npc;

import com.abyss.resource.NPCNames;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class NPC {

	private IntegerProperty id;
	private StringProperty name;
	private ObservableList<NPCDrop> drops;

	public NPC(int id) {
		this(id, null);
	}

	public NPC(int id, NPCDrop[] drops) {
		this.id = new SimpleIntegerProperty(id);
		this.name = new SimpleStringProperty(NPCNames.getNpcName(id));
		this.drops = FXCollections.observableArrayList();
		if (drops != null)
			this.drops.setAll(drops);

	}

	public IntegerProperty idProperty() {
		return id;
	}

	public StringProperty nameProperty() {
		return name;
	}

	public int getId() {
		return id.get();
	}

	public String getName() {
		return name.get();
	}

	public ObservableList<NPCDrop> getDrops() {
		return drops;
	}

	@Override
	public String toString() {
		return getId() + " - " + getName();
	}
}
