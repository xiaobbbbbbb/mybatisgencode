package com.ecarinfo.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecarinfo.common.utils.PropUtil;
import com.ecarinfo.model.TableMetaData;
import com.ecarinfo.model.TableMetaDataField;
import com.ecarinfo.report.util.ReportMaker;

public class MetaUtil {
	
	
	public MetaUtil(String configFile) {
		this.configFile = configFile;
		init();
	}
	
	/**
	 * 格式　<b>表名前缀,类名前缀:表名前缀,类名前缀</b>
	 */
	public String PREFIX = null;//
	public String MODULE = null;//模块名称
	public String OUTPUT_DAO = null;
	public String OUTPUT_PO = null;
	public String OUTPUT_RM = null;
	public String OUTPUT_ORM = null;
	public String OUTPUT_ORM_CUSTOME = null;//用户自定义文件目录 
	public String OUTPUT_DIR = null;//输出根目录
	public String ORG_PREFIX = null;//总包名前缀(如:com.ecarinfo)
	private String configFile ;
	
	public static Map<String,String> prefixMap = new HashMap<String, String>();
	private void init() {
		DBUtil.URL = PropUtil.getProp(configFile, "DBUtil.URL");
		DBUtil.USERNAME = PropUtil.getProp(configFile, "DBUtil.USERNAME");
		DBUtil.PASSWORD = PropUtil.getProp(configFile, "DBUtil.PASSWORD");
		
		MODULE = PropUtil.getProp(configFile, "MetaUtil.MODULE");
		if(!hasText(MODULE)) {
			MODULE = "nomodel";
		}
		OUTPUT_DIR = PropUtil.getProp(configFile, "MetaUtil.OUTPUT_DIR");
		if(!hasText(OUTPUT_DIR)) {
			OUTPUT_DIR = System.getProperty("user.dir");
		}
		PREFIX = PropUtil.getProp(configFile, "MetaUtil.PREFIX");
		if(!hasText(PREFIX)) {
			PREFIX = "*";
		}
		ORG_PREFIX = PropUtil.getProp(configFile, "MetaUtil.ORG_PREFIX");
		if(!hasText(ORG_PREFIX)) {
			ORG_PREFIX = "com";
		}
				
		OUTPUT_DAO = "/src/main/java/"+ORG_PREFIX.replace(".", "/")+"/"+MODULE.replace(".", "/")+"/dao/";
		OUTPUT_PO = "/src/main/java/"+ORG_PREFIX.replace(".", "/")+"/"+MODULE.replace(".", "/")+"/po/";
		OUTPUT_RM = "/src/main/java/"+ORG_PREFIX.replace(".", "/")+"/"+MODULE.replace(".", "/")+"/rm/";
		OUTPUT_ORM = "/src/main/resources/persist/modules/"+MODULE.replace(".", "/")+"/system/";
		OUTPUT_ORM_CUSTOME = "/src/main/resources/persist/modules/"+MODULE.replace(".", "/")+"/custome/";
		//persist.system.readonly
		File file = new File(OUTPUT_DIR,OUTPUT_ORM_CUSTOME);
		if(!file.exists()) {
			file.mkdirs();
		}
		
		for(String p:PREFIX.split(":")) {
			String arry [] = p.split(",");
			if(arry.length == 2) {
				prefixMap.put(p.split(",")[0], p.split(",")[1]);
			} else {
				prefixMap.put(p.split(",")[0], null);
			}
		}
		
	}
	
	private boolean hasText(String value) {
		if(value == null) {
			return false;
		}
		if(value.trim().length() == 0) {
			return false;
		}
		return true;
	}
	
	public static final String getJavaStandField(String sqlField) {
		char [] chars = sqlField.toCharArray();
		for(int i=0;i<chars.length;i++) {
			char c = chars[i];
			if('_' == c && i < chars.length-1) {
				chars[i+1] = (char)(chars[i+1]-32);
			}
		}
		String dest = new String(chars);
		return dest.replace("_", "");
	}
	
