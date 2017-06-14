package com.vogella.jersey.first;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.SecurityContext;

import org.apache.xmlbeans.XmlException;

import com.microsoft.windowsazure.services.servicebus.*;
import com.microsoft.windowsazure.services.servicebus.models.*;
import com.flws.ipg.batch.IPGBatchProcessJobFirst;
import com.flws.ipg.batch.helper.AzureQueueProcessHelper;
import com.flws.ipg.batch.util.DBUtil;
import com.flws.ipg.batch.util.IPGBatchLogUtil;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.core.*;
import com.microsoft.windowsazure.exception.ServiceException;

import noNamespace.PaymentBatchRequestDocument;
import noNamespace.PaymentBatchRequestDocument.PaymentBatchRequest;
import noNamespace.PaymentBatchRequestDocument.PaymentBatchRequest.PaymentBatchRecord;
import noNamespace.PaymentBatchResponseDocument;
import noNamespace.PaymentBatchResponseDocument.PaymentBatchResponse;

import javax.ws.rs.*;
// Plain old Java Object it does not extend as class or implements
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello

@Path("/ipgbatch")
public class Hello {
	static org.apache.log4j.Logger  m_log = org.apache.log4j.Logger.getLogger(Hello.class);
	// This method is called if TEXT_PLAIN is request
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHello() {
		return "Hello Jersey";
	}

	public static void initIPgloging(){
		IPGBatchLogUtil.getCurrent(Hello.class);
		IPGBatchLogUtil.initLogger("");
		m_log.info(" Starting----  : " );
	}
	// This method is called if XML is request
	@GET
	@Produces(MediaType.TEXT_XML)
	public String sayXMLHello() {
		return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
	}
	//This method is called if XML is request
	@POST
	@Path("/batch")
	@Consumes({ "application/xml" })
	@Produces({ "application/xml" })
	public String processXMLBatchRequest(String str_paymentBatchRequest) {	

		initIPgloging();
		PaymentBatchRequestDocument paymentBatchRequestDocument = null;
		PaymentBatchResponseDocument paymentBatchResponseDocument = null;
		paymentBatchResponseDocument = PaymentBatchResponseDocument.Factory.newInstance();
		PaymentBatchRequest paymentBatchRequest = null;
		PaymentBatchResponse paymentBatchResponse = paymentBatchResponseDocument.addNewPaymentBatchResponse();
		try { 
			m_log.info(" Starting to process batch request XML: ");
			m_log.info(" Request XML: "+str_paymentBatchRequest);
			 
			
			paymentBatchRequestDocument = PaymentBatchRequestDocument.Factory.parse(str_paymentBatchRequest);
			m_log.info(" Request XML parsing completed: ");
			
			 paymentBatchRequest = paymentBatchRequestDocument.getPaymentBatchRequest();
			String strresponse_error =validateRequest(paymentBatchRequestDocument);
			if(strresponse_error != null && strresponse_error.length() >0){
				return strresponse_error;
			}
			m_log.info(" Request XML Batch ID: "+ paymentBatchRequest.getBatchID());
			long seqId=  DBUtil.callSQLPROCForIPGBatchID_1(paymentBatchRequest.getPID());
			m_log.info(" Batch ID from  SQL: "+ seqId);
			paymentBatchRequest.setBatchID(seqId);
			AzureQueueProcessHelper.sendMessageToQueue(paymentBatchRequestDocument.xmlText());
			m_log.info(" XML message Send to Queue and New batch ID for response"+ seqId);
			
			paymentBatchResponse.setPID(paymentBatchRequest.getPID());
			paymentBatchResponse.setRespCode("0");
			paymentBatchResponse.setRespMsg("Success");
			paymentBatchResponse.setTransactionCount(paymentBatchRequest.getTransactionCount()+"");
			paymentBatchResponse.setBatchId(paymentBatchRequest.getBatchID()+"");
			
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			m_log.info("Error while processing the request: "+e.getMessage());
			if(paymentBatchRequest != null){
				paymentBatchResponse.setPID(paymentBatchRequest.getPID());
				paymentBatchResponse.setRespCode("2");
				paymentBatchResponse.setRespMsg("Failiure in proccesing the request."+e.getMessage());
				paymentBatchResponse.setTransactionCount(paymentBatchRequest.getTransactionCount()+"");
				paymentBatchResponse.setBatchId(paymentBatchRequest.getBatchID()+"");
			}
			else{
				paymentBatchResponse.setPID("");
				paymentBatchResponse.setRespCode("3");
				paymentBatchResponse.setRespMsg("Failiure in proccesing the request.Invalid Request or Unexpected error,not able process."+e.getMessage());
				paymentBatchResponse.setTransactionCount("");
				paymentBatchResponse.setBatchId("");
			}
		}
		return paymentBatchResponseDocument.xmlText();

	}

	
	//This method is called if XML is request
		@GET		
		@Path("/{Projectid}/batch/{BatchId}")
		@Consumes({ "application/xml" })
		@Produces({ "application/xml" })
		public String processXMLBatchResponseRequest(@PathParam("Projectid") String Projectid,@PathParam("BatchId") String BatchId) {	
			initIPgloging();
			String str_batch_XML_response = "";
			m_log.info(" Projetc ID: " +Projectid);
			m_log.info(" Batch ID: " +BatchId);
			PaymentBatchResponseDocument paymentBatchResponseDocument = null;
			paymentBatchResponseDocument = PaymentBatchResponseDocument.Factory.newInstance();
			PaymentBatchResponse paymentBatchResponse = paymentBatchResponseDocument.addNewPaymentBatchResponse();
			try { 
				int int_bath_id = Integer.parseInt(BatchId);
				str_batch_XML_response =  DBUtil.callSQLPROCForUSP_PROVIDE_RESPONSE(Projectid,int_bath_id);
				m_log.info(" Response batch XML: " +str_batch_XML_response);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				m_log.info(" Error while fetching the response batch Data: " +e.getMessage());
				paymentBatchResponse.setPID("");
				paymentBatchResponse.setRespCode("4");
				paymentBatchResponse.setRespMsg("Failiure in proccesing the request.Invalied request or Unexpected error,not able process."+e.getMessage());
				paymentBatchResponse.setTransactionCount("");
				paymentBatchResponse.setBatchId("");
				
			}
			return str_batch_XML_response;

		}
	// This method is called if HTML is request
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello() {
		return "<html> " + "<title>" + "Hello Jersey" + "</title>"
				+ "<body><h1>" + "Hello Jersey" + "</body></h1>" + "</html> ";
	}
	
