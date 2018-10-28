package org.lorislab;

import org.lorislab.util.YamlUtil;
import org.lorislab.model.Config;
import org.lorislab.util.GitUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;

public class Main {
   
    private static final String[] CMD_DEPLOY = { "hash", "latest", "diff", "upload" };
    
    public static void main(String[] args) throws Exception {
     
        Config config = createConfig();
        
        if (args == null || args.length == 0) {
            info();
        } else {
            
            LinkedList<String> cmds;            
            switch (args[0]) {
                case "deploy":
                    cmds = new LinkedList<>(Arrays.asList(CMD_DEPLOY));
                    break;
                default:
                    cmds = new LinkedList<>(Arrays.asList(args));
            }
            
            while (!cmds.isEmpty()) {
                command(config, cmds.pop(), cmds);
            }            
        }
    }
    
    private static void command(Config config, String command, LinkedList<String> cmds) throws Exception {
        System.out.println("Execute command: " + command);
        switch (command) {
            case "version":
                System.out.println(GitUtil.getVersion());
                break;
            case "config":
                System.out.println("Configuration: " + config);
                break;
            case "hash":
                CmdHash.createHash("public", "public/latest.hash" );
                break;
            case "diff":
                CmdDiff.createDiff("latest.hash", "public/latest.hash", "latest.diff", "latest.hash");
                break;
            case "changes":
                CmdDiff.chnages("latest.diff");
                break;
            case "latest":
                CmdFtp.download(config, "latest.hash", "latest.hash");
                break;
            case "upload":
                CmdUpload.upload(config, "public/", "latest.diff");
                break;
            case "ftp":
                switch (cmds.pop()) {
                    case "ls" : CmdFtp.ls(config, cmds.pop()); break;
                    case "mkdir" : CmdFtp.mkdir(config, cmds.pop()); break;
                    default:
                        System.out.println("Missing ftp command!");
                        break;
                }
                break;                
            default:
                info();
                throw new RuntimeException("Wrong command " + command);
        }
    }
    
    private static void info() {
        System.out.println("Commands: config|hash|diff|latest|deploy|upload|changes|ftp ls <path>|ftp mkdir <path>");        
    }
    
    private static Config createConfig() throws Exception {
        
        Path file = Paths.get(System.getProperty("user.home"), ".hugoup");
        
        if (!Files.exists(file)) {
            System.out.println("Example of the ~/.hugoup file.");
            Config tmp = new Config();
            tmp.setHost("host_example");
            tmp.setUser("host_example");
            tmp.setPassword("host_example");
            tmp.setPath("host_example");
            tmp.setPort(21);            
            System.out.println(YamlUtil.toString(tmp));
            
            throw new RuntimeException("Missing config ~/.hugoup file.");
        }
        
        Config config = YamlUtil.readFile(file.toString(), Config.class);
        
        // host
        String host = System.getenv("FTP_HOST");
        if (host != null && !host.isEmpty()) {
            config.setHost(host);
        }
        // user
        String user = System.getenv("FTP_USER");
        if (user != null && !user.isEmpty()) {
            config.setUser(user);
        }   
        // password
        String password = System.getenv("FTP_PASSWORD");
        if (password != null && !password.isEmpty()) {
            config.setPassword(password);
        }   
        // path
        String path = System.getenv("FTP_PATH");
        if (path != null && !path.isEmpty()) {
            config.setPath(path);
        }   
        // port
        String port = System.getenv("FTP_PORT");
        if (port != null && !port.isEmpty()) {
            Integer p = Integer.valueOf(port);
            config.setPort(p);
        }        
        return config;
        
    }    
}
