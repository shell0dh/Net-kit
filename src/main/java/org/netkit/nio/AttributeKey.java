package org.netkit.nio;

/**
   * Date: 8/6/13
 * Time: 5:11 PM
  */
public class AttributeKey<T> {
    private final Class<T> attributeType;
    private final String attributeName;

    private final int hashCode;

    public AttributeKey(Class<T> type,String name){
        this.attributeName = name;
        this.attributeType = type;
        hashCode =  createHashCode();
    }

    public static <T> AttributeKey<T> createKey(Class<T> type,String name){
        return new AttributeKey<T>(type,name);
    }

    private int createHashCode(){
        final int prime = 31;
        int result = prime + this.attributeName.hashCode();
        result = prime * result + attributeType.hashCode();
        return result;
    }

    public String getName(){
        return attributeName;
    }

    public Class<T> getType(){
        return attributeType;
    }

    public int hashCode(){
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }

        if(obj == null) return false;

        if(getClass() != obj.getClass()) return false;

        AttributeKey<?> other = (AttributeKey<?>) obj;

        return hashCode == other.hashCode;
    }
}
