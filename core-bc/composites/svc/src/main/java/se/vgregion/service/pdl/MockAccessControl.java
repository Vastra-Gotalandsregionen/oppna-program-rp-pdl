package se.vgregion.service.pdl;


import org.springframework.stereotype.Service;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.decorators.WithAccess;

@Service
public class MockAccessControl implements AccessControl {
    @Override
    public WithAccess<PdlContext> authorize(PdlContext ctx) {
        return WithAccess.sameProvider(ctx);
    }
}
