package kcs.funding.fundingboost.domain.service;

import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_MEMBER;

import java.util.List;
import kcs.funding.fundingboost.domain.dto.response.myPage.MyPageMemberDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.wishList.BookmarkListItemDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.wishList.MyBookmarkListDto;
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

    public MyBookmarkListDto getMyWishList(Long memberId) {

        List<BookmarkListItemDto> wishtListItemDtos = bookmarkRepository.findAllByMemberId(memberId).stream()
                .map(bookmark -> BookmarkListItemDto.fromEntity(bookmark.getItem())).toList();

        MyPageMemberDto myPageMemberDto = MyPageMemberDto.fromEntity(
                memberRepository.findById(memberId).orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER)));

        return MyBookmarkListDto.fromEntity(myPageMemberDto, wishtListItemDtos);
    }
}
