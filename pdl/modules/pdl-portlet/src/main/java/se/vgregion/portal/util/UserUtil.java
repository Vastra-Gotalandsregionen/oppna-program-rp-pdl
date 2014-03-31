package se.vgregion.portal.util;

import com.liferay.portal.util.PortalUtil;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Patrik Bergström
 */
public class UserUtil {

    public String getUserId(PortletRequest request) {
        Map<String, String> userInfo = (Map<String, String>) request.getAttribute(PortletRequest.USER_INFO);
        String info;
        if (userInfo != null) {
            info = userInfo.get(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString());
        } else {
            throw new IllegalStateException("User login id was not found on request.");
        }
        return info;
    }

    public String getHsaIdFromRequestHeader(PortletRequest request) {
        HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(request);

        String hsaIdentity = httpServletRequest.getHeader("hsaid");

        return hsaIdentity;
    }
}