	public static final String firstUpString(String value) {
		if(value.length() == 1) {
			return value.toUpperCase();
		} else if(value.length() >= 2) {
			return value.substring(0, 1).toUpperCase()+value.substring(1);
		} else {
			return value;
		}
	}
	
	public static final void genGetterAndSetter(BufferedWriter writer,String type,String fieldName) throws IOException {
		writer.newLine();
		writer.write("    public "+type+" get"+firstUpString(fieldName)+" () {");
		writer.newLine();
		writer.write("        return "+fieldName+";");
		writer.newLine();
		writer.write("    }");
		writer.newLine();
		
//		if("Date".equals(type)) {
//			writer.write("    public String get"+firstUpString(fieldName)+"2 () {");
//			writer.newLine();
//			writer.write("        if("+fieldName+" == null) {");
//			writer.newLine();
//			writer.write("            return null;");
//			writer.newLine();
//			writer.write("        }");
//			writer.newLine();
//			writer.write("        return DateUtils.dateToString("+fieldName+", DateUtils.FormatType.yyyy_MM_dd);");
//			writer.newLine();
//			writer.write("    }");
//			writer.newLine();
//		}
		writer.newLine();
		writer.write("    public void set"+firstUpString(fieldName)+" ("+type+" "+fieldName+") {");
		writer.newLine();
		writer.write("        this."+fieldName+" = "+fieldName+";");
		writer.newLine();
		writer.write("    }");
		writer.newLine();
	}
	
