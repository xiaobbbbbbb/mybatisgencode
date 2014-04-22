package com.ecarinfo.report.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class ReportMaker {
	private static Configuration cfg;            //模版配置对象 
	private final static Logger logger = Logger.getLogger(ReportMaker.class);
	
	static {
		try {
			//初始化FreeMarker配置 
            //创建一个Configuration实例 
            cfg = new Configuration();   
            cfg.setDefaultEncoding("UTF-8");
            cfg.setEncoding(Locale.CHINA, "UTF-8");  
            //设置FreeMarker的模版文件夹位置 
            cfg.setClassForTemplateLoading(ReportMaker.class.getClass(), "/templates");
		} catch (Exception e) {
			logger.error("报表工具初始化错误。",e);
		}
	}

    public static final void exeute(Map<String,Object> map,String templateFile,String outputFile) throws Exception { 
            //创建模版对象 
            Template t = cfg.getTemplate(templateFile);
            t.setEncoding("UTF-8");
            File outFile = new File(outputFile);
            File p = outFile.getParentFile();
            if(!p.exists()) {
            	p.mkdirs();
            }
            Writer writer =  null;
            try {
            	writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),"UTF-8"));
                //在模版上执行插值操作，并输出到制定的输出流中 
                t.process(map, writer); 
			} finally {
				if(writer != null) {
					writer.close();
				}
			}
    } 
    
    /**
     * 根据模板，生成文本并返回
     * @param map
     * @param templateFile
     * @param outputContent
     * @return
     * @throws Exception
     */
    public static final String exeute4Content(Map<String,Object> map,String templateFile) throws Exception { 
        //创建模版对象 
        Template t = cfg.getTemplate(templateFile);
        t.setEncoding("UTF-8");
        StringWriter writer = new StringWriter();
        //在模版上执行插值操作，并输出到制定的输出流中 
        t.process(map, writer);
        String res = writer.toString();
        writer.close();
        return res;
    } 
}
