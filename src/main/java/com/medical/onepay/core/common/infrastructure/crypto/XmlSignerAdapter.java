package com.medical.onepay.core.common.infrastructure.crypto;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.medical.onepay.core.common.application.ports.out.XmlSignerPort;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;


@Component
public class XmlSignerAdapter implements XmlSignerPort {

    private static final String SIGNATURE_METHOD =
            "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";

    @Override
    public String sign(String xml, InputStream certStream, String certPassword) throws Exception {

        Document document = parseXml(xml);

        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        Reference ref = fac.newReference(
                "",
                fac.newDigestMethod(DigestMethod.SHA256, null),
                Collections.singletonList(
                        fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)
                ),
                null,
                null
        );

        SignedInfo signedInfo = fac.newSignedInfo(
                fac.newCanonicalizationMethod(
                        CanonicalizationMethod.INCLUSIVE,
                        (C14NMethodParameterSpec) null
                ),
                fac.newSignatureMethod(SIGNATURE_METHOD, null),
                Collections.singletonList(ref)
        );

        KeyStore.PrivateKeyEntry keyEntry = loadPrivateKey(certStream, certPassword);
        KeyInfo keyInfo = buildKeyInfo(fac, keyEntry);

        DOMSignContext signContext =
                new DOMSignContext(keyEntry.getPrivateKey(), document.getDocumentElement());

        XMLSignature signature = fac.newXMLSignature(signedInfo, keyInfo);
        signature.sign(signContext);

        return transformToString(document);
    }

    // ===================== HELPERS =====================

    private Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true); // 🔴 CRÍTICO para XML Signature
        dbf.setIgnoringElementContentWhitespace(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(new InputSource(new StringReader(xml)));
    }

    private KeyStore.PrivateKeyEntry loadPrivateKey(InputStream certStream, String password)
            throws Exception {

        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(certStream, password.toCharArray());

        String alias = ks.aliases().nextElement();

        return (KeyStore.PrivateKeyEntry) ks.getEntry(
                alias,
                new KeyStore.PasswordProtection(password.toCharArray())
        );
    }

    private KeyInfo buildKeyInfo(XMLSignatureFactory fac, KeyStore.PrivateKeyEntry keyEntry) {

        X509Certificate cert = (X509Certificate) keyEntry.getCertificate();

        KeyInfoFactory kif = fac.getKeyInfoFactory();

        List<Serializable> x509Content = new ArrayList<>();
        x509Content.add(cert);

        X509Data x509Data = kif.newX509Data(x509Content);

        return kif.newKeyInfo(Collections.singletonList(x509Data));
    }

    private String transformToString(Document document) throws Exception {

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(output));

        return output.toString(StandardCharsets.UTF_8);
    }
}
