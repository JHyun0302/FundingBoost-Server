package kcs.funding.fundingboost.domain.service;

import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_MEMBER;

import java.util.List;
import kcs.funding.fundingboost.domain.dto.response.MyPageMemberDto;
import kcs.funding.fundingboost.domain.dto.response.MyWishListDto;
import kcs.funding.fundingboost.domain.dto.response.WishtListItemDto;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.bookmark.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;

    public MyWishListDto getMyWishList(Long memberId) {

        List<WishtListItemDto> wishtListItemDtos = bookmarkRepository.findAllByMemberId(memberId).stream()
                .map(bookmark -> WishtListItemDto.fromEntity(bookmark.getItem())).toList();

        MyPageMemberDto myPageMemberDto = MyPageMemberDto.fromEntity(
                memberRepository.findById(memberId).orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER)));

        return MyWishListDto.fromEntity(myPageMemberDto, wishtListItemDtos);
    }
}
