package com.ajkaandrej.hugoup;

import com.ajkaandrej.hugoup.model.Config;
import com.ajkaandrej.hugoup.model.DiffResult;
import com.ajkaandrej.hugoup.util.YamlUtil;
import java.io.FileInputStream;

public class CmdUpload {

    
    public static void upload(Config config, String dir, String file) throws Exception {
        
        DiffResult result = YamlUtil.readFile(file, DiffResult.class);
        
        try (CmdFtp.FtpClient ftp = new CmdFtp.FtpClient(config)) {
            ftp.connect();
            
            // add new files
            System.out.println("Add [" + result.getAdd().size() + "]");
            for (String add : result.getAdd()) {                
                String tmp = dir + add;
                String targetDir = add.substring(0, add.lastIndexOf('/'));
                System.out.println("  - " + tmp + " --> " + add + " ,mkdir " + targetDir); 
                ftp.makeDirectories(targetDir);
                ftp.getClient().storeFile(add, new FileInputStream(tmp));
            }

            // update files
            System.out.println("Update [" + result.getUpdate().size() + "]");
            for (String update : result.getUpdate()) {                
                String tmp = dir + update;
                System.out.println("  - " + tmp + " --> " + update);                
                ftp.getClient().storeFile(update, new FileInputStream(tmp));
            }
            
            // delete files
            System.out.println("Delete [" + result.getDelete().size() + "]");
            for (String delete : result.getDelete()) {                
                System.out.println("  - " + delete);
                ftp.getClient().deleteFile(delete);
            }            
            
            ftp.getClient().logout();
        }        
    }
   
}
