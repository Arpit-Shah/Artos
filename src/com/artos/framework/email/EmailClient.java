/*******************************************************************************
 * Copyright (C) 2018 Arpit Shah
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.artos.framework.email;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.artos.framework.xml.AuthSettingsParser;
import com.artos.framework.xml.UserAuthParameters;

public class EmailClient {

	// Recipient's email ID needs to be mentioned.
	String to = null;
	// Sender's email ID needs to be mentioned
	String from = null;
	String username = null;
	String password = null;
	String host = null;
	String port = null;
	String enable_auth = null;
	String enable_ttl = null;
	String emailSubject = null;
	String emailBody = null;
	File emailAuthSettingsFilePath = null;
	String authID = null;

	public EmailClient(String to, String from, File emailAuthSettingsFilePath, String authID, String host, String port, String enable_auth,
			String enable_ttl, String emailSubject, String emailBody) throws Exception {
		super();
		this.to = to;
		this.from = from;
		this.emailAuthSettingsFilePath = emailAuthSettingsFilePath;
		this.authID = authID;
		this.host = host;
		this.port = port;
		this.enable_auth = enable_auth;
		this.enable_ttl = enable_ttl;
		this.emailSubject = emailSubject;
		this.emailBody = emailBody;

		AuthSettingsParser authSettingsParser = new AuthSettingsParser(emailAuthSettingsFilePath);
		UserAuthParameters authParam = authSettingsParser.getAuthParametersByID(authID);
		if (null == authParam) {
			throw new Exception("Invalid Auth ID");
		}
		
		this.username = authParam.getUserName();
		this.password = authParam.getPassword();
	}

	public EmailClient(String to, String from, String username, String password, String host, String port, String enable_auth, String enable_ttl,
			String emailSubject, String emailBody) {
		super();
		this.to = to;
		this.from = from;
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
		this.enable_auth = enable_auth;
		this.enable_ttl = enable_ttl;
		this.emailSubject = emailSubject;
		this.emailBody = emailBody;
	}

	public void sendEmailWithAnAttachment() {

		Properties props = new Properties();
		props.put("mail.smtp.auth", enable_auth);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		// Get the Session object.
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			// Create a default MimeMessage object.
			Message message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

			// Set Subject: header field
			message.setSubject(emailSubject);

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();

			// Now set the actual message
			messageBodyPart.setText(emailBody);

			// Create a multi part message
			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			// Part two is attachment
			messageBodyPart = new MimeBodyPart();
			String filename = "./conf/extent_configuration.xml";
			DataSource source = new FileDataSource(filename);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(filename);
			multipart.addBodyPart(messageBodyPart);

			// Send the complete message parts
			message.setContent(multipart);

			// Send message
			Transport.send(message);

			System.out.println("Sent message successfully....");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
