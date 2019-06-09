package cenamos.service;

import cenamos.Config;
import cenamos.MyLogger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

    private final String requestApiToken;

    public AuthService(Config config) {
        requestApiToken = config.getRequestApiToken();
        MyLogger.logger(AuthService.class).info("starting up, request {}", requestApiToken);
    }

    public boolean authorized(String token){
        if (StringUtils.isEmpty(requestApiToken)){
            return true;
        }
        return requestApiToken.equals(token);
    }
}
