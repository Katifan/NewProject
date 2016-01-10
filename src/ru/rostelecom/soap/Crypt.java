package ru.rostelecom.soap;

import org.apache.commons.codec.binary.Base64;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.dom4j.Element;
import org.xml.sax.SAXException;
import ru.infotecs.crypto.ViPNetProvider;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by Olcha on 28.12.2015.
 */
public class Crypt {
    KeyStore keyStore;
    String alias;
    char[] password;
    MessageDigest digestDriver;

    /*
        Метод для взятие хэш от каноникализируемого элемента в XML.
        Принимает: каноникализированный элемент в XML
        Возвращает: хэш от каноникализированного элемента
     */
    public String getHash(Element xmlElement) throws IOException, ParserConfigurationException, SAXException, CanonicalizationException, InvalidCanonicalizerException {
        String canonXML = toCanonicalize(xmlElement);
        digestDriver.update(canonXML.getBytes());
        byte[] digestValue = digestDriver.digest();
        String hash = DatatypeConverter.printBase64Binary(digestValue);
        return hash;
    }

    /*
        Метод для взятия подписи от элемента(можно засунуть любой элемент, но настойчиво рекомендую <ds:SignedInfo>)
        Принимает: Элемент XML(рекомендую SignedInfo>)
        Возвращает: Подпись
     */
    public String getSignature(Element signedInfo) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateEncodingException, NoSuchProviderException, InvalidKeyException, SignatureException, IOException, ParserConfigurationException, SAXException, CanonicalizationException, InvalidCanonicalizerException {
        Signature signatureDriver = Signature.getInstance(
                "GOST3411-94withGOST3410-2001",
                "ViPNet"
        );
        PrivateKey pKey = (PrivateKey) keyStore.getKey(alias, password);
        signatureDriver.initSign(pKey);
        String canonXML = toCanonicalize(signedInfo);
        byte[] nextDataChunk = canonXML.getBytes();
        signatureDriver.update(nextDataChunk);
        byte[] signatureValue = signatureDriver.sign();
        String sign = Base64.encodeBase64String(signatureValue);
        return sign;
    }

    /*
        Метод, который вытаскивает сертификат из хранилища ключей.
        Возвращает: сертификат, который принадлежит данному алиасу с данным паролем.
     */
    public String getCertification() throws CertificateEncodingException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
        String certificate = Base64.encodeBase64String(cert.getEncoded());
        return certificate;
    }

    /*
        Метод, который каноникализирует элемент в XML
        Принимает: элемент XML, который надо каноникализировать
        Возвращает: каноникализированный элемент XML
     */
    public String toCanonicalize(Element xmlElement) throws InvalidCanonicalizerException, IOException, ParserConfigurationException, SAXException, CanonicalizationException {
        Canonicalizer canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        byte[] canonBody = canon.canonicalize(xmlElement.asXML().getBytes("UTF-8"));
        String canonXML = new String(canonBody, "UTF-8");
        return canonXML;
    }

    /*
        Метод, который инициализирует библиотеку с классами безопасности, загружает хранилище ключей и добавляет провайдера защиты
     */
    public void initBeginParams() throws NoSuchProviderException, KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        org.apache.xml.security.Init.init();
        Security.addProvider(new ViPNetProvider());
        digestDriver = MessageDigest.getInstance("GOST3411-94","ViPNet");
        keyStore = KeyStore.getInstance("ViPNetContainer", "ViPNet");
        keyStore.load(new FileInputStream("C:\\Users\\Olcha\\Downloads\\lesson_9_crypto (1)\\lesson_9_crypto\\token"), null);
        alias = "key";
        password = "1234567890".toCharArray();
    }

}
