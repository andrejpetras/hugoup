package com.ajkaandrej.hugoup.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class HashResult {
    
    private String version;
    
    private Map<String, String> data = new HashMap<>();
    
}
