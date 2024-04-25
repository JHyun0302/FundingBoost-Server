package kcs.funding.fundingboost.domain.service;

import java.util.List;
import java.util.Optional;
import kcs.funding.fundingboost.domain.dto.response.myPage.orderHistory.OrderHistoryDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.orderHistory.OrderHistoryItemDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.orderHistory.OrderHistoryMemberDto;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.entity.OrderItem;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.exception.ErrorCode;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.orderItem.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderHistoryDto getOrderHistory(Long memberId) {
        Optional<List<OrderItem>> orderItemList = orderItemRepository.findLastOrderByMemberId(memberId);

        if (orderItemList.isPresent()) {
            // 주문 목록이 존재하는 경우
            List<OrderItem> orderItems = orderItemList.get();
            List<OrderHistoryItemDto> orderHistoryItemDtoList = orderItems.stream()
                    .map(OrderHistoryItemDto::fromEntity)
                    .toList(); // orderItemDtoList 초기화

            Member member = orderItems.get(0).getOrder().getMember();
            OrderHistoryMemberDto orderHistoryMemberDto = OrderHistoryMemberDto.fromEntity(member); // memberDto 초기화
            return OrderHistoryDto.fromEntity(orderHistoryMemberDto, orderHistoryItemDtoList);
        } else {
            // 주문 목록이 존재하지 않는 경우
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
            OrderHistoryMemberDto orderHistoryMemberDto = OrderHistoryMemberDto.fromEntity(member); // memberDto 초기화
            return OrderHistoryDto.fromEntity(orderHistoryMemberDto, null);
        }

    }
}
