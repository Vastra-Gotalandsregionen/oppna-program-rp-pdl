package se.vgregion.service.xmlsig;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.crypto.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class XMLSigSpike {

    @Test
    public void testXmlSignature() throws Exception {

        String providerName = System.getProperty("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");

        XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM", (Provider) Class.forName(providerName).newInstance());
        DigestMethod digestMethod = factory.newDigestMethod(DigestMethod.SHA1, null);
        Transform transform = factory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null);
        Reference reference = factory.newReference("", digestMethod, Collections.singletonList(transform), null, null);
        CanonicalizationMethod canonicalizationMethod = factory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, (C14NMethodParameterSpec) null);
        SignatureMethod signatureMethod = factory.newSignatureMethod(SignatureMethod.RSA_SHA1, null);
        SignedInfo signedInfo = factory.newSignedInfo(canonicalizationMethod, signatureMethod, Collections.singletonList(reference));

        KeyStore ks = KeyStore.getInstance("JKS");
        InputStream fis = XMLSigSpike.class.getClassLoader().getResourceAsStream("dev.jks");
        ks.load(fis, "devjks".toCharArray());
        fis.close();

        PrivateKey prv = (PrivateKey) ks.getKey("vgr-pdl", "devjks".toCharArray());
        final Certificate cert = ks.getCertificate("vgr-pdl");
        final Certificate cacert = ks.getCertificate("vgr-ca");

        DocumentBuilderFactory dbf =
                DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc =
                dbf.newDocumentBuilder().
                        parse(XMLSigSpike.class.getClassLoader().getResourceAsStream("bfr.xml"));

        final X509Certificate x509Cert = (X509Certificate) cert;
        List<X509Certificate> x509 = Arrays.asList(x509Cert);

        KeyInfoFactory keyInfoFactory = factory.getKeyInfoFactory();
        X509Data x509Data = keyInfoFactory.newX509Data(x509);
        List items = new ArrayList();

        items.add(x509Data);
        //items.add(pub);
        KeyInfo keyInfo = keyInfoFactory.newKeyInfo(items);

        DOMSignContext dsc = new DOMSignContext(prv, doc.getDocumentElement());

        XMLSignature signature = factory.newXMLSignature(signedInfo, keyInfo);
        signature.sign(dsc);

        FileOutputStream fos = new FileOutputStream("mySignedFile.xml");
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(
                new DOMSource(doc),
                new StreamResult(fos));
        fos.close();

        Document signedDoc =
                dbf.newDocumentBuilder().
                        parse(new FileInputStream("mySignedFile.xml"));


        // Find Signature element.
        NodeList nl =
                doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        if (nl.getLength() == 0) {
            throw new Exception("Cannot find Signature element");
        }

        KeySelector selector = new KeySelector() {
            @Override
            public KeySelectorResult select(final KeyInfo keyInfo,final  Purpose purpose,final  AlgorithmMethod algorithmMethod,final  XMLCryptoContext xmlCryptoContext) throws KeySelectorException {
                return new KeySelectorResult() {
                    @Override
                    public Key getKey() {
                       List<X509Data> dataList = keyInfo.getContent();
                       List<X509Certificate> certList = dataList.get(0).getContent();
                       X509Certificate cert = certList.get(0);
                        try {
                            x509Cert.verify(cacert.getPublicKey());
                        } catch (CertificateException e) {
                            throw new RuntimeException(e);
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        } catch (InvalidKeyException e) {
                            throw new RuntimeException(e);
                        } catch (NoSuchProviderException e) {
                            throw new RuntimeException(e);
                        } catch (SignatureException e) {
                            throw new RuntimeException(e);
                        }
                        return cert.getPublicKey();

                    }
                };
            }
        };

        // Create a DOMValidateContext and specify a KeySelector
        // and document context.
        DOMValidateContext valContext = new DOMValidateContext
                (selector, nl.item(0));

        // Unmarshal the XMLSignature.
        XMLSignature xmlSignature = factory.unmarshalXMLSignature(valContext);

        // Validate the XMLSignature.
        boolean coreValidity = signature.validate(valContext);

        assertTrue(coreValidity);
    }
}
