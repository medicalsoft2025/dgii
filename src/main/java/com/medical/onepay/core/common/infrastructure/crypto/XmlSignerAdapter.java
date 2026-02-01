package com.medical.onepay.core.common.infrastructure.crypto;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import oracle.xml.parser.v2.DOMParser;
import oracle.xml.parser.v2.XMLDocument;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.*;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class XmlSignerAdapter {

    private static final String SIGNATURE_METHOD = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";

    /**
     * Firma un documento XML utilizando el método requerido por la DGII,
     * que depende del DOMParser de Oracle.
     *
     * @param xml          El string del XML a firmar.
     * @param certStream   Un InputStream del certificado P12.
     * @param certPassword La contraseña del certificado.
     * @return El XML firmado como un String.
     * @throws Exception Si ocurre un error durante el proceso de firma.
     */
    public String sign(String xml, InputStream certStream, String certPassword) throws Exception {

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

        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(certStream, certPassword.toCharArray());
        String alias = ks.aliases().nextElement();

        KeyStore.PrivateKeyEntry keyEntry =
                (KeyStore.PrivateKeyEntry) ks.getEntry(
                        alias,
                        new KeyStore.PasswordProtection(certPassword.toCharArray())
                );

        X509Certificate cert = (X509Certificate) keyEntry.getCertificate();

        KeyInfoFactory kif = fac.getKeyInfoFactory();
        List<Object> x509Content = new ArrayList<>();
        x509Content.add(cert);
        X509Data x509Data = kif.newX509Data(x509Content);
        KeyInfo keyInfo = kif.newKeyInfo(Collections.singletonList(x509Data));

        DOMParser parser = new DOMParser();
        parser.setPreserveWhitespace(false);
        parser.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        XMLDocument document = parser.getDocument();
        Element root = document.getDocumentElement();

        DOMSignContext signContext =
                new DOMSignContext(keyEntry.getPrivateKey(), root);

        XMLSignature signature = fac.newXMLSignature(signedInfo, keyInfo);
        signature.sign(signContext);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(root), new StreamResult(output));

        return output.toString(StandardCharsets.UTF_8);
    }
}
