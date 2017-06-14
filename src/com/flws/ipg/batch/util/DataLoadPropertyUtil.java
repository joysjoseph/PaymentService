package com.flws.ipg.batch.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DataLoadPropertyUtil {

	/**
	 * @param args
	 */

	public static Hashtable sysConfigInfo = new Hashtable();
	public	static Date sysConfigInfoLastLoadedDate = null;	
	public static int reloadIntervalOfSysConfigInfoInMinutes = 15;
	public static void main(String[] args) {
		try {
			String str = getFuctionalParameterValue("dataFieldKeys","jjjjjjj");
			System.out.println("#####################"+str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static boolean isConfigInfoAlreadyLoaded() {

		Date	currentDate = new Date();
		int		reloadIntervalInMinutes = 0;
		String	methodName = "isConfigInfoAlreadyLoaded()";

		try {		

			reloadIntervalInMinutes = reloadIntervalOfSysConfigInfoInMinutes;
			if(sysConfigInfo == null  ||  sysConfigInfo.isEmpty() == true) {
				return false;						

			} else if(sysConfigInfoLastLoadedDate == null  ||  ((currentDate.getTime() - sysConfigInfoLastLoadedDate.getTime()) > reloadIntervalInMinutes * 60 * 1000)) {
				return false;					

			} else {
				 return true;
			}			

		} catch (Exception ex) {

			return false;		
		}						

	}		
	public static Hashtable loadConfigInfo(String funcConfigName) throws Exception {

		ClassLoader				classLoader = null;
		String					nameOfFuncConfigBundle = "";
		String					nameOfFuncConfigXmlFile = "";
		InputStream				inputStream = null;
		DocumentBuilderFactory	factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder			builder = null;
		Document				document = null;
		Element					funcConfigInfoAsElement = null;
		NodeList				configInfoElementsAsList = null;
		Node					configNode = null;		
		Hashtable				configInfo = new Hashtable();
		String					configParamName = "";
		String					configParamValue = "";
		String					methodName = "loadConfigInfo()";	

		try{
			nameOfFuncConfigBundle = funcConfigName;
			nameOfFuncConfigXmlFile = nameOfFuncConfigBundle;
			classLoader = DataLoadPropertyUtil.class.getClassLoader();
			inputStream = new FileInputStream(nameOfFuncConfigBundle);
			builder = factory.newDocumentBuilder();
			document = builder.parse(inputStream);
			funcConfigInfoAsElement = document.getDocumentElement();
			configInfoElementsAsList = funcConfigInfoAsElement.getChildNodes();
			int i = 0;
			while(i < configInfoElementsAsList.getLength()) {
				configNode = configInfoElementsAsList.item(i);			
				if(configNode.getNodeType() == Node.ELEMENT_NODE) {
					configParamName = (String) configNode.getNodeName();	
					configParamValue = configNode.getLastChild().getNodeValue();
					configInfo.put(configParamName, configParamValue);	
					if(configParamName!= null && configParamName.equals("RELOAD_INTERVAL") && configParamValue != null && configParamValue.trim().length() > 0){
						reloadIntervalOfSysConfigInfoInMinutes = Integer.parseInt(configParamValue.trim());
					}
				}
				i++;
			}
		}catch(Exception ex){
			sysConfigInfoLastLoadedDate = new Date();
			throw ex;
		}
		sysConfigInfoLastLoadedDate = new Date();
		return configInfo;
	}
	public static String getFuctionalParameterValue(String strParameterName, String strDefaultValue) {

		try {
			String str_osname = "XP";
			str_osname = System.getProperty("os.name");
			
			if (!isConfigInfoAlreadyLoaded()){		
				String xmlConfigFileName = "/opt/jobuser/incontactReport/config/incontact_standalone_config.xml";
				String xmlConfigFileName_windows = "C:\\joys\\AZure\\IPGbatch\\config\\ipg_batch_standalone_config.xml";
				if(str_osname.startsWith("Windows")){
					sysConfigInfo = loadConfigInfo(xmlConfigFileName_windows);
				}
				else{
					sysConfigInfo = loadConfigInfo(xmlConfigFileName);
				}
			}
			String strParameterValue = (String)sysConfigInfo.get(strParameterName);
			if (strParameterValue != null || strParameterValue.trim().length() > 0){
				return strParameterValue ;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return strDefaultValue;
		}
		return strDefaultValue;
	}

}

