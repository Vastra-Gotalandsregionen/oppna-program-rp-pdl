package se.vgregion.domain.bfr.decorators;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class WithZfpUrls<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = -5965647759183691571L;

    public final List<String> urls;
    public final T value;

    public WithZfpUrls(List<String> urls, T value) {
        this.urls = Collections.unmodifiableList(urls);
        this.value = value;
    }

    public WithZfpUrls<T> mapVisibility(List<String> newUrls) {
        return new WithZfpUrls<T>(Collections.unmodifiableList(newUrls), value);
    }

    public <N extends Serializable> WithZfpUrls<N> mapValue(N newValue) {
        return new WithZfpUrls<N>(urls, newValue);
    }

    public List<String> getUrls() {
        return urls;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "WithZfpUrls{" +
                "urls=" + urls +
                ", value=" + value +
                '}';
    }
}
