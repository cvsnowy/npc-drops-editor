package com.abyss.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class NPCNames {
	private static HashMap<Integer, String> npcNames = new HashMap<>();

	public static void loadNPCNames() throws IOException {
		InputStream path = NPCNames.class.getResourceAsStream("/resource/npc_names.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(path));
		while (true) {
			String line = reader.readLine();
			if (line == null)
				break;
			String[] split = line.split(" - ", 2);
			int id = Integer.valueOf(split[0]);
			String name = split[1];
			npcNames.put(id, name);
		}

		reader.close();
	}

	public static HashMap<Integer, String> getNpcNames() {
		return npcNames;
	}

	public static String getNpcName(int id) {
		return npcNames.get(id) == null ? "null" : npcNames.get(id);
	}
}
