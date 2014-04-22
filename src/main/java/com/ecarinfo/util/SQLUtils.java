package com.ecarinfo.util;

import java.util.List;

import com.ecarinfo.model.TableMetaData;
import com.ecarinfo.model.TableMetaDataField;

public class SQLUtils {
		
		public static final String getInsertSql(TableMetaData md) {
			StringBuffer sql = new StringBuffer();
			/*
			 * insert into car_info (
		          	device_id
		          ) values (
		          	#{deviceId}
		          )
			 */
			sql.append("INSERT INTO ").append(md.getTableName())
			.append("(");
			for(TableMetaDataField f:md.getFields()) {
				boolean isAppend = false;
				if("auto_increment".equals(f.getExtra().trim())) { //主键自动增涨
					if(!"PRI".equals(f.getKey())) {
						isAppend = true;
					}
				} else {
					isAppend = true;
				}
				if(isAppend) {
					sql.append(f.getField()).append(",");
				}
			}
			sql.deleteCharAt(sql.length() - 1);//去逗号
			sql.append(") VALUES (");
			for(TableMetaDataField f:md.getFields()) {
				boolean isAppend = false;
				if("auto_increment".equals(f.getExtra().trim())) { //主键自动增涨
					if(!"PRI".equals(f.getKey())) {
						isAppend = true;
					}
				} else {
					isAppend = true;
				}
				if(isAppend) {
					if(f.getType().toUpperCase().equals("POINT")) {
						sql.append("PointFromText('point(${").append(MetaUtil.getJavaStandField(f.getField())).append("})'),");
					} else {
						sql.append("#{").append(MetaUtil.getJavaStandField(f.getField())).append("},");
					}
				}
			}
			sql.deleteCharAt(sql.length() - 1);//去逗号
			sql.append(")");
			return PrettySQLFormatter.getPerttySql(sql.toString());
		}
		
		public static final String getDeleteByPKSql(TableMetaData md) {
			/**
			 * delete from car_info 
				where
					car_id = #{carId} 
			 */
			StringBuffer sql = new StringBuffer("DELETE FROM ")
				.append(md.getTableName())
				.append(" WHERE ")
				.append(md.getPk()).append(" = ").append("#{").append(MetaUtil.getJavaStandField(md.getPk())).append("}");
			return PrettySQLFormatter.getPerttySql(sql.toString());
		}
		
		

		public static final String getDeleteObjSql(TableMetaData md) {
			/**
			 * DELETE from car_info WHERE ${whereCond}
			 */
			StringBuffer sql = new StringBuffer("DELETE FROM ")
				.append(md.getTableName())
				.append(" WHERE ").append(md.getPk()).append(" = #{").append(MetaUtil.getJavaStandField(md.getPk())).append("}");
			return PrettySQLFormatter.getPerttySql(sql.toString());
		}
		
		public static final String getDeleteByCriteriaSql(TableMetaData md) {
			/**
			 * DELETE from car_info WHERE ${whereCond}
			 */
			StringBuffer sql = new StringBuffer("DELETE FROM ")
				.append(md.getTableName())
				.append(" WHERE ${whereCond} ");
			return PrettySQLFormatter.getPerttySql(sql.toString());
		}
		
		public static final String getUpdateSql(TableMetaData md) {
			/**
			 * update car_info 
				set
					device_id=#{deviceId},
				where
					car_id = #{carId} 
			 */
			StringBuffer sql = new StringBuffer("UPDATE ")
				.append(md.getTableName())
				.append(" SET ");
			
			for(TableMetaDataField f:md.getFields()) {
				
				boolean isAppend = false;
				if("auto_increment".equals(f.getExtra().trim())) { //主键自动增涨
					if(!"PRI".equals(f.getKey())) {
						isAppend = true;
					}
				} else {
					isAppend = true;
				}
				if(isAppend) {
					if(f.getType().toUpperCase().equals("POINT")) {
						sql.append(f.getField()).append(" = PointFromText('point(${").append(MetaUtil.getJavaStandField(f.getField())).append("})'),");
					} else {
						sql.append(f.getField()).append(" = #{").append(MetaUtil.getJavaStandField(f.getField())).append("},");
					}
				}
			}
			sql.deleteCharAt(sql.length() - 1);//去逗号
			sql.append(" WHERE ").append(md.getPk()).append(" = #{").append(MetaUtil.getJavaStandField(md.getPk())).append("}");
			return PrettySQLFormatter.getPerttySql(sql.toString());
		}
		

		public static final String getUpdateWithCriteriaSql(TableMetaData md) {
			/**
			 * update car_info 
				set
					device_id=#{deviceId},
				where
					car_id = #{carId} 
			 */
			StringBuffer sql = new StringBuffer("UPDATE ")
				.append(md.getTableName())
				.append(" SET ${updateField}")
				.append(" WHERE ${whereCond}");
			return PrettySQLFormatter.getPerttySql(sql.toString());
		}
		
