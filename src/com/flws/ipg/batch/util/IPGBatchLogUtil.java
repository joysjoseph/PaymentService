 package com.flws.ipg.batch.util;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;



public final class IPGBatchLogUtil {
	 private Logger logger;
	//private static Logger logger111 = Logger.getLogger(CTILogger.class);
	/*public static void main(String[] args) {
		logger = Logger.getLogger(CTILogger.class);
		DOMConfigurator.configure("C:\\WISMO_TEST\\WISMO_MESSAGE_FLOWJava\\com\\flowers\\common\\ctilog4j.xml");       
		logger111.info("Testsdsds Log");
		logger111.error("TESTTTTT");

	}*/
	 private IPGBatchLogUtil(Logger logger)
	  {
	    this.logger = logger;
	  }

	
	 public static IPGBatchLogUtil getCurrent(Class clazz)
	  {
	    return new IPGBatchLogUtil(Logger.getLogger(clazz));
	  }
	
	public static void initLogger(String jobNumber) {
		//DOMConfigurator.configure("C:\\WISMO_TEST\\WISMO_MESSAGE_FLOWJava\\com\\flowers\\common\\ctilog4j.xml");     
		//DOMConfigurator.configure("//app1//rtopuser//ctiivr//config//ctilog4j.xml");
		String log4jFile = "/opt/jobuser/incontactReport/config/incontact_standalone_Log4j.xml";
		if(jobNumber != null && jobNumber.trim().length() > 0){
			 log4jFile = "/opt/jobuser/incontactReport/config/incontact_standalone_Log4j.xml";
		}
		String log4jFile_windows = "C:\\joys\\AZure\\IPGbatch\\config\\ipg_batch_stand_log.xml";
		//System.out.println("initalizing log 4j completed");
		String str_osname = "XP";
		str_osname = System.getProperty("os.name");
		if(str_osname.startsWith("Windows")){
			DOMConfigurator.configure(log4jFile_windows);
		}
		else{
			DOMConfigurator.configure(log4jFile);
		}
		
	}
	
	 public void info(Object message)
	  {
		 logger.info(message);
	  }
	 public void debug(Object message)
	  {
		 logger.debug(message);
	  }
	 public void error(Object message)
	  {
		 logger.error(message);
	  }



}