	public static String validateRequest(PaymentBatchRequestDocument paymentBatchRequestDocument){
		String str_responseXML="";
		boolean status = false;
		PaymentBatchRequest paymentBatchRequest = paymentBatchRequestDocument.getPaymentBatchRequest();
		
		PaymentBatchResponseDocument paymentBatchResponseDocument = getPaymentBatchResponseDocumentFromReq(paymentBatchRequest);
		PaymentBatchRecord[] arr_batch = paymentBatchRequest.getPaymentBatchRecordArray();
		
		if(paymentBatchRequest.getTransactionCount()<=0 ){
			addExceptionToResponseHeader(paymentBatchResponseDocument,"1","Validation Failied.Transaction count in XML("+paymentBatchRequest.getTransactionCount()+") Should be greater than zero", status);			
			return paymentBatchResponseDocument.xmlText();
		}
		else if(paymentBatchRequest.getPID() == null || paymentBatchRequest.getPID().trim().length() <=0 ){
			addExceptionToResponseHeader(paymentBatchResponseDocument,"1","Validation Failied.PID in XML Should not be empty",status);			
			return paymentBatchResponseDocument.xmlText();
		}
		
		if(arr_batch != null && arr_batch.length > 0){
			if(paymentBatchRequest.getTransactionCount() != arr_batch.length){
				addExceptionToResponseHeader(paymentBatchResponseDocument,"1","Validation Failied.Transaction count in XML("+paymentBatchRequest.getTransactionCount()+") not Matching with number of batch records in request("+arr_batch.length+").",status);			
				return paymentBatchResponseDocument.xmlText();

			}
			for(int ibath = 0;ibath<arr_batch.length;ibath++){
				PaymentBatchRecord batchRecord = arr_batch[ibath];
				
				try{
					batchRecord.getAmount();
				}catch(Exception e){
					addExceptionToResponseHeader(paymentBatchResponseDocument,"2","Failed validation.",status);
					addExceptionBatchRecordToResponse(batchRecord,paymentBatchResponseDocument,"1","Validation Failied. Amount must be numeric");
					status =true;
					continue;
				}
				try{
					batchRecord.getTxRefNum();
				}catch(Exception e){
					addExceptionToResponseHeader(paymentBatchResponseDocument,"2","Failed validation.",status);
					addExceptionBatchRecordToResponse(batchRecord,paymentBatchResponseDocument,"1","Validation Failied. TxRefNum must be numeric");
					status =true;
					continue;
				}
				try{
					batchRecord.getPaymentOrderID();
				}catch(Exception e){
					addExceptionToResponseHeader(paymentBatchResponseDocument,"2","Failed validation.",status);
					addExceptionBatchRecordToResponse(batchRecord,paymentBatchResponseDocument,"1","Validation Failied. PaymentOrderID must be numeric");
					status =true;
					continue;
				}
				
				if(batchRecord.getTransactionType() ==null || batchRecord.getTransactionType().trim().length()<=0 ){
					addExceptionToResponseHeader(paymentBatchResponseDocument,"2","Failed validation.",status);
					addExceptionBatchRecordToResponse(batchRecord,paymentBatchResponseDocument,"1","Validation Failied. TransactionType is missing in XML");
					status =true;
				}
				else if(!checkIsNumber(batchRecord.getAmount() +"")){
					addExceptionToResponseHeader(paymentBatchResponseDocument,"2","Failed validation.",status);
					addExceptionBatchRecordToResponse(batchRecord,paymentBatchResponseDocument,"1","Validation Failied. Amount is missing in XML");
					status =true;
				}
				else if(batchRecord.getBrandCode() ==null || batchRecord.getBrandCode().trim().length()<=0){
					addExceptionToResponseHeader(paymentBatchResponseDocument,"2","Failed validation.",status);
					addExceptionBatchRecordToResponse(batchRecord,paymentBatchResponseDocument,"1","Validation Failied. BrandCode is missing in XML");
					status =true;
				}
				else if(batchRecord.getCurrency() ==null || batchRecord.getCurrency().trim().length()<=0){
					addExceptionToResponseHeader(paymentBatchResponseDocument,"2","Failed validation.",status);
					addExceptionBatchRecordToResponse(batchRecord,paymentBatchResponseDocument,"1","Validation Failied. Currency is missing in XML");
					status =true;
				}
				else if(batchRecord.getDivisionnumber() == null || batchRecord.getDivisionnumber().trim().length()<=0){
					addExceptionToResponseHeader(paymentBatchResponseDocument,"2","Failed validation.",status);
					addExceptionBatchRecordToResponse(batchRecord,paymentBatchResponseDocument,"1","Validation Failied. Divisionnumber is missing in XML");
					status =true;
				}
				else if(batchRecord.getTransactionLable0() == null || batchRecord.getTransactionLable0().trim().length()<=0){
					addExceptionToResponseHeader(paymentBatchResponseDocument,"2","Failed validation.",status);
					addExceptionBatchRecordToResponse(batchRecord,paymentBatchResponseDocument,"1","Validation Failied. TransactionLable0 is missing in XML");
					status =true;
				}
				else if(batchRecord.getMerchantOrderNumber() == null || batchRecord.getMerchantOrderNumber().trim().length()<=0){
					addExceptionToResponseHeader(paymentBatchResponseDocument,"2","Failed validation.",status);
					addExceptionBatchRecordToResponse(batchRecord,paymentBatchResponseDocument,"1","Validation Failied. MerchantOrderNumber is missing in XML");
					status =true;
				}
				else if(batchRecord.getExternalReferenceId() == null || batchRecord.getExternalReferenceId().trim().length()<=0){
					addExceptionToResponseHeader(paymentBatchResponseDocument,"2","Failed validation.",status);
					addExceptionBatchRecordToResponse(batchRecord,paymentBatchResponseDocument,"1","Validation Failied. ExternalReferenceId is missing in XML");
					status =true;
				}
				else if(!checkIsNumber(batchRecord.getTxRefNum()+"")){
					addExceptionToResponseHeader(paymentBatchResponseDocument,"2","Failed validation.",status);
					addExceptionBatchRecordToResponse(batchRecord,paymentBatchResponseDocument,"1","Validation Failied. TxRefNum is missing in XML");
					status =true;
				}
				else if(!checkIsNumber(batchRecord.getPaymentOrderID()+"")){
					addExceptionToResponseHeader(paymentBatchResponseDocument,"2","Failed validation.",status);
					addExceptionBatchRecordToResponse(batchRecord,paymentBatchResponseDocument,"1","Validation Failied. PaymentOrderID is missing in XML");
					status =true;
				}
				//batchRecord.get
			}
		}
		else {
			addExceptionToResponseHeader(paymentBatchResponseDocument,"1","Validation Failied. Batch Records missing in XML",status);			
			return paymentBatchResponseDocument.xmlText();
		}
		if(status ){
			return paymentBatchResponseDocument.xmlText();
		}
		return "";
	}
	public static boolean checkIsNumber(String str_value){
		return str_value.matches("^(?:(?:\\-{1})?\\d+(?:\\.{1}\\d+)?)$");
		 /*try  
		  {  
		    double d = Double.parseDouble(str_value);  
		  }  
		  catch(NumberFormatException nfe)  
		  {  
		    return false;  
		  }  
		  return true;  */
		
	}
	public static void addExceptionBatchRecordToResponse(PaymentBatchRecord batchRecord, PaymentBatchResponseDocument paymentBatchResponseDocument, String code, String message){
		
		PaymentBatchResponse paymentBatchResponse = paymentBatchResponseDocument.getPaymentBatchResponse();
		
		noNamespace.PaymentBatchResponseDocument.PaymentBatchResponse.PaymentBatchRecord responsebatchRecord = paymentBatchResponse.addNewPaymentBatchRecord();
		responsebatchRecord.setExternalReferenceId(batchRecord.getExternalReferenceId());
		responsebatchRecord.setMerchantTxId(batchRecord.getMerchantTxId());	
		responsebatchRecord.setTransactionType(batchRecord.getTransactionType());
		responsebatchRecord.setMerchantOrderNumber(batchRecord.getMerchantOrderNumber());
		//if(checkIsNumber(batchRecord.getPaymentOrderID()+""))
		//responsebatchRecord.setPaymentOrderId(batchRecord.getPaymentOrderID()+"");		
		responsebatchRecord.setReasonCode(code);
		responsebatchRecord.setReasonMessage(message);
		
		try{
			responsebatchRecord.setPaymentOrderId(batchRecord.getPaymentOrderID()+"");
		}catch(Exception e){
			
		}
		try{
			responsebatchRecord.setMerchantTxId(batchRecord.getTxRefNum()+"");
			//batchRecord.getTxRefNum();
		}catch(Exception e){
			
		}
		try{
			responsebatchRecord.setPaymentOrderId(batchRecord.getPaymentOrderID()+"");
			
		}catch(Exception e){
			
		}
		
		
		
	}
	public static void addExceptionToResponseHeader(PaymentBatchResponseDocument paymentBatchResponseDocument, String code, String message, boolean flag){
		
		if(!flag){
			PaymentBatchResponse paymentBatchResponse = paymentBatchResponseDocument.getPaymentBatchResponse();

			paymentBatchResponse.setRespCode(code);
			paymentBatchResponse.setRespMsg(message);			
			//String str_responseXML = paymentBatchResponseDocument.xmlText();
			//return str_responseXML;
		}
		
	}
	public static PaymentBatchResponseDocument getPaymentBatchResponseDocumentFromReq(PaymentBatchRequest paymentBatchRequest){
		
		PaymentBatchResponseDocument paymentBatchResponseDocument = PaymentBatchResponseDocument.Factory.newInstance();
		PaymentBatchResponse paymentBatchResponse = paymentBatchResponseDocument.addNewPaymentBatchResponse();
		paymentBatchResponse.setPID(paymentBatchRequest.getPID());
		paymentBatchResponse.setRespCode("");
		paymentBatchResponse.setRespMsg("");
		paymentBatchResponse.setBatchId(paymentBatchRequest.getBatchID()+"");
		paymentBatchResponse.setTransactionCount(paymentBatchRequest.getTransactionCount()+"");
		
		return paymentBatchResponseDocument;
	}

}