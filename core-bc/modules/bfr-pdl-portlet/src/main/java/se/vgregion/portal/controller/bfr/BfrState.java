package se.vgregion.portal.controller.bfr;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BfrState {
    private List<String> infobrokerIds;

    public BfrState() {
    }

    public List<String> getInfobrokerIds() {
        return infobrokerIds;
    }

    public void setInfobrokerIds(List<String> infobrokerIds) {
        this.infobrokerIds = infobrokerIds;
    }
}
