package com.flws.ipg.batch.helper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

import com.flws.ipg.batch.IPGBatchProcessJobFirst;
import com.flws.ipg.batch.util.DataLoadPropertyUtil;
import com.flws.ipg.batch.util.IPGBatchLogUtil;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.core.Builder;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import com.microsoft.windowsazure.services.servicebus.models.GetQueueResult;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMessageOptions;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMode;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveQueueMessageResult;

public class AzureQueueProcessHelper {
	static org.apache.log4j.Logger  m_log = org.apache.log4j.Logger.getLogger(AzureQueueProcessHelper.class);


	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void sendMessageToQueue(String strXmlMessage){

		m_log.info("Starting");
		String primary_key ="DTfA4DDwGtfv2KBQp3YYYyeTp+nfxn4i8VimhHjti0s=";
		String secondary_key = "Endpoint=sb://ipgdev.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=DTfA4DDwGtfv2KBQp3YYYyeTp+nfxn4i8VimhHjti0s=";

		//ServiceBusConfiguration.configureWithConnectionString(profile , configuration, connectionString)
		Configuration config = ServiceBusConfiguration.configureWithSASAuthentication(
				"ipgdev",
				"RootManageSharedAccessKey",
				primary_key,
				".servicebus.windows.net"
				);

		ServiceBusContract service = ServiceBusService.create(config);
		//service.
		m_log.info("config created");
		//QueueInfo queueInfo = new QueueInfo("TestQueue");
		try
		{
			// CreateQueueResult result = service.createQueue(queueInfo);
			try
			{
				GetQueueResult get_queueResult = null;
				try{
					get_queueResult = service.getQueue("IPGTEST");
					m_log.info("Queue Count before sending to queue"+get_queueResult.getValue().getCountDetails().getActiveMessageCount());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				BrokeredMessage messagere = new BrokeredMessage(strXmlMessage);
				//InputStream body = new InputStream(strXmlMessage);
				//messagere.
				service.sendQueueMessage("IPGTEST", messagere);
				//service.sendMessage("IPGTEST", messagere.setBody(body)); 
				m_log.info("Message send to queue");
				try{
					get_queueResult = service.getQueue("IPGTEST");
					m_log.info("Queue  Count"+get_queueResult.getValue().getCountDetails().getActiveMessageCount());
				}
				catch (Exception e) {
					e.printStackTrace();
				}			

			}
			catch (ServiceException e)
			{
				System.out.print("ServiceException encountered: ");
				m_log.info(e.getMessage());
				//System.exit(-1);
			}
		}
		catch (Exception e)
		{
			System.out.print("ServiceException encountered: ");
			m_log.info(e.getMessage());
			//System.exit(-1);
		}


	}
	public static String getMessageFromQueue() throws Exception{

		m_log.info("Starting to get message from Queue");
		String primary_key ="DTfA4DDwGtfv2KBQp3YYYyeTp+nfxn4i8VimhHjti0s=";
		String secondary_key = "Endpoint=sb://ipgdev.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=DTfA4DDwGtfv2KBQp3YYYyeTp+nfxn4i8VimhHjti0s=";
		String str_message = "";


		try{
			String str_srvice_bus_name = DataLoadPropertyUtil.getFuctionalParameterValue("AZ_SERVICE_BUS_NAME", "");
			String str_primary_key = DataLoadPropertyUtil.getFuctionalParameterValue("AZ_SERVICE_BUS_PRI_KEY", "");
			String str_url = DataLoadPropertyUtil.getFuctionalParameterValue("AZ_SERVICE_BUS_URL", "");
			String str_Queue_name = DataLoadPropertyUtil.getFuctionalParameterValue("AZ_SERVICE_BUS_QUEUE", "");
			m_log.info("str srvice bus name:" +str_srvice_bus_name);
			Configuration config = ServiceBusConfiguration.configureWithSASAuthentication(
					str_srvice_bus_name,
					"RootManageSharedAccessKey",
					str_primary_key,
					str_url
					);

			/*Configuration config = ServiceBusConfiguration.configureWithSASAuthentication(
					"ipgdev",
					"RootManageSharedAccessKey",
					primary_key,
					".servicebus.windows.net"
					);*/
			//config.

			ServiceBusContract service = ServiceBusService.create(config);
			m_log.info("config created");
			//QueueInfo queueInfo = new QueueInfo("TestQueue");

			boolean abc = true;
			while(abc)  {
				ReceiveMessageOptions opts = ReceiveMessageOptions.DEFAULT;
				opts.setReceiveMode(ReceiveMode.PEEK_LOCK);

				//str_Queue_name
				ReceiveQueueMessageResult resultQM =
						service.receiveQueueMessage(str_Queue_name, opts);

				BrokeredMessage message = resultQM.getValue();
				if (message != null && message.getMessageId() != null)
				{
					m_log.info("MessageID: " + message.getMessageId());
					// Display the queue message.
					System.out.print("From queue: ");
					byte[] b = new byte[200];
					String s = null;
					//int numRead = message.getBody().read(b);

					InputStream queueStream = message.getBody();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(queueStream));

					String str_line = null;

					while ((str_line = bufferedReader.readLine()) != null){

						str_message = str_message+str_line;
					}





					/*while (-1 != numRead)
					{
						s = new String(b);
						str_message = str_message+ s.trim();
						//System.out.print(s);
						numRead = message.getBody().read(b);
					}*/
					m_log.info(str_message);
					m_log.info("Custom Property: " +
							message.getProperty("MyProperty"));
					// Remove message from queue.
					m_log.info("Deleting this message.");
					service.deleteMessage(message);
					//str_message = s;
					break;
				}  
				else  
				{
					m_log.info(" no more messages In Queue.");
					return "FALSE";
					//break;
					// Added to handle no more messages.
					// Could instead wait for more messages to be added.
				}
			}
		}
		catch (ServiceException e) {
			System.out.print("ServiceException encountered: "+e.getMessage());
			e.printStackTrace();
			m_log.info(e.getMessage());
			throw new Exception("Service bus Exception while reading message from Queue: "+ e.getMessage());
		} catch (IOException e)
		{
			System.out.print("IO Exception encountered: "+e.getMessage());
			e.printStackTrace();
			m_log.info(e.getMessage());
			throw new Exception("IO Exception while reading message from Queue: "+ e.getMessage());
		}
		 catch (Exception e)
		{
			System.out.print("Gen Exception encountered: "+e.getMessage());
			e.printStackTrace();
			m_log.info(e.getMessage());
			throw new Exception("Gen Exception while reading message from Queue: "+ e.getMessage());
		}

		return str_message;
	}
}
