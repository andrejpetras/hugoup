package com.ajkaandrej.hugoup;

import com.ajkaandrej.hugoup.model.DiffResult;
import com.ajkaandrej.hugoup.model.HashResult;
import com.ajkaandrej.hugoup.util.YamlUtil;
import java.util.Map;
import java.util.Map.Entry;

public class CmdDiff {

    public static void chnages(String output) throws Exception {
        DiffResult result = YamlUtil.readFile(output, DiffResult.class);
        System.out.println("Update [" + result.getUpdate().size() + "]");
        for (String item : result.getUpdate()) {
            System.out.println("  - "+ item);
        }
        System.out.println("Add [" + result.getAdd().size() + "]");
        for (String item : result.getAdd()) {
            System.out.println("  - "+ item);
        }        
        System.out.println("Delete [" + result.getDelete().size() + "]");
        for (String item : result.getDelete()) {
            System.out.println("  - "+ item);
        }       
        System.out.println("Old version: " + result.getOldVersion());
        System.out.println("New version: " + result.getNewVersion());        
    }
    
    public static void createDiff(String oldFile, String newFile, String output, String hashFile) throws Exception {
        HashResult oldHash = YamlUtil.readFile(oldFile, HashResult.class);
        HashResult newHash = YamlUtil.readFile(newFile, HashResult.class);

        DiffResult result = new DiffResult();
        result.setNewVersion(newHash.getVersion());
        result.setOldVersion(oldHash.getVersion());

        Map<String, String> old = oldHash.getData();
        for (Entry<String, String> entry : newHash.getData().entrySet()) {
            if (!old.containsKey(entry.getKey())) {
                result.getAdd().add(entry.getKey());
            } else {
                if (!entry.getValue().equals(old.get(entry.getKey()))) {
                    result.getUpdate().add(entry.getKey());
                }
            }
        }

        // find delete
        for (String key : oldHash.getData().keySet()) {
            if (!oldHash.getData().containsKey(key)) {
                result.getDelete().add(key);
            }
        }

        if (!result.isEmpty()) {
            result.getUpdate().add(hashFile);
        }
        
        System.out.println("Found " + result.getAdd().size() + " new files");
        System.out.println("Found " + result.getUpdate().size() + " changed files");
        System.out.println("Found " + result.getDelete().size() + " deleted files");

        YamlUtil.writeFile(output, result);
    }

}
