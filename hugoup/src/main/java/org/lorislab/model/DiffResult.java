package org.lorislab.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class DiffResult {
    
    private String oldVersion;
    
    private String newVersion;
    
    private List<String> add = new ArrayList<>();
    
    private List<String> update = new ArrayList<>();
    
    private List<String> delete = new ArrayList<>();
    
    public boolean isEmpty() {
        return add.isEmpty() && update.isEmpty() && delete.isEmpty();
    }
}
