package com.fhalcom.facturacion.xsd;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.stream.Collectors;
import java.io.InputStream;

@Component
public class XsdRegistry {
  private final Map<String, List<String>> versionsByType = new HashMap<>();
  private final Map<String, String> pathByKey = new HashMap<>(); // tipo:version -> path
  private final ObjectMapper om = new ObjectMapper();

  public XsdRegistry(){
    try{
      // Try to read static index.json if present
      PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
      Resource idx = r.getResource("classpath:/xsd/vendor/index.json");
      if(idx.exists()){
        try(InputStream in = idx.getInputStream()){
          Map<String,Object> map = om.readValue(in, Map.class);
          Map<String, List<String>> vbt = (Map<String, List<String>>) map.get("versionsByType");
          if(vbt != null){ versionsByType.putAll(vbt); }
        }
      }
      // Always also scan dynamically to pick up any new resources
      for(Resource res : r.getResources("classpath*:/xsd/vendor/sri/**/*.xsd")){
        String path = res.getURL().toString();
        // expected pattern .../xsd/vendor/sri/<tipo>/<version>.xsd
        int sriIdx = path.indexOf("/xsd/vendor/sri/");
        if(sriIdx==-1) continue;
        String tail = path.substring(sriIdx + "/xsd/vendor/sri/".length());
        String[] parts = tail.split("/");
        if(parts.length!=2) continue;
        String tipo = parts[0];
        String version = parts[1].replace(".xsd","");
        versionsByType.computeIfAbsent(tipo, k->new ArrayList<>());
        if(!versionsByType.get(tipo).contains(version)) versionsByType.get(tipo).add(version);
      }
      // Normalize: sort versions semantically if possible
      for(List<String> vs : versionsByType.values()){
        Collections.sort(vs, (a,b)->a.compareTo(b));
      }
      // Build key->path map (classpath path form)
      for(Map.Entry<String,List<String>> e : versionsByType.entrySet()){
        for(String v: e.getValue()){
          String cp = "/xsd/vendor/sri/"+e.getKey()+"/"+v+".xsd";
          pathByKey.put(e.getKey()+":"+v, cp);
        }
      }
    }catch(Exception e){
      throw new RuntimeException("Error initializing XsdRegistry", e);
    }
  }

  public List<String> versionsFor(String tipo){
    return versionsByType.getOrDefault(tipo, List.of());
  }
  public String pathFor(String tipo, String version){
    return pathByKey.get(tipo+":"+version);
  }
  public Map<String, List<String>> all(){ return java.util.Collections.unmodifiableMap(versionsByType); }
  public String latestPath(String tipo){
    var vs = versionsFor(tipo);
    if(vs.isEmpty()) return null;
    return pathFor(tipo, vs.get(vs.size()-1));
  }
}
