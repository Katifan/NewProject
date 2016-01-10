package ru.rostelecom.soap;

import org.dom4j.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by Olcha on 23.11.2015.
 */
public class Message {
    String sName;
    String sCode;
    String currentDate;
    public Message(String senderName, String senderCode) throws ParseException {
        sName = senderName;
        sCode = senderCode;
        currentDate = getCurrentDate();
    }

    public void generateMessage(Element getDictionary) throws ParseException {
        Element message = getDictionary.addElement("smev:Message");
        Element sender = message.addElement("smev:Sender");
        Element code = sender.addElement("smev:Code")
                .addText(sCode);
        Element name = sender.addElement("smev:Name")
                .addText(sName);
        Element recipient = message.addElement("smev:Recipient");
        code = recipient.addElement("smev:Code")
                .addText(sCode);
        name = recipient.addElement("smev:Name")
                .addText(sName);
        Element originator = message.addElement("smev:Originator");
        code = originator.addElement("smev:Code")
                .addText(sCode);
        name = originator.addElement("smev:Name")
                .addText(sName);
        Element typeCode = message.addElement("smev:TypeCode")
                .addText("GSRV");
        Element status = message.addElement("smev:Status")
                .addText("REQUEST");
        Element date = message.addElement("smev:Date")
                .addText(currentDate);
        Element exchangeType = message.addElement("smev:ExchangeType")
                .addText("1");
        Element serviceCode = message.addElement("smev:ServiceCode")
                .addText("5440100010000545045");
        Element caseNumber = message.addElement("smev:CaseNumber")
                .addText("70263950");
    }

    //Получаем текущее время клиента на его устройстве
    private String getCurrentDate() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        //String[] date = sdf.format(new Date()).split(" ");
        return sdf.format(new Date());
    }
}
