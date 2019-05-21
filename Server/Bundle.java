import java.util.HashMap;

public class Bundle{
    private HashMap<String, Object> map = new HashMap<>();

    void put(String key, Object o){ map.put(key, o); }
    int getInt(String key){ return (int)(map.get(key)); }
    double getDouble(String key){ return (double) (map.get(key)); }
    String getString(String key){ return (String) (map.get(key)); }
    boolean getBoolean(String key){ return (boolean) (map.get(key)); }
    Object getObject(String key){ return map.get(key); }
}
