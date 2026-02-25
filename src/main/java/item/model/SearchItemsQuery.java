package item.model;

import jakarta.annotation.Nullable;

public class SearchItemsQuery {

    @Nullable
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String toString() {
        return key;
    }
}
