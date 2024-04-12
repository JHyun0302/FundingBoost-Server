package kcs.funding.fundingboost.domain.entity;


public enum Tag {

    BIRTHDAY("#생일"),
    EMAIL("#졸업"),
    ETC("#기타");

    private final String tag;

    Tag(String tag) {
        this.tag = tag;
    }
}
