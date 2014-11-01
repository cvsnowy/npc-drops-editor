package com.abyss.npc;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Collections;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.controlsfx.dialog.Dialogs;

import com.abyss.item.Item;
import com.abyss.tab.TabManager;

public class DropsManager {

	@Inject
	TabManager tabManager;

	private File lastDirectory;

	private ObservableList<NPC> masterNPCDropDefinitions;

	@PostConstruct
	public void init() {
		masterNPCDropDefinitions = FXCollections.observableArrayList();
	}

	public boolean loadNpcDrops(File file) {
		try {
			RandomAccessFile dataAccessFile = new RandomAccessFile(file, "r");
			FileChannel channel = dataAccessFile.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0,
					channel.size());

			int dropSize = buffer.getShort() & 0xffff;

			masterNPCDropDefinitions.clear();
			tabManager.closeAllTabs(false);

			for (int i = 0; i < dropSize; i++) {
				int npcId = buffer.getShort() & 0xffff;
				NPCDrop[] drops = new NPCDrop[buffer.getShort() & 0xffff];
				for (int d = 0; d < drops.length; d++) {

					if (buffer.get() == 0) {
						int itemId = buffer.getShort() & 0xFFFF;
						double rate = buffer.getDouble();
						drops[d] = new NPCDrop(new Item(itemId), rate,
								buffer.getInt(), buffer.getInt(), false);

					} else
						drops[d] = new NPCDrop(new Item(0), 0, 0, 0, true);

				}
				masterNPCDropDefinitions.add(new NPC(npcId, drops));

			}

			Collections.sort(masterNPCDropDefinitions, new Comparator<NPC>() {
				@Override
				public int compare(NPC o1, NPC o2) {
					return Integer.compare(o1.getId(), o2.getId());
				}

			});

			dataAccessFile.close();
			buffer.clear();

		} catch (IOException | BufferUnderflowException exception) {
			Dialogs.create()
					.title("Error while Loading File")
					.message(
							"Could not load the drops file! Please click show details for more information.")
					.showException(exception);
			return false;
		}
		lastDirectory = file;
		return true;

	}

	public void packNpcDrops(File file) throws IOException {
		RandomAccessFile out = new RandomAccessFile(file, "rw");
		int dropSize = masterNPCDropDefinitions.size();
		out.writeShort(dropSize);
		for (int i = 0; i < dropSize; i++) {
			NPC npc = masterNPCDropDefinitions.get(i);
			out.writeShort(npc.getId());
			out.writeShort(npc.getDrops().size());
			for (int j = 0; j < npc.getDrops().size(); j++) {
				NPCDrop drop = npc.getDrops().get(j);
				boolean rare = drop.getRarity();
				if (rare) {
					out.writeByte(1);
				} else {
					out.writeByte(0);
					out.writeShort(drop.getItem().getId());
					out.writeDouble(drop.getRate());
					out.writeInt(drop.getMinAmount());
					out.writeInt(drop.getMaxAmount());
				}

			}
		}
		out.close();

		Dialogs.create().title("Success!")
				.message("Successfully packed your npc drops file.")
				.showInformation();

	}

	public ObservableList<NPC> getMasterDropDefinitions() {
		return masterNPCDropDefinitions;
	}

	public File getLastDirectory() {
		return lastDirectory;
	}

}