		public static final String getFindByPKSql(TableMetaData md) {
			/**
			 * select * from car_info 
	        		where car_id = #{carId}
			 */
			String fStr = "*";//"${selectFields}";//getFields(md);
			StringBuffer sql = new StringBuffer("SELECT "+fStr+" FROM ")
			.append(md.getTableName())
			.append(" WHERE ")
			.append(md.getPk()).append(" = ").append("#{").append(MetaUtil.getJavaStandField(md.getPk())).append("}");
			return PrettySQLFormatter.getPerttySql(sql.toString());
		}
		
		public static final String getFindOneByAttrSql(TableMetaData md) {
			//SELECT * FROM car_info  WHERE `${fieldName}` ${condition} #{value}
			String fStr = "*";//getFields(md);
			return PrettySQLFormatter.getPerttySql("SELECT "+fStr+" FROM "+md.getTableName()+" WHERE `${fieldName}` = #{value} limit 1");
		}
		
		/**
		 * 获取指定表的所有字段
		 * @param md
		 * @return
		 */
		public static final String getFields(TableMetaData md) {
			String fStr = "";
			List<TableMetaDataField> fs = md.getFields();
			for(int i=0;i<fs.size();i++) {
				TableMetaDataField f = fs.get(i);
				if("POINT".equals(f.getType().toUpperCase())) {
					fStr += "AsText("+f.getField()+") AS "+f.getField();
				} else {
					fStr += f.getField();
				}
				if(i< fs.size() - 1) {
					fStr += ",";
				}
			}
			return fStr;
		}
		
		public static final String getFindByAttrSql(TableMetaData md) {
			//SELECT * FROM car_info  WHERE `${fieldName}` ${condition} #{value}
			String fStr = "*";//getFields(md);
			return PrettySQLFormatter.getPerttySql("SELECT "+fStr+" FROM "+md.getTableName()+" WHERE `${fieldName}` = #{value}");
		}
		
		public static final String getFindByAttrWithPagingSql(TableMetaData md) {
			//SELECT * FROM car_info  WHERE `${fieldName}` ${condition} #{value}
			String fStr = "*";//getFields(md);
			return PrettySQLFormatter.getPerttySql("SELECT "+fStr+" FROM "+md.getTableName()+" WHERE `${fieldName}` = #{value} limit #{offset} , #{rows}");
		}
		
		public static final String getFindByAttrCountsSql(TableMetaData md) {
			//SELECT * FROM car_info  WHERE `${fieldName}` ${condition} #{value}
			return PrettySQLFormatter.getPerttySql("SELECT COUNT("+md.getPk()+") FROM "+md.getTableName()+" WHERE `${fieldName}` = #{value}");
		}
		
		public static final String getFindAllSql(TableMetaData md) {
			//SELECT * FROM car_info WHERE ${whereCond}
			String fStr = "*";//getFields(md);
			return PrettySQLFormatter.getPerttySql("SELECT "+fStr+" FROM "+md.getTableName());
		}
		
		public static final String getFindByCriteriaSql(TableMetaData md) {
			//SELECT * FROM car_info WHERE ${whereCond}
			String fStr = "${selectFields}";//getFields(md);
			return PrettySQLFormatter.getPerttySql("SELECT "+fStr+" FROM "+md.getTableName()+" WHERE ${whereCond}");
		}
		
		
		public static final String getFindFindMapSql(TableMetaData md) {
			//SELECT * FROM car_info WHERE ${whereCond}
			String fStr = "${selectFields}";//getFields(md);
			return PrettySQLFormatter.getPerttySql("SELECT "+fStr+" FROM "+md.getTableName()+" WHERE ${whereCond}");
		}
		
		public static final String getFindWithPagingSql(TableMetaData md) {
			//SELECT * FROM car_info WHERE ${whereCond}
			String fStr = "${selectFields}";//getFields(md);
			return PrettySQLFormatter.getPerttySql("SELECT "+fStr+" FROM "+md.getTableName()+" WHERE ${whereCond} limit #{offset} , #{rows}");
		}
		
		public static final String getCountSql(TableMetaData md) {
			//SELECT * FROM car_info WHERE ${whereCond}
			String fStr = md.getPk();
			return PrettySQLFormatter.getPerttySql("SELECT COUNT("+fStr+") FROM "+md.getTableName()+" WHERE ${whereCond}");
		}
		
		public static final String getFindOneSql(TableMetaData md) {
			String fStr = "${selectFields}";//getFields(md);
			return PrettySQLFormatter.getPerttySql("SELECT "+fStr+" FROM "+md.getTableName()+" WHERE ${uniqueWhereCond}");
		}
		
		public static final String getResultMap(TableMetaData md) {
			/**
			 * 	<id 	property="carId" 			column="car_id"/>  
	        	<result property="deviceId" 		column="device_id"/>  
			 */
			StringBuffer buffer = new StringBuffer();
			for(TableMetaDataField f:md.getFields()) {
				if("PRI".equals(f.getKey())) {
					buffer.append("<id 		property=\"").append(MetaUtil.getJavaStandField(f.getField())).append("\" column=\"").append(f.getField()).append("\"/>\r\n		");
				} else {
					buffer.append("<result 	property=\"").append(MetaUtil.getJavaStandField(f.getField())).append("\" column=\"").append(f.getField()).append("\"/>\r\n		");
				}
			}
			return buffer.toString();
		}
}