	public final TableMetaData getMetaData(String tableName) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			//show full fields from car_info; 
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("show full fields from "+tableName); 
			TableMetaData data = new TableMetaData();
			data.setTableName(tableName);
			int idx = 0;
			while(rs.next()) {
				TableMetaDataField field = new TableMetaDataField();
				field.setComment(rs.getString("Comment"));
				field.setField(rs.getString("Field"));
				field.setKey(rs.getString("Key"));
				field.setType(rs.getString("Type"));
				field.setExtra(rs.getString("Extra"));
				field.setDefaultVal(rs.getString("Default"));
				if(field.getKey() != null && "PRI".equals(field.getKey())) {
					data.setPk(field.getField());
					data.setPkIdx(idx);
					data.setJavaPkType(getJavaType(field.getType()));
					if(field.getExtra() != null && field.getExtra().equals("auto_increment")) {
						data.setAutoincrement(true);
					}
				}
				data.getFields().add(field);
				idx ++;
			}
			if(data.getPk() == null) {
				System.err.println("table ["+tableName+"] 's primary key not found!");
				System.exit(1);
			}
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn,stmt,rs);
		}
		return null;
	}
	
	
	
	public static final String getJavaType(String mysqlType) {
		if(mysqlType.contains("(")) {
			mysqlType = mysqlType.substring(0,mysqlType.indexOf('('));
		}
		mysqlType = mysqlType.toUpperCase();
		return MappingUtil.getJavaType(mysqlType);
	}
	
	public final void genDao(String className,String outputDir) throws Exception {
		Map<String,Object> map = new HashMap<String,Object>(); 
		map.put("className", className);
		map.put("module", MODULE);
		File outFile = new File(outputDir+OUTPUT_DAO+className+"Dao.java");
		if(outFile.exists()) {
			return;
		}
	    ReportMaker.exeute(map,"dao.tfl",outputDir+OUTPUT_DAO+className+"Dao.java"); 
	}
	
	public final void genORMXml(TableMetaData md,String className,String outputDir) throws Exception {
		Map<String,Object> map = new HashMap<String,Object>(); 
	    map.put("pKType", md.getJavaPkType());
	    map.put("javaPK", getJavaStandField(md.getPk()));
	    map.put("className", className);
	    map.put("resultMap", SQLUtils.getResultMap(md));
	    
	    map.put("findByPK", SQLUtils.getFindByPKSql(md));
//	    map.put("findByPKWithEx", getFindByPKSql(md));
	    map.put("findByAttr", SQLUtils.getFindByAttrSql(md));
	    map.put("findOneByAttr", SQLUtils.getFindOneByAttrSql(md));
	    map.put("findUnique", SQLUtils.getFindOneSql(md));
	    map.put("findAll", SQLUtils.getFindAllSql(md));
	    map.put("findByCriteria", SQLUtils.getFindByCriteriaSql(md));
	    map.put("findMap", SQLUtils.getFindFindMapSql(md));
	    map.put("insert", SQLUtils.getInsertSql(md));
	    map.put("update", SQLUtils.getUpdateSql(md));
	    map.put("updateWithCriteria", SQLUtils.getUpdateWithCriteriaSql(md));
	    map.put("deleteEntity", SQLUtils.getDeleteObjSql(md));
	    map.put("deleteByPK", SQLUtils.getDeleteByPKSql(md));
	    map.put("deleteByCriteria", SQLUtils.getDeleteByCriteriaSql(md));
	    map.put("module", MODULE);
	    //autoincrement
	    map.put("autoincrement", md.isAutoincrement());
	    
	    //map.put("findByAttrWithPaging", getFindByAttrWithPagingSql(md));
	    //map.put("findByAttrCounts", getFindCountsSql(md));
	    map.put("count", SQLUtils.getCountSql(md));
	    ReportMaker.exeute(map,"orm.tfl",outputDir+OUTPUT_ORM+className+".xml"); 
	}
	
	public final void execute() {
		String []tableNames = null;
		try {
			if(MODULE == null) {
				System.err.println("请填写MetaUtil.MODULE信息。");
				return;
			}
			if(tableNames==null||tableNames.length==0) {
				tableNames = new String[]{};
				tableNames = getTableNames().toArray(tableNames);
			}
			init();
			
			for(String tableName:tableNames) {
				tableName = tableName.trim();
				String className = tableName;
				boolean valid = false;
				if(prefixMap.size() > 0) { //如果要截取前缀
					for(Map.Entry<String, String> en:prefixMap.entrySet()) {
						String tablePre = en.getKey();//前缀
						String classPre = en.getValue();//替换
						if(classPre == null) {
							classPre = "";
						}
						if("*".equals(tablePre)) {
							className = classPre+"_"+className;
							valid = true;
							break;
						} else if(tableName.startsWith(tablePre+"_")) {
							className = tableName.substring(tableName.indexOf("_"));
							className = classPre+className;
							valid = true;
							break;
						}
					}
				} else {
					valid = true;
				}
				if(valid) {
//					System.err.println(tableName+" "+className+" "+firstUpString(getJavaStandField(className)));
					genClassFromTable(tableName,className,OUTPUT_DIR);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private final void genRMClass(TableMetaData md,String outputDir,String className) throws Exception {
		className = firstUpString(getJavaStandField(className));
		BufferedWriter writer = null;
		try {
			File destDir = new File(outputDir+OUTPUT_RM);
			if(!destDir.exists()) {
				destDir.mkdirs();
			}
			writer = new BufferedWriter(new FileWriter(destDir+"/"+className+"RM.java"));
			writer.write("package "+ORG_PREFIX+"."+MODULE+".rm;");
			writer.newLine();
			writer.write("public class "+className+"RM {");
			writer.newLine();
			writer.write("	public static final String tableName=\""+md.getTableName()+"\";//表名");
			writer.newLine();
			int idx = 0;
			for(TableMetaDataField f:md.getFields()) {
				if("PRI".equals(f.getKey())) {
					writer.write("	public static final String pk"+(idx>0?idx:"")+"=\""+f.getField()+"\";//主键");
					idx++;
					writer.newLine();
					writer.write("	public static final String "+getJavaStandField(f.getField())+"=\""+f.getField()+"\";//对应数据库主键,"+f.getComment());
				} else {
					writer.write("	public static final String "+getJavaStandField(f.getField())+"=\""+f.getField()+"\";//"+f.getComment());
				}
				writer.newLine();
			}
			writer.write("}");
			writer.newLine();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writer.flush();
			writer.close();
		}
		
		
	}
	
	private static List<String> getTableNames() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			//show full fields from car_info; 
			conn = DBUtil.getConnection();
			DatabaseMetaData dbmd = conn.getMetaData();
			rs = dbmd.getTables(null, null, "%", new String[]{ "TABLE" }); 
			List<String> tableNames = new ArrayList<String>();
            while (rs.next()) {  
                String tableName = rs.getString("TABLE_NAME");  //表名  
                tableNames.add(tableName);
            }  
			return tableNames;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn,stmt,rs);
		}
		return null;
	}
	
	
	private static final String getDefaultString(String javaType,Object value) {
		/**
		 * var4MysqlToJavaMap.put("BIGINT", "Long");
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
		 */
		if(value == null) {
			return "";
		}
		
		if("Long".equals(javaType)) {
			return value+"l";
		}
		if("String".equals(javaType)) {
			return "\""+value+"\"";
		}
		if("Float".equals(javaType)) {
			return value+"f";
		}
		if("Date".equals(javaType)) {
			return "new Date()";
		}
		if("Boolean".equals(javaType)) {
			Integer intVal = Integer.parseInt(value+"");
			if(intVal == null || intVal == 0) {
				return "false";
			}
			return "true";
		}
		if("Double".equals(javaType)) {
			return value+"d";
		}
		return value+"";
	}
	
	
	private final void genClassFromTable(String tableName,String className,String outputDir) throws Exception {
		System.err.println("process table "+tableName);
		className = firstUpString(getJavaStandField(className));
		TableMetaData md = getMetaData(tableName);
		File destDir = new File(outputDir+OUTPUT_PO);
		if(!destDir.exists()) {
			destDir.mkdirs();
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(destDir+"/"+className+".java"));
		writer.write("package "+ORG_PREFIX+"."+MODULE+".po;");
		writer.newLine();
		writer.write("import java.io.Serializable;");
		writer.newLine();
		
		boolean containsDate = false;// 防止重复引入
		for(TableMetaDataField f:md.getFields()) {
			if(!containsDate && (f.getType().equals("datetime") || f.getType().equals("date") || f.getType().equals("timestamp"))) {
				writer.write("import java.util.Date;");
				writer.newLine();
				containsDate = true;
			}
		}
		
		writer.newLine();
		
		//package com.ecarinfo.persist.po;
		writer.write("public class "+className+" implements Serializable {");
		writer.newLine();
		writer.newLine();
		writer.write("	private static final long serialVersionUID = -2260388125919493487L;");
		writer.newLine();
		
		try {
			for(TableMetaDataField f:md.getFields()) {
				String comment = f.getComment();
				String javaType = getJavaType(f.getType());
				String javaField = getJavaStandField(f.getField());
				Object defVal = f.getDefaultVal();
				String defaultValue = "";
				if(defVal == null) {
					defaultValue = "";
				} else {
					//gen defaultValue
					defaultValue = getDefaultString(javaType,defVal);
					if(defaultValue.length() > 0) {
						defaultValue = " = "+defaultValue;
					}
				}
				if(comment != null && comment.length() > 0) {
					writer.write("	private "+javaType+" "+javaField+defaultValue+";//"+comment);
				} else {
					writer.write("	private "+javaType+" "+javaField+defaultValue+";");
				}
				writer.newLine();
			}
			
			for(TableMetaDataField f:md.getFields()) {
				String javaType = getJavaType(f.getType());
				String javaField = getJavaStandField(f.getField());
				genGetterAndSetter(writer, javaType, javaField);
			}
			
			writer.write("}");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writer.flush();
			writer.close();
		}
		
		//生成DAO
		genDao(className,outputDir);
		
	    //生成mybatis orm 配置文件
		genORMXml(md,className,outputDir);
		genRMClass(md,outputDir,className);
	}
}
