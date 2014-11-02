/*
Contains 2 linkedhashmaps, with the key/values reversed
Used for holding the tiles/objects and their id
Much faster for finding the tile/object when loading
*/
import java.util.LinkedHashMap;


public class biMap<K, V>
{
  //map with K,V
  private LinkedHashMap<K, V> normalMap;
  
  //map with V,K
  private LinkedHashMap<V, K> reverseMap;
  
  public biMap()
  {
    normalMap = new LinkedHashMap<K, V>();
    reverseMap = new LinkedHashMap<V, K>();
  }
  
  //add keys/values to the maps
  public void put(K key, V value)
  {
    normalMap.put(key, value);
    reverseMap.put(value, key);
  }
  
  //get the Value by the Key
  public V getNormal(K key)
  {
    return normalMap.get(key);
  }
  
  //get the Key by the Value
  public K getReverse(V value)
  {
    return reverseMap.get(value);
  }
  
  //get the size of the maps
  public int size()
  {
    return normalMap.size();
  }
  
  //determine if it contains a key
  public Boolean containsKey(K key)
  {
    return normalMap.containsKey(key);
  }
  
  //determine if it contains a value
  public Boolean containsValue(V value)
  {
    return normalMap.containsValue(value);
  }
}