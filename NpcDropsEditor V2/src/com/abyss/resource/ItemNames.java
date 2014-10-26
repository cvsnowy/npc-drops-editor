package com.abyss.resource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javafx.collections.FXCollections;

public class ItemNames {
	private static Map<Integer, String> names = FXCollections
			.observableHashMap();

	public static void loadItemNames() {
		try {

			InputStream inputStream = ItemNames.class
					.getResourceAsStream("/resource/item_list742.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					inputStream));
			do {
				String line = in.readLine();
				if (line == null)
					break;
				if (!line.startsWith("//")) {
					String splitedLine[] = line.split(" - ", 3);
					int id = Integer.valueOf(splitedLine[0]).intValue();
					String name = splitedLine[1];
					names.put(id, name);
				}
			} while (true);
			in.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<Integer, String> getNames() {
		return names;
	}

	public static String getItemName(int id) {
		if (names.get(id) != null)
			return names.get(id);

		return "Unknown";
	}
}
