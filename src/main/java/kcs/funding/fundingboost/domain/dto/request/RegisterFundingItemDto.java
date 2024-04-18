package kcs.funding.fundingboost.domain.dto.request;

public record RegisterFundingItemDto(
        Long itemId,
        int itemSequence
) {
    public static RegisterFundingItemDto createRegisterFundingItemDto(Long itemId, int itemSequence) {
        return new RegisterFundingItemDto(itemId, itemSequence);
    }
}