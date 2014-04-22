package com.ecarinfo.main;

import com.ecarinfo.util.MetaUtil;

public class PersistToolMain {

	public static void main(String[] args) throws Exception {
		// ec-server
		execute("ec-server-persist.properties");

		// ec-core
//		execute("ec-core-persist.properties");

		// ec-gprs-server
//		execute("ec-gprs-server-persist.properties");

		// ec-gprs-core
//		execute("ec-gprs-core-persist.properties");
		
	}
	
	static void execute(String configure) {
		new MetaUtil(configure).execute();
	}
}
