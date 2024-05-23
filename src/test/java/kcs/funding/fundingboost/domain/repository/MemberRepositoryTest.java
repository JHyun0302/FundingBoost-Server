package kcs.funding.fundingboost.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import kcs.funding.fundingboost.domain.config.QueryDslConfig;
import kcs.funding.fundingboost.domain.entity.member.Member;
import kcs.funding.fundingboost.domain.model.MemberFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QueryDslConfig.class)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        member = MemberFixture.member1();
    }

    @AfterEach
    void after() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("닉네임으로 사용자 검색")
    void findByNickName() {
        Member savedMember = memberRepository.save(member);
        //when
        Optional<Member> searchMember = memberRepository.findByNickName(savedMember.getNickName());

        //then
        assertThat(searchMember).isPresent();
        assertThat(searchMember.get().getNickName()).isEqualTo(savedMember.getNickName());
        assertThat(searchMember.get().getMemberId()).isEqualTo(savedMember.getMemberId());
    }
}