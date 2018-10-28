package com.ajkaandrej.hugoup.util;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;

public class YamlUtil {
    
    public static <T> T readFile(String file, Class<T> clazz) throws Exception {
        YamlConfig yamlConfig = new YamlConfig();
        yamlConfig.readConfig.setClassTags(false);
        YamlReader reader = new YamlReader(new FileReader(file));
        return reader.read(clazz);        
    }
    
    public static String toString(Object data) throws Exception {
        YamlConfig yamlConfig = new YamlConfig();
        yamlConfig.writeConfig.setWriteClassname(YamlConfig.WriteClassName.NEVER);
        StringWriter w = new StringWriter();
        YamlWriter writer = new YamlWriter(w, yamlConfig);
        writer.write(data);
        writer.close();          
        return w.toString();
    }
    
    public static void writeFile(String file, Object data) throws Exception {
        YamlConfig yamlConfig = new YamlConfig();
        yamlConfig.writeConfig.setWriteClassname(YamlConfig.WriteClassName.NEVER);
        YamlWriter writer = new YamlWriter(new FileWriter(file), yamlConfig);
        writer.write(data);
        writer.close();        
    }
}
