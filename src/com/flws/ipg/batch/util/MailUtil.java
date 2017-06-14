package com.flws.ipg.batch.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Properties;


import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		sendAlertMail("10.201.50.50","jjoseph@1800flowers.com,joysjoseph80@gmail.com","TEST","JJ@jj.com","etst");

	}
	public static void sendAlertMail(String host, String to, String subject, String from, String messageText ){

		/*String host = "server.myhost.com";
		String to = "YourFriend@someemail.com";
		String from = "Me@myhost.com";
		String subject = "My First Email";
		String messageText = "I am sending a message using the JavaMail API.\n Here type your message.";*/
		String hostname ="";
		try {
			 hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(hostname !=null && hostname.trim().length() > 0 && from != null && from.trim().length() >0){
			from  = hostname+from;
			
		}
		else{
			from  = hostname+"IncontactReportjob@1800flowers.com";
		}
		System.out.println("Sending mail. Mail Message body "+messageText);
		try {

			boolean sessionDebug = false;
			Properties props = System.getProperties();
			if(props.getProperty("mail.host") == null){
				props.put("mail.host", host);
			}
			if(props.getProperty("mail.transport.protocol") == null ) {
				props.put("mail.transport.protocol", "smtp");
			}

			Session session = Session.getDefaultInstance(props, null);
			session.setDebug(sessionDebug);

			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));
			String [] str_rec = to.split(",");
			InternetAddress[] address =null;
			if(str_rec != null && str_rec.length >0){
				address = new InternetAddress[str_rec.length];
				for(int iadd =0;iadd<str_rec.length;iadd++){
					address[iadd]=new InternetAddress(str_rec[iadd]);
					
				}
				msg.setRecipients(Message.RecipientType.TO, address);
			}else{
				InternetAddress[] address1 = {new InternetAddress(to)};
				msg.setRecipients(Message.RecipientType.TO, address1);
			}
			
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			msg.setText(messageText);
//			Hand the message to the default transport service
//			for delivery.
			Transport.send(msg);
		}
		catch (Exception mex) {
			mex.printStackTrace();
		}


	}
	
	public static void sendAlertMail(Properties mailProps){

		String host = mailProps.getProperty("host");
		String to = mailProps.getProperty("to");
		String from = mailProps.getProperty("from");
		String subject = mailProps.getProperty("subject");
		String messageText = mailProps.getProperty("messageText");
		System.out.println("Sending mail. Mail Message body "+messageText);
		String hostname ="";
		try {
			 hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(hostname !=null && hostname.trim().length() > 0){
			from  = hostname+"Fbottomjob@1800flowers.com";
		}
		try {

			boolean sessionDebug = false;
			Properties props = System.getProperties();
			if(props.getProperty("mail.host") == null){
				props.put("mail.host", host);
			}
			if(props.getProperty("mail.transport.protocol") == null ) {
				props.put("mail.transport.protocol", "smtp");
			}

			Session session = Session.getDefaultInstance(props, null);
			session.setDebug(sessionDebug);

			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));
			InternetAddress[] address = {new InternetAddress(to)};
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			msg.setText(messageText);
//			Hand the message to the default transport service
//			for delivery.
			Transport.send(msg);
		}
		catch (Exception mex) {
			mex.printStackTrace();
		}


	}


}
