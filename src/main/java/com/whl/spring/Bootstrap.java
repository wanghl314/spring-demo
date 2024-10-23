package com.whl.spring;

import org.apache.catalina.Executor;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardVirtualThreadExecutor;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;

public class Bootstrap {
    private static final int PORT = 8080;

    private static final String URI_ENCODING = "UTF-8";

    private static final String CONTEXT_PATH = "/spring";

    private static final String WEBAPP = "src/main/webapp";

    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();

        File baseDir = createTempDir("tomcat");
        tomcat.setBaseDir(baseDir.getAbsolutePath());

        Executor executor = new StandardVirtualThreadExecutor();

        Connector connector = new Connector();
        connector.setPort(PORT);
        connector.setURIEncoding(URI_ENCODING);
        connector.getProtocolHandler().setExecutor(executor);

//        ssl 配置
//        SSLHostConfig config = new SSLHostConfig();
//        SSLHostConfigCertificate certificate = new SSLHostConfigCertificate(config, SSLHostConfigCertificate.Type.RSA);
//        certificate.setCertificateKeystorePassword("ebridge");
//        certificate.setCertificateKeystoreFile("D:\\emp\\appsvr\\tomcat\\conf\\emp.ssl.jks");
//        config.addCertificate(certificate);
//
//        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
//        protocol.setSSLEnabled(true);
//        protocol.setSecure(true);
//        protocol.addSslHostConfig(config);
//        connector.setScheme("https");

        tomcat.getService().addConnector(connector);
        tomcat.getService().addExecutor(executor);

        File docBase = new File(WEBAPP);
        tomcat.addWebapp(CONTEXT_PATH, docBase.getAbsolutePath());

        tomcat.getHost().setAutoDeploy(false);

        tomcat.start();
        tomcat.getServer().await();
    }

    private static File createTempDir(String prefix) throws IOException {
        File tempDir = File.createTempFile(prefix + ".", "." + PORT);
        tempDir.delete();
        tempDir.mkdir();
        tempDir.deleteOnExit();
        return tempDir;
    }

}
