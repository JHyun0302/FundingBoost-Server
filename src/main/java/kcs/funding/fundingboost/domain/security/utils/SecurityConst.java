package kcs.funding.fundingboost.domain.security.utils;

public interface SecurityConst {
    String TOKEN_PREFIX = "Bearer ";
    String REDIS_BLACK_KEY = "black";
    long REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS = 86400000;
    long REFRESH_TOKEN_VALIDITY_IN_SECONDS = REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS / 1000;
    long ACCESS_TOKEN_VALIDITY_IN_MILLISECONDS = 864000;
    long ACCESS_TOKEN_VALIDITY_IN_SECONDS = ACCESS_TOKEN_VALIDITY_IN_MILLISECONDS / 1000;
    String SECRET = "tmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmp";
}
