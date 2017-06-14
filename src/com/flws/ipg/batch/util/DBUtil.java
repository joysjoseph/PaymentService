package com.flws.ipg.batch.util;

import java.sql.*;
import java.util.ArrayList;

import com.flws.ipg.batch.IPGBatchProcessJobFirst;
import com.microsoft.sqlserver.jdbc.*;

public class DBUtil {
	static org.apache.log4j.Logger  m_log = org.apache.log4j.Logger.getLogger(DBUtil.class);
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static Connection getSQLDBConnection(){
		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;

		String hostname = DataLoadPropertyUtil.getFuctionalParameterValue("DB_HOST", "");
		String portNumber = DataLoadPropertyUtil.getFuctionalParameterValue("DB_PORT", "");
		String str_DB_Name = DataLoadPropertyUtil.getFuctionalParameterValue("DB_NAME", "");
		String str_DB_user= DataLoadPropertyUtil.getFuctionalParameterValue("DB_USER", "");
		String str_DB_pass = DataLoadPropertyUtil.getFuctionalParameterValue("DB_PASS", "");
		SQLServerDataSource ds = new SQLServerDataSource();
		//ds.setIntegratedSecurity(true);
		/*ds.setServerName("10.201.43.101");
		ds.setPortNumber(1433); 
		ds.setDatabaseName("PaymentDB");
		ds.setUser("paymentdev");
		ds.setPassword("paymentdev");*/
		
		ds.setServerName(hostname);
		ds.setPortNumber(Integer.parseInt(portNumber)); 
		ds.setDatabaseName(str_DB_Name);
		ds.setUser(str_DB_user);
		ds.setPassword(str_DB_pass);
		try {
			con = ds.getConnection();
		} catch (SQLServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;
	}
	
	public static long callSQLPROCForIPGBatchID(String str_xml) throws Exception{

		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;		
		long iseq =0;
		
			con = getSQLDBConnection();
			cstmt = con.prepareCall("{call dbo.USP_GET_ID_FOR_BATCHID(?)}");
			//cstmt.setString(1, str_xml);
			cstmt.registerOutParameter(1, Types.BIGINT);
			

			cstmt.execute();

			 iseq = (long) cstmt.getObject(1);
			m_log.info("iseq "+iseq);	
			
			

		return iseq;
	}
	public static long callSQLPROCForIPGBatchID_1(String pid) throws Exception{

		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;		
		long iseq =0;
		
			con = getSQLDBConnection();
			cstmt = con.prepareCall("{call dbo.USP_GET_ID_FOR_BATCHID_For_PID(?,?)}");
			cstmt.setString(1, pid);
			cstmt.registerOutParameter(2, Types.BIGINT);
			

			cstmt.execute();

			 iseq = (long) cstmt.getObject(2);
			m_log.info("iseq "+iseq);	
			
			

		return iseq;
	}
	public static long callSQLPROC_USP_UPDATE_STAUS_SETTLEMENT(long seqNumber, String str_resCode, String str_response_string) throws Exception{

		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;		
		long iseq =0;
		int i_resCode = Integer.parseInt(str_resCode);
		
			
			con = getSQLDBConnection();
			cstmt = con.prepareCall("{call dbo.USP_UPDATE_STAUS_SETTLEMENT(?,?,?,?,?)}");
			//cstmt.setString(1, str_xml);
			cstmt.setLong(1, seqNumber);			
			cstmt.setInt(2, i_resCode);
			cstmt.setString(3, str_response_string);
			cstmt.registerOutParameter(4, Types.INTEGER);
			cstmt.registerOutParameter(5, Types.SMALLINT);
			

			cstmt.execute();

			 iseq = (int) cstmt.getObject(4);
			m_log.info("response for USP_UPDATE_STAUS_SETTLEMENT "+iseq);	
			if(cstmt.getObject(5)!= null){
			 iseq = (long) cstmt.getObject(5);
			 m_log.info("erroce code  for USP_UPDATE_STAUS_SETTLEMENT "+iseq);	
			}
			
			

		
		return iseq;
	}
	public static ArrayList callSQLPROC_USP_WAITING_FOR_RESPONSE(String str_projectID) throws Exception{

		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;		
		long iseq =0;
		ArrayList str_wait_details = new ArrayList();
		
			con = getSQLDBConnection();
			cstmt = con.prepareCall("{call dbo.USP_WAITING_FOR_RESPONSE(?,?)}");			
			cstmt.setString(1, str_projectID);			
			//cstmt.registerOutParameter(2, Types.REF_CURSOR);
			cstmt.registerOutParameter(2, Types.INTEGER);
			

			cstmt.execute();

			 //iseq = (long) cstmt.getObject(3);
			m_log.info("response for USP_WAITING_FOR_RESPONSE "+iseq);	
			
			
			rs =  cstmt.getResultSet();
			String str_wait_detail ="";
			if (rs==null){
				//if(cstmt.getInt(2) != 0){
					//int res_code =(int) cstmt.getObject(2);
					int res_code =cstmt.getInt(2);
					m_log.info("response code forthe call  USP_WAITING_FOR_RESPONSE: "+res_code);
					if(res_code != 0){
						m_log.info("Error response code forthe call  USP_WAITING_FOR_RESPONSE: "+res_code);
					}
				//}
			}
			while (rs.next()){		
				str_wait_detail = "";
				String str_prject_code = rs.getString(1);
				long seq_number = rs.getLong(2);
				m_log.info("project Code "+str_prject_code);	
				m_log.info(" and Sequence number: "+seq_number);
				str_wait_detail = str_prject_code+"~"+seq_number+"";
				str_wait_details.add(str_wait_detail);
			}
			

		
		return str_wait_details;
	}
	public static String callSQLPROCForIPGSeQIDXML(String str_xml) throws Exception{

		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;		
		int iseq =0;
		String str_SP_modified_xml = "";
		m_log.info(" calling USP_LOAD_SETTLEMENT starting ");

		con = getSQLDBConnection();
		cstmt = con.prepareCall("{call dbo.USP_LOAD_SETTLEMENT(?,?,?,?)}");
		cstmt.setString(1, str_xml);
		cstmt.registerOutParameter(2, Types.INTEGER);
		cstmt.registerOutParameter(3, Types.LONGVARCHAR);
		cstmt.registerOutParameter(4, Types.INTEGER);

		m_log.info(" calling USP_LOAD_SETTLEMENT starting to execute ");
		cstmt.execute();
		m_log.info(" calling USP_LOAD_SETTLEMENT completed"
				+ " ");
		if(cstmt.getObject(2) != null){
			iseq = (Integer) cstmt.getObject(2);
			m_log.info("status for call "+iseq);	

			if(iseq != 0){
				m_log.info("Error response code for the call  USP_LOAD_SETTLEMENT: "+iseq);
				throw new Exception("Error response code forthe call  USP_LOAD_SETTLEMENT: "+iseq);
			}
		}
		str_SP_modified_xml =  cstmt.getString(3);
		
		if(cstmt.getObject(4) != null){
			int res_code =(int) cstmt.getObject(4);
			m_log.info("response code for the call  USP_LOAD_SETTLEMENT: "+res_code);
			if(res_code != 0){
				m_log.info("Error response code for the call  USP_LOAD_SETTLEMENT: "+res_code);
				throw new Exception("Error response code forthe call  USP_LOAD_SETTLEMENT: "+res_code);
			}
		}

		return str_SP_modified_xml;
	}
	
	public static String callSQLPROCForUSP_LOAD_RESPONSE(String str_xml) throws Exception{

		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;		
		int iseq =0;
		String str_SP_modified_xml = "";
		m_log.info(" calling USP_LOAD_RESPONSE starting ");
					con = getSQLDBConnection();
			cstmt = con.prepareCall("{call dbo.USP_LOAD_RESPONSE(?,?,?)}");
			cstmt.setString(1, str_xml);
			cstmt.registerOutParameter(2, Types.INTEGER);
			cstmt.registerOutParameter(3, Types.INTEGER);
						
			m_log.info(" calling USP_LOAD_RESPONSE starting to execute ");
			cstmt.execute();
			m_log.info(" calling USP_LOAD_RESPONSE completed"
					+ " ");
			if(cstmt.getObject(2) != null){
			 iseq = (Integer) cstmt.getObject(2);
			 m_log.info("iseq "+iseq);
			}
			if(cstmt.getObject(3) != null){
				 iseq = (Integer) cstmt.getObject(3);
				 m_log.info("err: "+iseq);
				 throw new Exception("Exception from DB for call to USP_LOAD_RESPONSE. Error code from DB: "+iseq);
				}
			
			//str_SP_modified_xml =  cstmt.getString(3);

		
		return str_SP_modified_xml;
	}

	public static String callSQLPROCForUSP_PROVIDE_RESPONSE(String str_ProjectID, int strBatchID) throws Exception{

		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;		
		int iseq =0;
		String str_SP_xml = "";
		m_log.info(" calling USP_PROVIDE_RESPONSE starting ");
				con = getSQLDBConnection();
			cstmt = con.prepareCall("{call dbo.USP_PROVIDE_RESPONSE(?,?,?,?)}");
			cstmt.setString(2, str_ProjectID);
			cstmt.setInt(1, strBatchID);
			//cstmt.registerOutParameter(3, Types.LONGNVARCHAR);
			cstmt.registerOutParameter(3, Types.SQLXML);
			cstmt.registerOutParameter(4, Types.INTEGER);
			//cstmt.registerOutParameter(3, Types.INTEGER);
						
			m_log.info(" calling USP_PROVIDE_RESPONSE starting to execute ");
			cstmt.execute();
			m_log.info(" calling USP_PROVIDE_RESPONSE completed"
					+ " ");
			if(cstmt.getObject(3) != null){
				SQLXML sqlxml__SP_xml = (SQLXML) cstmt.getObject(3);
				str_SP_xml = sqlxml__SP_xml.getString();
			 m_log.info("XML Rsponse to clinet "+str_SP_xml);
			}
			if(cstmt.getObject(4) != null){
				 iseq = (Integer) cstmt.getObject(4);
				 m_log.info("err: "+iseq);
				}
			
			//str_SP_modified_xml =  cstmt.getString(3);

		return str_SP_xml;
	}

}
