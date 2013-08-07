package org.netkit.nio;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import static java.util.Collections.unmodifiableSet;

/**
 * Date: 8/6/13
 * Time: 6:07 PM
 */
public final class AttributeMap implements AttributeContainer{

    private final Map<AttributeKey<?>,Object> attributes = new ConcurrentHashMap<AttributeKey<?>, Object>();

    public <T> T getAttribute(AttributeKey<T> key, T defaultValue) {
        T value = (T) attributes.get(key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    @Override
    public <T> T getAttribute(AttributeKey<T> key) {
        T value = (T)attributes.get(key);
        return value;
    }

    @Override
    public <T> T removeAttribute(AttributeKey<T> key) {
        return (T)attributes.remove(key);
    }

    @Override

    public <T> T setAttribute(AttributeKey<? extends T> key, T value) {
        if(value == null)
            return removeAttribute(key);
        return (T) attributes.put(key,value);
    }

    @Override
    public Set<AttributeKey<?>> getAttributekeys() {
        return unmodifiableSet(attributes.keySet());
    }
}
