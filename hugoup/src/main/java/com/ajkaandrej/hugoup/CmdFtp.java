package com.ajkaandrej.hugoup;

import com.ajkaandrej.hugoup.model.Config;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPClient;

public class CmdFtp {

    final static Logger LOGGER = Logger.getLogger(CmdFtp.class.getName());

    public static void mkdir(Config config, String path) throws Exception {
        try (FtpClient ftp = new FtpClient(config)) {
            ftp.connect();
            ftp.makeDirectories(path);
            ftp.getClient().logout();
        }
    }
    
    public static void ls(Config config, String path) throws Exception {
        try (FtpClient ftp = new FtpClient(config)) {
            ftp.connect();
            ftp.getClient().changeWorkingDirectory(path);
            System.out.println(ftp.getClient().printWorkingDirectory());
            for (String f : ftp.getClient().listNames()) {
                System.out.println("  - " + f);
            }
            ftp.getClient().logout();
        }
    }

    public static void download(Config config, String source, String target) throws Exception {
        System.out.println("Start to download file " + source + " to " + target);
        try (FtpClient ftp = new FtpClient(config)) {
            ftp.connect();
            Files.copy(ftp.getClient().retrieveFileStream(source), Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
            ftp.getClient().logout();
        }
        System.out.println("The file " + target + " has been downloaded.");
    }

    public static class FtpClient implements AutoCloseable {

        private final FTPClient client;

        private final Config config;

        public FtpClient(Config config) {
            this.client = new FTPClient();
            
            // This will cause the file upload/download methods to send a NOOP approximately every 5 minutes.
            client.setControlKeepAliveTimeout(300);
            
            this.config = config;
        }

        public void connect() throws Exception {
            client.connect(config.getHost(), config.getPort());
            client.login(config.getUser(), config.getPassword());
            client.changeWorkingDirectory(config.getPath());
        }

        @Override
        public void close() throws Exception {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
        }

        public FTPClient getClient() {
            return client;
        }

        public boolean makeDirectories(String dirPath) throws IOException {
            String[] pathElements = dirPath.split("/");
            if (pathElements != null && pathElements.length > 0) {
                for (String singleDir : pathElements) {
                    boolean existed = client.changeWorkingDirectory(singleDir);
                    if (!existed) {
                        boolean created = client.makeDirectory(singleDir);
                        if (created) {
                            client.changeWorkingDirectory(singleDir);
                        } else {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

    }
}
