package com.example.mengnanchen.mylayouttest;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by MengnanChen on 2017/9/5.
 */

public class MailSender {
    private Properties properties;
    private Session session;
    private Message message;
    private MimeMultipart mimeMultipart;

    public MailSender(){
        super();
        this.properties=new Properties();
    }

    public void setProperties(String host,String post){
        //地址
        this.properties.put("mail.smtp.host",host);
        //端口号
        this.properties.put("mail.smtp.post",post);
        //是否验证
        this.properties.put("mail.smtp.auth",true);

        this.session=Session.getInstance(properties);
        this.message=new MimeMessage(session);
        this.mimeMultipart=new MimeMultipart("mixed");
    }

    public void setReceiver(String[] receiver) throws MessagingException {
        Address[] addresses=new InternetAddress[receiver.length];
        for(int i=0;i<receiver.length;++i){
            addresses[i]=new InternetAddress(receiver[i]);
        }
        this.message.setRecipients(Message.RecipientType.TO,addresses);
    }

    public void setContent(String from,String title,String content) throws MessagingException,AddressException {
        this.message.setFrom(new InternetAddress(from));
        this.message.setSubject(title);
        MimeBodyPart textBody=new MimeBodyPart();
        textBody.setContent(content,"text/html;charset=gbk");
        this.mimeMultipart.addBodyPart(textBody);
    }

    public void addAttachment(String filePath) throws MessagingException{
        FileDataSource fileDataSource = new FileDataSource(new File(filePath));
        DataHandler dataHandler = new DataHandler(fileDataSource);
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setDataHandler(dataHandler);
        mimeBodyPart.setFileName(fileDataSource.getName());
        this.mimeMultipart.addBodyPart(mimeBodyPart);
    }

    public void sendMail(String host,String username,String password) throws MessagingException{
        this.message.setSentDate(new Date());
        this.message.setContent(this.mimeMultipart);
        this.message.saveChanges();

        Transport transport=session.getTransport("smtp");
        transport.connect(host,username,password);
        transport.sendMessage(message,message.getAllRecipients());

        transport.close();
    }
}
