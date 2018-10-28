package org.lorislab.hugoup.model;

import lombok.Data;

@Data
public class Config {
 
    private String host;
    
    private String user;
    
    private String password;
    
    private Integer port = 21;
    
    private String path;
    
}
