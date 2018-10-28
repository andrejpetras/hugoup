package com.ajkaandrej.hugoup;

import com.ajkaandrej.hugoup.util.YamlUtil;
import com.ajkaandrej.hugoup.model.HashResult;
import com.ajkaandrej.hugoup.util.GitUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class CmdHash {

    static final byte[] BUFFER = new byte[8192];

    private static MessageDigest MD;

    static {
        try {
            MD = MessageDigest.getInstance("SHA-1"); //SHA-1, SHA, MD2, MD5, SHA-256, SHA-384...
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void createHash(String dir, String output) throws Exception {
        System.out.println("Creating hash information ....");        
        Path file = Paths.get(dir);
        
        String version = GitUtil.getVersion();

        HashResult result = new HashResult();
        result.setVersion(version);
        hashFiles(result, file, file);
        YamlUtil.writeFile(output, result);     
        System.out.println("Hash file '" + output + "' created.");
    }

    private static void hashFiles(HashResult result, Path fp, Path dir) throws Exception {
        if (Files.isDirectory(fp)) {
            List<Path> children = Files.list(fp).collect(Collectors.toList());       
            for (Path child : children) {
                hashFiles(result, child, dir);
            }
        } else {
            result.getData().put(dir.relativize(fp).toString(), hashFile(fp));
        }
    }

    private static String hashFile(Path fp) {
        MD.reset();
        try (InputStream in = new FileInputStream(fp.toFile())) {
            int len = in.read(BUFFER);
            while (len >= 0) {;
                MD.update(BUFFER, 0, len);
                len = in.read(BUFFER);
            }
            StringBuilder result = new StringBuilder();
            for (byte b : MD.digest()) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
