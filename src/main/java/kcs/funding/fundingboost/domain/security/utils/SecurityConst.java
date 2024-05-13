package kcs.funding.fundingboost.domain.security.utils;

public interface SecurityConst {
    String AUTHORITIES_KEY = "auth";
    String TOKEN_PREFIX = "Bearer ";
    long refreshTokenValidityInMilliseconds = 864000000;
    long accessTokenValidityInMilliseconds = 864000;
    String secret = "tmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmptmp";
}
