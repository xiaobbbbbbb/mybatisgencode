package com.ecarinfo.util;

import java.util.HashMap;
import java.util.Map;

public class MappingUtil {
	private static final Map<String,String> var4MysqlToJavaMap = new HashMap<String,String>();
	
	static {
		init();
	}
	private static final void init() {
		var4MysqlToJavaMap.put("BIGINT", "Long");
		var4MysqlToJavaMap.put("CHAR", "String");
		var4MysqlToJavaMap.put("INT", "Integer");
		var4MysqlToJavaMap.put("FLOAT", "Float");
		var4MysqlToJavaMap.put("DATETIME", "Date");
		var4MysqlToJavaMap.put("TIMESTAMP", "Date");
		var4MysqlToJavaMap.put("DATE", "Date");
		var4MysqlToJavaMap.put("VARCHAR", "String");
		var4MysqlToJavaMap.put("ENUM", "String");
		var4MysqlToJavaMap.put("TEXT", "String");
		var4MysqlToJavaMap.put("BIT", "Integer");
		var4MysqlToJavaMap.put("BOOL", "Boolean");
		var4MysqlToJavaMap.put("BOOLEAN", "Boolean");
		var4MysqlToJavaMap.put("TINYINT", "Boolean");
		var4MysqlToJavaMap.put("SMALLINT", "Integer");
		var4MysqlToJavaMap.put("DOUBLE", "Double");
		var4MysqlToJavaMap.put("DECIMAL", "Double");
//		var4MysqlToJavaMap.put("POINT", "java.sql.Blob");
	}
	
	public static final String getJavaType(String mysqlType) {
		String type = var4MysqlToJavaMap.get(mysqlType);
		if(type == null) {
			System.err.println("mysqlType = "+mysqlType);
			type = "String";
		}
		return type;
	}
}
