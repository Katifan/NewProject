package ru.rostelecom.soap;

import org.apache.commons.codec.binary.Base64;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.dom4j.Element;
import org.xml.sax.SAXException;
import ru.infotecs.crypto.ViPNetProvider;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Created by Olcha on 23.11.2015.
 */
public class Header {
    String c;
    String h;
    Crypt crypt;
//    public Header(String cert, String hash, PrivateKey privateKey){
//        c = cert;
//        h = hash;
//        pKey = privateKey;
//    }
    public Header(String cert, String hash, Crypt crypt){
        c = cert;
        h = hash;
        this.crypt = crypt;
    }
    public void generateHeader(Element root) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, SignatureException, NoSuchProviderException, InvalidKeyException, IOException, SAXException, ParserConfigurationException, CanonicalizationException, InvalidCanonicalizerException {
        Element header =  root.addElement("soap:Header");
        Element security = header.addElement("wsse:Security")
                .addAttribute("soap:actor", "http://smev.gosuslugi.ru/actors/smev");
        Element binarySecurityToken =  security.addElement("wsse:BinarySecurityToken").
                addAttribute("Encoding", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary").
                addAttribute("ValueType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3").
                addAttribute("wsu:Id", "CertId-1").
                addAttribute("xmlns:wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd").
                addText(c);
        Element signature = security.addElement("ds:Signature");
        signature.addAttribute("Id", "Signature-1");
        Element signedInfo = signature.addElement("ds:SignedInfo");
        Element canonicalizationMethod = signedInfo.addElement("ds:CanonicalizationMethod").
                addAttribute("Algorithm", "http://www.w3.org/2001/10/xml-exc-c14n#");
        Element signatureMethod = signedInfo.addElement("ds:SignatureMethod").
                addAttribute("Algorithm", "http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411");
        Element reference = signedInfo.addElement("ds:Reference").
                addAttribute("URI", "#body");

        Element transforms = reference.addElement("ds:Transforms");
        Element transform = transforms.addElement("ds:Transform").
                addAttribute("Algorithm", "http://www.w3.org/2001/10/xml-exc-c14n#");
        Element digestMethod = reference.addElement("ds:DigestMethod").
                addAttribute("Algorithm", "http://www.w3.org/2001/04/xmldsig-more#gostr3411");
        Element digestValue = reference.addElement("ds:DigestValue")
                .addText(h);
        String sign = crypt.getSignature(signedInfo);
        Element signatureValue = signature.addElement("ds:SignatureValue")
                .addText(sign);
        Element keyInfo = signature.addElement("ds:KeyInfo")
                .addAttribute("Id", "KeyId-1");
        Element securityTokenReference =  keyInfo.addElement("wsse:SecurityTokenReference")
                .addAttribute("wsu:Id", "STRId-1")
                .addAttribute("xmlns:wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
        Element wReference = securityTokenReference.addElement("wsse:Reference")
                .addAttribute("URI", "#CertId-1")
                .addAttribute("ValueType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");

    }

    //Получение подписи
//    public String getSignature(Element signedInfo) throws NoSuchProviderException, KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, InvalidKeyException, SignatureException, InvalidCanonicalizerException, CanonicalizationException, SAXException, ParserConfigurationException {
//        Signature signatureDriver = Signature.getInstance(
//                "GOST3411-94withGOST3410-2001", // название алгоритма
//                "ViPNet"                        // название провайдера
//        );
//        signatureDriver.initSign(pKey);
//
//        Canonicalizer canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
//        byte[] canonSign = canon.canonicalize(signedInfo.asXML().getBytes("UTF-8"));
//        String canonXML = new String(canonSign, "UTF-8");
//
//        byte[] nextDataChunk = canonXML.getBytes();
//        signatureDriver.update(nextDataChunk);
//
//        // завершение операции создания цифровой подписи
//        byte[] signatureValue = signatureDriver.sign();
//        String sign = Base64.encodeBase64String(signatureValue);
//        return sign;
//    }
}
