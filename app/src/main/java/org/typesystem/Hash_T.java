package org.typesystem;

import org.typesystem.utils.*;

import java.util.Map;

public class Hash_T implements Object_T {
    Map<HashKey, HashPair> pairs;

    public Hash_T(Map<HashKey, HashPair> pairs) {
        this.pairs = pairs;
    }

    @Override
    public String type() {
        return TypeList.HASH_OBJECT;
    }

    @Override
    public String inspect() {
        String res = "{ \n";
        for (HashPair pair : this.pairs.values()) {
            res += pair.getKey().inspect() + " : " + pair.getValue().inspect() + ",\n ";
        }
        res += " }";
        return res;
    }

    public Map<HashKey, HashPair> getPairs() {
        return this.pairs;
    }
}
