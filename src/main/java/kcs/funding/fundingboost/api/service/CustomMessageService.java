package kcs.funding.fundingboost.api.service;

import java.util.Arrays;
import java.util.List;
import kcs.funding.fundingboost.api.dto.DefaultMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomMessageService {
    private final MessageService messageService;

    public boolean sendReminderMessage() {
        DefaultMessageDto myMsg = DefaultMessageDto.createDefaultMessageDto("text", "바로 확인하기",
                "https://www.naver.com", "펀딩 남은 기간이 2일 남았습니다!!");
        String accessToken = HttpCallService.accessToken;
        return messageService.sendMessageToMe(accessToken, myMsg);
    }

    public boolean sendMessageToFriends() {
        DefaultMessageDto myMsg = DefaultMessageDto.createDefaultMessageDto("text", "버튼 버튼",
                "https://www.naver.com", "내가 지금 생각하고 있는 것은??");
        String accessToken = HttpCallService.accessToken;
        List<String> friendUuids = Arrays.asList("친구의 UUID 여기에 추가"); // TODO: 실제로는 적절한 UUID 목록을 제공해야 합니다.
        return messageService.sendMessageToFriends(accessToken, myMsg, friendUuids);
    }
}
