package com.auto.solution.Common;

import java.io.File;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

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
			Multipart mp = (Multipart) matched_msg.getContent();
			
			int mpcount = mp.getCount();
			
			String html_content = "";
			
			for(int index = 0;index < mpcount; index++){
				 html_content = fetchHtmlContent(mp.getBodyPart(index));
			}			
			
			FileUtils.writeStringToFile(new File(htmlFileName), html_content);
			
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
	
	
	public void connectToMailServerInbox(String email,String password)throws Exception{
		
		try{
		Properties props = new Properties();
		
		props.setProperty("mail.store.protocol", "imaps");
		
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
	
	public boolean isEmailSentToIndox(String email_subject_pattern)throws Exception{
		
		
		try{
			Message[] messages = inbox.getMessages();
			
			for (Message message : messages) {
				String msg_subject = message.getSubject();
				Pattern pattern = Pattern.compile(email_subject_pattern);
				if(pattern.matcher(msg_subject).matches()){
					matched_msg = message;
					break;
				}
			}
			if(matched_msg == null){
				return false;
			}
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
