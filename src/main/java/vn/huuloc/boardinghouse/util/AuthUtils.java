package vn.huuloc.boardinghouse.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import vn.huuloc.boardinghouse.constant.SecurityConstants;

@UtilityClass
public class AuthUtils {
    public static String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(SecurityConstants.AUTHORIZATION);
        // Kiểm tra xem header Authorization có chứa thông tin jwt không
        if (org.springframework.util.StringUtils.hasText(bearerToken) && bearerToken.startsWith(SecurityConstants.BEARER)) {
            return bearerToken.substring(SecurityConstants.BEARER.length());
        }
        return null;
    }
}
