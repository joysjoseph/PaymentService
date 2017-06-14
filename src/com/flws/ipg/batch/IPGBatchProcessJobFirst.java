package com.flws.ipg.batch;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import com.flws.ipg.batch.helper.AzureQueueProcessHelper;
import com.flws.ipg.batch.util.DBUtil;
import com.flws.ipg.batch.util.DataLoadPropertyUtil;
import com.flws.ipg.batch.util.IPGBatchLogUtil;
import com.flws.ipg.batch.util.MailUtil;

import noNamespace.BATDocument;
import noNamespace.BATDocument.BAT;
import noNamespace.BATDocument.BAT.TRX;
import noNamespace.PaymentBatchRequestDocument;
import noNamespace.PaymentBatchRequestDocument.PaymentBatchRequest;
import noNamespace.PaymentBatchRequestDocument.PaymentBatchRequest.PaymentBatchRecord;
import noNamespace.PaymentBatchResponseDocument;
import noNamespace.PaymentBatchResponseDocument.PaymentBatchResponse;

public class IPGBatchProcessJobFirst {
	static org.apache.log4j.Logger  m_log = org.apache.log4j.Logger.getLogger(IPGBatchProcessJobFirst.class);
	public static void main(String[] args) {
		IPGBatchLogUtil.getCurrent(IPGBatchProcessJobFirst.class);
		IPGBatchLogUtil.initLogger("");
		m_log.info(" Starting----  : " );
		boolean firststatus = false;
		boolean secondstatus = false;
		
		if(args != null && args.length > 0){
			String str_param1 = (String)args[0];
			if(str_param1.equalsIgnoreCase("FIRST")){
				firststatus = true;
			}
			else if(str_param1.equalsIgnoreCase("SECOND")){
				secondstatus = true;
			}
		}
		String host = DataLoadPropertyUtil.getFuctionalParameterValue("MAIL_HOST", "");
		String to = DataLoadPropertyUtil.getFuctionalParameterValue("MAIL_RECIPIENTS", "");
		String from = DataLoadPropertyUtil.getFuctionalParameterValue("MAIL_FROM", "");
		
		String strXmlMessage = "";
		//DBUtil.callSQLPROC_USP_UPDATE_STAUS_SETTLEMENT(201, "333", "assigned error");
		/*try {
			DBUtil.callSQLPROC_USP_WAITING_FOR_RESPONSE("FLOATLAS");
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}*/
		//DBUtil.callSQLPROCForUSP_PROVIDE_RESPONSE("FLOATLAS", 149);
		String str_xml="<BAT><SRC>FLOATLAS</SRC><SEQ>201</SEQ><CNT>7</CNT><TRX><SVC>Collect</SVC><PRJ>FLOFLOWERS</PRJ><CTY>US</CTY><ORD>WA3444503</ORD><CUR>USD</CUR><NET>5.1</NET><TAX>0.0</TAX><GRS>5.1</GRS><XIE>304</XIE><XRF>2F84T914RVTC1</XRF><TL0>259580</TL0><TL1>1001</TL1></TRX><TRX><SVC>Collect</SVC><PRJ>FLOFLOWERS</PRJ><CTY>US</CTY><ORD>WA3444503</ORD><CUR>USD</CUR><NET>5.1</NET><TAX>0.0</TAX><GRS>5.1</GRS><XIE>305</XIE><XRF>2F84T914RVTC1</XRF><TL0>259580</TL0><TL1>1001</TL1></TRX><TRX><SVC>Collect</SVC><PRJ>FLOFLOWERS</PRJ><CTY>US</CTY><ORD>WA3444503</ORD><CUR>USD</CUR><NET>1.1</NET><TAX>0.0</TAX><GRS>1.1</GRS><XIE>306</XIE><XRF>2F84T914RVTC1</XRF><TL0>259580</TL0><TL1>1002</TL1></TRX><TRX><SVC>Collect</SVC><PRJ>FLOFLOWERS</PRJ><CTY>US</CTY><ORD>WA3444503</ORD><CUR>USD</CUR><NET>1.1</NET><TAX>0.0</TAX><GRS>1.1</GRS><XIE>307</XIE><XRF>2F84T914RVTC1</XRF><TL0>259580</TL0><TL1>1002</TL1></TRX><TRX><SVC>Collect</SVC><PRJ>FLOFLOWERS</PRJ><CTY>US</CTY><ORD>WA3456</ORD><CUR>USD</CUR><NET>5.1</NET><TAX>0.0</TAX><GRS>5.1</GRS><XIE>308</XIE><XRF>Y22YSSJW1GTC5</XRF><TL0>259580</TL0><TL1>1001</TL1></TRX><TRX><SVC>Collect</SVC><PRJ>FLOFLOWERS</PRJ><CTY>US</CTY><ORD>WA345603</ORD><CUR>USD</CUR><NET>5.1</NET><TAX>0.0</TAX><GRS>5.1</GRS><XIE>309</XIE><XRF>DFMJHYCVR3TC1</XRF><TL0>259580</TL0><TL1>1001</TL1></TRX><TRX><SVC>Collect</SVC><PRJ>FLOFLOWERS</PRJ><CTY>US</CTY><ORD>WA345613</ORD><CUR>USD</CUR><NET>5.1</NET><TAX>0.0</TAX><GRS>5.1</GRS><XIE>310</XIE><XRF>84A1FABJCZTC1</XRF><TL0>259580</TL0><TL1>1001</TL1></TRX></BAT>";
		//sendBatchToIPG(str_xml);
		System.setProperty("javax.net.ssl.keyStore","C:\\joys\\IIB9PJTS\\IBMPayment\\certs\\brkKeystore.jks");  //CITIAUTH.cer  ANLCert.cer
		System.setProperty("javax.net.ssl.trustStore","C:\\joys\\IIB9PJTS\\IBMPayment\\certs\\brktrustKeystore.jks");  //CITIAUTH.cer  ANLCert.cer

		System.setProperty("javax.net.ssl.keyStorePassword","password");  //CITIAUTH.cer  ANLCert.cer
		System.setProperty("javax.net.ssl.trustStorePassword","password");  
		System.setProperty("javax.net.ssl.trustStoreType","jks"); 
		System.setProperty("javax.net.ssl.keyStoreType","jks");

		System.setProperty("https.protocols", "TLSv1.2");
		
		
		
		if(secondstatus){
			try{
				ArrayList arr_str_wait_details = DBUtil.callSQLPROC_USP_WAITING_FOR_RESPONSE("FLOATLAS");
				if(arr_str_wait_details != null && arr_str_wait_details.size() > 0){
					m_log.info("No Of batches waiting for Response from IPG: "+arr_str_wait_details.size());
					for(int iarrwait=0;iarrwait < arr_str_wait_details.size();iarrwait++){
						String str_wait_detail = "";
						try{
							str_wait_detail = (String)arr_str_wait_details.get(iarrwait);
							if(str_wait_detail != null && str_wait_detail.trim().length()  > 0){
								String[] arr_wait_pjt_seq = str_wait_detail.split("~");
								if(arr_wait_pjt_seq != null && arr_wait_pjt_seq.length > 1){
									
									m_log.info("Getting Batch Response for Seq.number.."+arr_wait_pjt_seq[1]);
									String str_batch_response_IPG = getBatchResponseFromIPG(arr_wait_pjt_seq[1],arr_wait_pjt_seq[0]);
									if(str_batch_response_IPG != null && str_batch_response_IPG.length() > 0 && !str_batch_response_IPG.startsWith("<ERR>")){
										if( str_batch_response_IPG.contains("<BST>Created</BST>")){
											m_log.info("Batch staus: Created...");
										}
										if( str_batch_response_IPG.contains("<BST>Submitted</BST>")){
											m_log.info("Batch staus: Susbmitted...");
										}
										else if( str_batch_response_IPG.contains("<BST>Completed</BST>")){
											m_log.info("Batch staus: Completed...Processing response");
											String str_formatted_res_xml = getFLWSXMLFROMXMLResponse(str_batch_response_IPG);
											m_log.info("Formatted batch response XML to SQL DB "+str_formatted_res_xml);
											DBUtil.callSQLPROCForUSP_LOAD_RESPONSE(str_formatted_res_xml);
										}
									}
								}
							}

						}catch (Exception e) {
							e.printStackTrace();
							m_log.error("Exception in getting message from IPG "+e.getMessage());
							MailUtil.sendAlertMail(host, to, "IPG Batch process Response fail", from, str_wait_detail+"Exception in getting message from IPG"+e.getMessage());	
						}
					}
				}
				else{
					m_log.info("No batches waiting for Response from IPG ");
				}

			}
			catch (Exception e) {
				MailUtil.sendAlertMail(host, to, "IPG Batch process Response fail", from, "Exception in getting pending seq.numbers from DB"+e.getMessage());
			}
		}
		while(firststatus){
			
			try {
				strXmlMessage = AzureQueueProcessHelper.getMessageFromQueue();
			} catch (Exception e1) {
				m_log.error("Exception in getting message from Queue "+e1.getMessage());
				e1.printStackTrace();
				MailUtil.sendAlertMail(host, to, "IPG Batch process fail", from, "Exception in getting message from Queue"+e1.getMessage());	
				break;
			}
			if(strXmlMessage == null ||strXmlMessage.trim().length() < 0 || strXmlMessage.equals("FALSE")){
				m_log.info("Final Message or empty queue, Skipping");
				//sendBatchToIPG(str_xml);
				break;
				/*try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
			
			if(strXmlMessage.contains("<BatchID>")){
				try {
					PaymentBatchRequestDocument paymentBatchRequestDocument;

					
					String str_SP_modified_Xml = DBUtil.callSQLPROCForIPGSeQIDXML(strXmlMessage);
					m_log.info("XML Message from stroed Procedure: "+str_SP_modified_Xml);
					
					paymentBatchRequestDocument = PaymentBatchRequestDocument.Factory.parse(str_SP_modified_Xml);

					PaymentBatchRequest paymentBatchRequest = paymentBatchRequestDocument.getPaymentBatchRequest();
					m_log.info("Sequence number from stroed Procedure: "+paymentBatchRequest.getSeqNumber());
					//long batchid=  paymentBatchRequest.getBatchID();
					try{
						BATDocument batchdoc =getBATDocFromQueueMessage(paymentBatchRequest);
						if(batchdoc==null || batchdoc.isNil()){
							m_log.error("Sequence number: "+paymentBatchRequest.getSeqNumber()+" Failed as Wromng XML from Strored procedure. <BAT> Doc is null or empty");						
							throw new Exception("Sequence number: "+paymentBatchRequest.getSeqNumber()+" Failed.Exception while processing the message from DB stored proc:Wromng XML from Strored procedure. Unable to create IPG bat xml.");
						}
						m_log.info("IPG XML Message using XML from stored proc: "+batchdoc.xmlText());
						String str_res_message = sendBatchToIPG(batchdoc.xmlText());
						m_log.info("IPG XML Message Send to IPG system:");					
						m_log.info("Response message from IPG Sysytem: "+str_res_message);
						long l_seq = paymentBatchRequest.getSeqNumber();
						long dbupdateStatus;
						if(str_res_message.contains("<ERR>")){
							String str_resCode = str_res_message.substring(str_res_message.indexOf("<RCD>")+"<RCD>".length(), str_res_message.indexOf("</RCD>"));
							String str_res_err_message =  str_res_message.substring(str_res_message.indexOf("<MSG>")+"<MSG>".length(), str_res_message.indexOf("</MSG>"));
							dbupdateStatus = DBUtil.callSQLPROC_USP_UPDATE_STAUS_SETTLEMENT(l_seq, "2", "Res code:"+str_resCode+" Response Message"+str_res_err_message);
							m_log.info("batch submission Status for seq."+l_seq+" - error Code "+str_resCode);
							m_log.info("batch submission Status for seq. "+l_seq+" - error message "+str_res_err_message);
							m_log.info("batch submission Status Updated to IPG DB for the seq number: "+l_seq);
							m_log.info("batch submission Status Updated to IPG DB status: "+dbupdateStatus);

						}
						else{
							dbupdateStatus = DBUtil.callSQLPROC_USP_UPDATE_STAUS_SETTLEMENT(l_seq, "0", "");
							m_log.info("batch submission Status Updated to IPG DB for the seq number: "+l_seq);
							m_log.info("batch submission Status Updated to IPG DB status: "+dbupdateStatus);
						}

					}catch (Exception e) {
						long seq_number = paymentBatchRequest.getSeqNumber();
						m_log.info("batch submission Status for seq."+seq_number+" failed. updating DB...");
						 DBUtil.callSQLPROC_USP_UPDATE_STAUS_SETTLEMENT(seq_number, "1", "Unexpected error"+e.getMessage());
						 throw new Exception("Excpetion While processining the Message from DB, that has Sequence number:"+seq_number);
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					m_log.error("Excpetion While processining the Message from Queue: "+e.getMessage());
					MailUtil.sendAlertMail(host, to, "IPG Batch process fail", from, "Exception in processing XML Message"+e.getMessage());
				}
			}
			else{
				m_log.info("invalid xml"+strXmlMessage);
				MailUtil.sendAlertMail(host, to, "IPG Batch process fail", from, "Ivalid XML message received from Queue");	
			}
		}
		System.out.println("Completed FIRST Job");

	}
	
	
	public static BATDocument  getBATDocFromQueueMessage(PaymentBatchRequest paymentBatchRequest ){

		String str_data_line = "";
		BATDocument batdoc = null;
		BufferedReader br = null;
		FileReader fr = null;

		
			

			String sCurrentLine;
			
			batdoc = BATDocument.Factory.newInstance();
			BAT bat = batdoc.addNewBAT();
			bat.setCNT(paymentBatchRequest.getTransactionCount()+"");
			bat.setSEQ(paymentBatchRequest.getSeqNumber()+"");
			bat.setSRC(paymentBatchRequest.getPID());
			PaymentBatchRecord[] arr_paymentBatchRecord = paymentBatchRequest.getPaymentBatchRecordArray();
			
			if(arr_paymentBatchRecord != null && arr_paymentBatchRecord.length > 0){
				bat.setCNT(arr_paymentBatchRecord.length+"");
				for(int ibatch_r=0;ibatch_r < arr_paymentBatchRecord.length;ibatch_r++){
					PaymentBatchRecord paymentBatchRecord = arr_paymentBatchRecord[ibatch_r];
					if(paymentBatchRecord != null){
						TRX trx = bat.addNewTRX();
						trx.setSVC(paymentBatchRecord.getTransactionType());
						trx.setPRJ("");
						trx.setCTY("US");
						trx.setORD(paymentBatchRecord.getMerchantOrderNumber()+"");
						trx.setCUR(paymentBatchRecord.getCurrency());
						trx.setTAX("0.0");
						trx.setNET(paymentBatchRecord.getAmount()+"");
						trx.setGRS(paymentBatchRecord.getAmount()+"");
						trx.setXIE(paymentBatchRecord.getBatchDetailId()+"");
						trx.setXRF(paymentBatchRecord.getMerchantTxId());
						trx.setTL0(paymentBatchRecord.getTransactionLable0());
						trx.setTL1(paymentBatchRecord.getBrandCode());
					}
				}
			}
			
			
		
		return batdoc;
	}
	
	public static void  getTRXDocFromDataLine(PaymentBatchRequest paymentBatchRequest,String str_data_line,TRX trx){


		//TRX trx = null;
		String[] str_arr_data_line = str_data_line.split("~"); 
		if(str_arr_data_line != null && str_arr_data_line.length > 0){
			///trx = TRX.Factory.newInstance();
			trx.setSVC(str_arr_data_line[1]);
			trx.setPRJ(str_arr_data_line[5]);
			trx.setCTY("US");
			trx.setORD(str_arr_data_line[7]);
			trx.setCUR(str_arr_data_line[4]);
			trx.setTAX("0.0");
			trx.setNET(str_arr_data_line[3]);
			trx.setGRS(str_arr_data_line[3]);
			trx.setXIE(str_arr_data_line[8]);
			trx.setXRF(str_arr_data_line[9]);
			trx.setTL0(str_arr_data_line[6]);
			trx.setTL1(str_arr_data_line[2]);
		}
		//return trx;
	}
	
	public static String sendBatchToIPG(String str_requestXML) throws Exception{

		if(str_requestXML == null || str_requestXML.length() <=0){
			return "";
		}
		
		String ipgToken = "";
		String str_responseXML=""; 
	//str_requestXML = "<BAT><SRC>FLOATLAS</SRC><SEQ>198</SEQ><CNT>7</CNT><TRX><SVC>Collect</SVC><PRJ>FLOFLOWERS</PRJ><CTY>US</CTY><ORD>WA3444503</ORD><CUR>USD</CUR><NET>5.1</NET><TAX>0.0</TAX><GRS>5.1</GRS><XIE>304</XIE><XRF>2F84T914RVTC1</XRF><TL0>259580</TL0><TL1>1001</TL1></TRX><TRX><SVC>Collect</SVC><PRJ>FLOFLOWERS</PRJ><CTY>US</CTY><ORD>WA3444503</ORD><CUR>USD</CUR><NET>5.1</NET><TAX>0.0</TAX><GRS>5.1</GRS><XIE>305</XIE><XRF>2F84T914RVTC1</XRF><TL0>259580</TL0><TL1>1001</TL1></TRX><TRX><SVC>Collect</SVC><PRJ>FLOFLOWERS</PRJ><CTY>US</CTY><ORD>WA3444503</ORD><CUR>USD</CUR><NET>1.1</NET><TAX>0.0</TAX><GRS>1.1</GRS><XIE>306</XIE><XRF>2F84T914RVTC1</XRF><TL0>259580</TL0><TL1>1002</TL1></TRX><TRX><SVC>Collect</SVC><PRJ>FLOFLOWERS</PRJ><CTY>US</CTY><ORD>WA3444503</ORD><CUR>USD</CUR><NET>1.1</NET><TAX>0.0</TAX><GRS>1.1</GRS><XIE>307</XIE><XRF>2F84T914RVTC1</XRF><TL0>259580</TL0><TL1>1002</TL1></TRX><TRX><SVC>Collect</SVC><PRJ>FLOFLOWERS</PRJ><CTY>US</CTY><ORD>WA3456</ORD><CUR>USD</CUR><NET>5.1</NET><TAX>0.0</TAX><GRS>5.1</GRS><XIE>308</XIE><XRF>Y22YSSJW1GTC5</XRF><TL0>259580</TL0><TL1>1001</TL1></TRX><TRX><SVC>Collect</SVC><PRJ>FLOFLOWERS</PRJ><CTY>US</CTY><ORD>WA345603</ORD><CUR>USD</CUR><NET>5.1</NET><TAX>0.0</TAX><GRS>5.1</GRS><XIE>309</XIE><XRF>DFMJHYCVR3TC1</XRF><TL0>259580</TL0><TL1>1001</TL1></TRX><TRX><SVC>Collect</SVC><PRJ>FLOFLOWERS</PRJ><CTY>US</CTY><ORD>WA345613</ORD><CUR>USD</CUR><NET>5.1</NET><TAX>0.0</TAX><GRS>5.1</GRS><XIE>310</XIE><XRF>84A1FABJCZTC1</XRF><TL0>259580</TL0><TL1>1001</TL1></TRX></BAT>";
		
		String url = DataLoadPropertyUtil.getFuctionalParameterValue("AZ_IPG_URL", "");
		url = url+"/bat";
				//"https://ips-preprod.ihost.com:50442/bat";//IPGPaymentUtil.getFuctionalParameterValue("IPG_URL","");//"https://ips-preprod.ihost.com:50443/tkn";
		//url="https://tmcpnydev01:8443/TokenManagerEngine/services/TokenService2.0";
		System.setProperty("javax.net.ssl.keyStore","C:\\joys\\IIB9PJTS\\IBMPayment\\certs\\brkKeystore.jks");  //CITIAUTH.cer  ANLCert.cer
		System.setProperty("javax.net.ssl.trustStore","C:\\joys\\IIB9PJTS\\IBMPayment\\certs\\brktrustKeystore.jks");  //CITIAUTH.cer  ANLCert.cer

		System.setProperty("javax.net.ssl.keyStorePassword","password");  //CITIAUTH.cer  ANLCert.cer
		System.setProperty("javax.net.ssl.trustStorePassword","password");  
		System.setProperty("javax.net.ssl.trustStoreType","jks"); 
		System.setProperty("javax.net.ssl.keyStoreType","jks");

		System.setProperty("https.protocols", "TLSv1.2");
		URL obj;
		try {
			obj = new URL(url);

			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();	

			con.setRequestMethod("POST");	

			con.setRequestProperty("Accept", "application/xml");
			con.setRequestProperty("Content-Type", "application/xml");	

			con.setDoOutput(true);
			con.setUseCaches(true);
			
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			m_log.info("uuuuuuuuuuuuuuuuuuuu: "+con.getCipherSuite());
			//m_log.info("Request XML:"+str_requestXML);
			wr.writeBytes(str_requestXML);
			wr.flush();
			wr.close();


			int responseCode = con.getResponseCode();
			m_log.info("\nSending 'POST' request to URL : " + url);
			//m_log.info("Post parameters : " + urlParameters);
			m_log.info("Response Code : " + responseCode);

			if(responseCode == 200){
				m_log.info(" Response Code :----> " + responseCode);
				BufferedReader in = new BufferedReader(
						new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				//print result
				str_responseXML = response.toString();
				m_log.info(response.toString());
				/*if(str_responseXML.contains("<TKI>")){
					ipgToken =  str_responseXML.substring(str_responseXML.indexOf("<TKI>")+"<TKI>".length(), str_responseXML.indexOf("</TKI>"));
				}*/
			}
			else{
				m_log.info(" Response Code :----> " + responseCode);
				BufferedReader in = new BufferedReader(
						new InputStreamReader(con.getErrorStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				//print result
				str_responseXML = response.toString();
				m_log.info(response.toString());
				//card_number =  str_responseXML.substring(str_responseXML.indexOf("<token>")+"<token>".length(), str_responseXML.indexOf("</token>"));
				str_responseXML =  response.toString();//"FALSE~IPG Tokenization Failed with HTTP Status code "+responseCode;

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			str_responseXML =  "FALSE~IPG Batch request Failed with Exception "+e.getMessage();
			throw new Exception("Batch request submission to IPG Failed with Exception:"+e.getMessage());
		}




		return str_responseXML;

	}
	
	public static String getFLWSXMLFROMXMLResponse(String strXML) throws Exception{
		String str_response_line = "";
		String str_tld ="~";
		PaymentBatchResponseDocument paymentBatchResponseDoc  = null;
		
			BATDocument batdoc = BATDocument.Factory.parse(strXML);
			BAT batch = batdoc.getBAT();
			TRX[] arr_trx = batch.getTRXArray();
			
			paymentBatchResponseDoc = PaymentBatchResponseDocument.Factory.newInstance();
			PaymentBatchResponse paymentBatchResponse = paymentBatchResponseDoc.addNewPaymentBatchResponse();
			paymentBatchResponse.setPID(batch.getSRC());
			paymentBatchResponse.setSeqNumber(batch.getSEQ()+"");
			paymentBatchResponse.setTransactionCount(batch.getCNT());
			
			if(arr_trx != null && arr_trx.length > 0){
				for (int iarr=0;iarr<arr_trx.length;iarr++){
					noNamespace.PaymentBatchResponseDocument.PaymentBatchResponse.PaymentBatchRecord resPaymentBatchRecord = paymentBatchResponse.addNewPaymentBatchRecord();
					resPaymentBatchRecord.setBatchDetailID(arr_trx[iarr].getXIE());
					if(arr_trx[iarr].getRES() != null){
						resPaymentBatchRecord.setLongMessage(arr_trx[iarr].getRES().getLLM());;
						resPaymentBatchRecord.setMerchantTxId(arr_trx[iarr].getXID());
						resPaymentBatchRecord.setReasonCode(arr_trx[iarr].getRES().getRCD());
						resPaymentBatchRecord.setReasonMessage(arr_trx[iarr].getRES().getMSG());
					}
				}
			}
		
		XmlOptions xmlops = new XmlOptions();
		xmlops.setUseDefaultNamespace();
		HashMap dnsMap = new HashMap();
		dnsMap.put("", "http://www.w3.org/2001/XMLSchema-instance");
		xmlops.setSaveImplicitNamespaces(dnsMap);

		xmlops.setSavePrettyPrint();
	      xmlops.setSaveNamespacesFirst();

		return paymentBatchResponseDoc.xmlText(xmlops);

	}
	
	public static String getBatchResponseFromIPG(String str_seq_number, String src) throws Exception{
		String str_responseXML = "";	
		String url = DataLoadPropertyUtil.getFuctionalParameterValue("AZ_IPG_URL", "");
		url =url +"/bat/"+src+"/"+str_seq_number;
		//String url = "https://ips-preprod.ihost.com:50442/bat/"+src+"/"+str_seq_number;
		//url="https://tmcpnydev01:8443/TokenManagerEngine/services/TokenService2.0";
		System.setProperty("javax.net.ssl.keyStore","C:\\joys\\IIB9PJTS\\IBMPayment\\certs\\brkKeystore.jks");  //CITIAUTH.cer  ANLCert.cer
		System.setProperty("javax.net.ssl.trustStore","C:\\joys\\IIB9PJTS\\IBMPayment\\certs\\brktrustKeystore.jks");  //CITIAUTH.cer  ANLCert.cer

		System.setProperty("javax.net.ssl.keyStorePassword","password");  //CITIAUTH.cer  ANLCert.cer
		System.setProperty("javax.net.ssl.trustStorePassword","password");  
		System.setProperty("javax.net.ssl.trustStoreType","jks"); 
		System.setProperty("javax.net.ssl.keyStoreType","jks");

		System.setProperty("https.protocols", "TLSv1.2");
		URL obj;
		try {
			obj = new URL(url);

			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			//HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			m_log.info("\nSending 'POST' request to URL : " + url);
			//m_log.info("Post parameters : " + urlParameters);
			m_log.info("Response Code : " + responseCode);

			if(responseCode == 200){
				m_log.info(" Response Code :----> " + responseCode);
				BufferedReader in = new BufferedReader(
						new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				//print result
				str_responseXML = response.toString();
				m_log.info(response.toString());
				/*if(str_responseXML.contains("<TKI>")){
					ipgToken =  str_responseXML.substring(str_responseXML.indexOf("<TKI>")+"<TKI>".length(), str_responseXML.indexOf("</TKI>"));
				}*/
			}
			else{
				m_log.info(" Response Code :----> " + responseCode);
				BufferedReader in = new BufferedReader(
						new InputStreamReader(con.getErrorStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				//print result
				str_responseXML = response.toString();
				m_log.info(response.toString());
				//card_number =  str_responseXML.substring(str_responseXML.indexOf("<token>")+"<token>".length(), str_responseXML.indexOf("</token>"));
				//str_responseXML =  "FALSE~IPG Tokenization Failed with HTTP Status code "+responseCode;

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			str_responseXML =  "FALSE~IPG Response request Failed with Exception "+e.getMessage();
			throw new Exception ("IPG Response request Failed with Exception "+e.getMessage());
		}

		return str_responseXML;
	}
	
}
