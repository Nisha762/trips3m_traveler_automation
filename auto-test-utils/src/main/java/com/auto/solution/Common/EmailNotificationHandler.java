package com.auto.solution.Common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class EmailNotificationHandler {

	ResourceManager rm = null;
	
	String htmlFileName = "";
	
	Folder inbox = null;
	
	Message matched_msg = null;
	
	public EmailNotificationHandler(ResourceManager rm){
		this.rm = rm;
		this.setHtmlFilePath();
		
	}
	
	private void setHtmlFilePath(){
		this.htmlFileName = rm.getTestExecutionLogFileLocation().replace("{0}", "htmlContent.html");
	}
	
	private void storeEmailHtmlContentToFile()throws Exception{
		
		try{
			String html_content = "";
			
			Object messageContent = matched_msg.getContent();

			if(messageContent instanceof String){
			
				html_content = messageContent.toString();
			}
			else if(messageContent instanceof Multipart){
			
				Multipart mp = (Multipart) matched_msg.getContent();
			
				int mpcount = mp.getCount();
			
				for(int index = 0;index < mpcount; index++){
			
				html_content = fetchHtmlContent(mp.getBodyPart(index));
				}
			}
			else{
			throw new Exception(Property.ERROR_MESSAGES.ERR_GETTING_MAIL_CONTENT.getErrorMessage());	
			}
			FileUtils.writeStringToFile(new File(htmlFileName), html_content);
			Utility.setKeyValueToGlobalVarMap(Property.Html_Content, html_content);
			}
			catch(Exception e){
			throw e;
			}
	}
		
	private String fetchHtmlContent(Part p) throws Exception{
		String htmlContent = "";
		try{
			if(p.isMimeType("text/html")){
				Object o = p.getContent();
					if (o instanceof String) {
						htmlContent = (String) o;
					}
			}
			//TODO : Need to cater other MIME type as well, but this will handle the case for now.
		}
		catch(Exception e){
			throw e;
		}
		return htmlContent;
	}	
	
	public void verifyChangesInTwoFile(String originalHtmlContentVariableName,String templateContentVariableName)throws Exception {
		try {
			String originalHtmlContent = Utility.getValueForKeyFromGlobalVarMap(originalHtmlContentVariableName);
			String templateContent = Utility.getValueForKeyFromGlobalVarMap(templateContentVariableName);
		
			String htmlFileName = rm.getTestExecutionLogFileLocation().replace("{0}", "htmlContent.html");
			String templateFileName = rm.getTestExecutionLogFileLocation().replace("{0}", "template.html");
			
			FileUtils.writeStringToFile(new File(htmlFileName), originalHtmlContent.replaceAll("tracked_email_id=[0-9]*&", "").replaceAll("tracked_email_id=[0-9]*&amp;", ""));
			FileUtils.writeStringToFile(new File(templateFileName), templateContent);

			Document temp = Jsoup.parse(new File(templateFileName), "UTF-8", "http://example.com/");		
			Document doc = Jsoup.parse(new File(htmlFileName), "UTF-8", "http://example.com/");
			
			Elements msgElement = doc.getElementsByAttributeValueContaining("class", "message_body");
			Elements tempElement = temp.getElementsByTag("body");
			
			FileUtils.writeStringToFile(new File(htmlFileName), msgElement.html());
			FileUtils.writeStringToFile(new File(templateFileName), tempElement.html());

			 HashMap<Boolean, String> matchedMap= Utility.verifyDiffInTwoFile(htmlFileName, templateFileName);
			 for(boolean isMatched : matchedMap.keySet()) {
				 if(!isMatched) {
						throw new Exception(Property.ERROR_MESSAGES.ERR_FILE_CONTENT_NOT_MATCHED.getErrorMessage()+matchedMap.get(isMatched));
				 }
				 break;
			 }
			 
		}catch(Exception ex) {
			throw ex;
		}
	}
	
	public String read(String file) throws IOException {
		  return new String(Files.readAllBytes(Paths.get(file)));
		}
	
	public void connectToMailServerInbox(String email,String password)throws Exception{
		
		try{
		System.setProperty("jsse.enableSNIExtension", "false");
		Properties props = new Properties();
		
		props.setProperty("mail.store.protocol", "imaps");
		
		props.setProperty("mail.imaps.ssl.trust", "imap.gmail.com");
		
		Session session = Session.getInstance(props, null);
		
		Store store = session.getStore();
		
		store.connect("imap.gmail.com", email, password);
		
		inbox = store.getFolder("INBOX");
		
		inbox.open(Folder.READ_ONLY);
		
		}
		catch(Exception e){
			throw e;
		}
	}
	
	public boolean isEmailSentToIndox(String email_subject_pattern, String recipientEmailId)throws Exception{	
		try{
			Calendar cal = null;
			
			cal = Calendar.getInstance();
			
			Date todaysDate = new Date(cal.getTimeInMillis());
			
			ReceivedDateTerm todaysSearchTerm = new ReceivedDateTerm(ComparisonTerm.EQ, todaysDate);
			
			Message[] messages = inbox.search(todaysSearchTerm);
			
			messages = (Message[]) Utility.reverseObjectArray(messages);
			
			for (Message message : messages) {
				boolean isMailFound = false;
				String msg_subject = message.getSubject();
				System.out.println(msg_subject);
				if(msg_subject == null){continue;}
				Pattern pattern = Pattern.compile(email_subject_pattern,Pattern.CASE_INSENSITIVE);
				if(pattern.matcher(msg_subject).matches()){
					Address[] recipents = message.getAllRecipients();
					for(Address recipent :recipents) {
						System.out.println(recipent);
						if(recipent.toString().equalsIgnoreCase(recipientEmailId)) {
							matched_msg = message;
							isMailFound = true;
						}
						
					}
					
				}
				if (isMailFound)
					break;
			}
			if(matched_msg == null){
				return false;
			}
		storeEmailHtmlContentToFile();
		return true;
		
		}
		catch(Exception e){
			throw e;
		}
	}
		

	public boolean isElementThereInMailContent(String cssQuery) throws Exception{
		try{
		
			this.storeEmailHtmlContentToFile();
			
			File input = new File(htmlFileName);
			
			Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

			Elements msgElement = doc.select(cssQuery);
			
			return msgElement.isEmpty();
		
		
		}
		catch(Exception e){
			throw e;
		}
		
		
	}
	
	
	
	
	
	
}
