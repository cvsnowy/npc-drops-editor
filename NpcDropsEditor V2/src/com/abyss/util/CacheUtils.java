package com.abyss.util;

import java.io.File;

public class CacheUtils {

	public static final String DATA_DIRECTORY = System.getProperty("user.home")
			+ "/.abyss_nde/";

	public static final String CACHE_DIRECTORY = DATA_DIRECTORY + "data/cache/";

	public static final File findDataDirectory() {
		File dataDirectory = new File(DATA_DIRECTORY);

		if (!dataDirectory.exists()) {
			dataDirectory.mkdir();
		}
		return dataDirectory;
	}
}
