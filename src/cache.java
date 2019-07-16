import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class cache {
    public static LinkedHashMap<String,List> CacheDir = new LinkedHashMap<String,List>(21, .75F, false){
        protected boolean removeEldestEntry(HashMap.Entry eldest) {
            return size() > 20;
        }
    };

}
