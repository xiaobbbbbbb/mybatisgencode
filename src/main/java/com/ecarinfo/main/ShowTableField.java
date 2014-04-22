package com.ecarinfo.main;

import java.sql.SQLException;

import com.ecarinfo.model.TableMetaData;
import com.ecarinfo.model.TableMetaDataField;
import com.ecarinfo.util.MetaUtil;

public class ShowTableField {
	
	/**
	 * 
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) throws Exception {
		String talbeName = "et_car_report";
		MetaUtil util = new MetaUtil("ec-server-persist.properties");
		TableMetaData tmd = util.getMetaData(talbeName);
		show(tmd);
	}
	
	private static final void show(TableMetaData tmd) {
		for(TableMetaDataField f:tmd.getFields()) {
			System.err.println(f.getField()+"	"+f.getComment());
		}
	}
}
