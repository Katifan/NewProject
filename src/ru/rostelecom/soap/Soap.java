package ru.rostelecom.soap;

import org.apache.commons.codec.digest.*;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.xml.sax.SAXException;
import ru.infotecs.crypto.ViPNetProvider;
import ru.infotecs.crypto.gost28147.Gost28147SecretKey;
import ru.infotecs.crypto.keys.ViPNetDirectoryLoadStoreParameter;
import ru.infotecs.crypto.keys.ViPNetKeyProtectionParameter;
import org.apache.commons.codec.binary.Base64;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
//import java.util.Base64;
import java.text.ParseException;
import java.util.Enumeration;

/**
 * Created by Olcha on 23.11.2015.
 */
public class Soap {

    public static void main(String[] args) throws NoSuchProviderException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableEntryException, InvalidKeyException, SignatureException, InvalidCanonicalizerException, CanonicalizationException, SAXException, ParserConfigurationException, ParseException {
        Crypt crypt = new Crypt(); //Создаём объект класса криптографии
        crypt.initBeginParams(); //Инициализируем начальный параметры(хранилище ключей, библиотеку безопасности Apache и т.д
        String certificate = crypt.getCertification(); //Берём сертификат

        //Создаём основной XML, на основе которого мы будем собирать SOAP
        Document xml = DocumentHelper.createDocument();
        Element root = createXML(xml);

        Element body = createBody(root); //создаём Body, чтобы высчитать хэш
        String hash = crypt.getHash(body); //Считываем хэш
        root.remove(body); //Удаляем Body

        //Формируем Head
        Header head = new Header(certificate, hash, crypt);
        head.generateHeader(root);

        //Формируем Body
        createBody(root);

        //Выводим на экран наш SOAP
        System.out.println(root.asXML());
    }

    /*
        Создаёт <soap:Envelope>, необходимый нам как корень всей XML и в котором лежат наши пространства имён
        Принимает один параметр: объект типа Document, то есть наш документ XML
        Возвращает объект, который является необходимым нам <soap:Envelope>
     */
    private static Element createXML(Document xml){
        Element root = xml.addElement("soap:Envelope").
                addNamespace("soap", "http://schemas.xmlsoap.org/soap/envelope/").
                addNamespace("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd").
                addNamespace("ds", "http://www.w3.org/2000/09/xmldsig#")
                .addNamespace("atc", "http://at-sibir.ru/getDictionary")
                .addNamespace("smev", "http://smev.gosuslugi.ru/rev120315")
                .addNamespace("wsu","http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
        return root;
    }

    /*
        Создаёт <soap:Body>, который нужен нам для формирование SOAP или для каноникализации <soap:Body>
        Принимает на вход: наш "корневой" элемент, на котором строится наш XML
        Возвращает: <soap:Body> полностью сформированный
     */
    private static Element createBody(Element root) throws ParseException {
        Element body = root.addElement("soap:Body")
                .addNamespace("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd")
                .addAttribute("wsu:Id", "body");
        Element getDictionary = body.addElement("atc:getDictionary");
        Message message = new Message("Olcha", "123456789");
        message.generateMessage(getDictionary);
        MessageData messageData = new MessageData("c580d006-f86f-4bdd-84be-b51de6f626c6");
        messageData.generateMessageData(getDictionary);
        return body;
    }
}
