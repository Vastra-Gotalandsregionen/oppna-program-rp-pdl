package se.vgregion.service.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.ssl.ConvenientSslContextFactory;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * @author Patrik Björk
 */
public class HsaSslSocketFactory extends SocketFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HsaSslSocketFactory.class);

    private static final String PROPERTY_FILE = System.getProperty("user.home") + "/.rp/pdl/security.properties";

    private final SSLSocketFactory wrappedSocketFactory;

    public HsaSslSocketFactory(ConvenientSslContextFactory convenientSslContextFactory) {
        try {
            wrappedSocketFactory = convenientSslContextFactory.createSslContext().getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SocketFactory getDefault() {
        Properties properties = new Properties();

        try (FileReader reader = new FileReader(PROPERTY_FILE)){

            properties.load(reader);

            ConvenientSslContextFactory convenientSslContextFactory = new ConvenientSslContextFactory(
                    properties.getProperty("hsa.ldap.truststore.fullpath"),
                    properties.getProperty("hsa.ldap.truststore.password"),
                    null,
                    null);

            return new HsaSslSocketFactory(convenientSslContextFactory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
        return wrappedSocketFactory.createSocket(s, i);
    }

    @Override
    public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
        return wrappedSocketFactory.createSocket(s, i, inetAddress, i1);
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
        return wrappedSocketFactory.createSocket(inetAddress, i);
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
        return wrappedSocketFactory.createSocket(inetAddress, i, inetAddress1, i1);
    }
}
