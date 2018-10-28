package org.lorislab;

import org.lorislab.model.Config;
import org.lorislab.model.DiffResult;
import org.lorislab.util.YamlUtil;
import java.io.FileInputStream;

public class CmdUpload {

    
    public static void upload(Config config, String dir, String file) throws Exception {
        
        DiffResult result = YamlUtil.readFile(file, DiffResult.class);
        
        try (CmdFtp.FtpClient ftp = new CmdFtp.FtpClient(config)) {
            ftp.connect();
            
            // add new files
            System.out.println("Add [" + result.getAdd().size() + "]");
            for (int i=0; i<result.getAdd().size(); i++) {                
                String add = result.getAdd().get(i);
                String tmp = dir + add;
                
                int index = add.lastIndexOf('/');
                if (index != -1) {
                    String targetDir = add.substring(0, index);
                    System.out.println("[" + (result.getAdd().size() - i) + "] - " + tmp + " --> " + add + " ,mkdir " + targetDir); 
                    ftp.makeDirectories(targetDir);
                    ftp.getClient().changeWorkingDirectory(config.getPath());
                } else {
                    System.out.println("[" + i + "] - " + tmp + " --> " + add); 
                }
                try (FileInputStream fi = new FileInputStream(tmp)) {
                    ftp.getClient().storeFile(add, fi);
                }
            }

            // update files
            System.out.println("Update [" + result.getUpdate().size() + "]");
            for (int i=0; i<result.getUpdate().size(); i++) {    
                String update = result.getUpdate().get(i);
                String tmp = dir + update;
                System.out.println("[" + (result.getUpdate().size() - i) + "] - " + tmp + " --> " + update);      
                try (FileInputStream fi = new FileInputStream(tmp)) {
                    ftp.getClient().storeFile(update, fi);
                }
            }
            
            // delete files
            System.out.println("Delete [" + result.getDelete().size() + "]");
            for (int i=0; i<result.getDelete().size(); i++) {          
                String delete = result.getDelete().get(i);
                System.out.println("[" + (result.getDelete().size() - i) + "] - " + delete);
                ftp.getClient().deleteFile(delete);
            }            
            
            ftp.getClient().logout();
        }        
    }
   
}
