package com.example.demo.auction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.example.demo.notification.Notification;
import com.example.demo.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.bid.BidDto;
import com.example.demo.bid.BidService;
import com.example.demo.chat.domain.ChatRoom;
import com.example.demo.chat.service.ChatRoomService;
import com.example.demo.user.Member;
import com.example.demo.user.MemberDto;
import com.example.demo.user.MemberService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuctionScheduler {

	private final AuctionService service;
	private final BidService bidService;
	private final ChatRoomService chatRoomService;
	private final SimpMessagingTemplate messagingTemplate;
	private final NotificationRepository notificationRepository;
	private final MemberService mservice;


//	@Scheduled(cron = "0 0/1 * * * *") // 매 1분에 실행
	@Scheduled(fixedRate = 10000)
	public void setStatus() {
		Date date = new Date();
		ArrayList<AuctionDto> list = service.getByStatus("경매중");
		for (AuctionDto auction : list) {
			if (auction.getEnd_time().before(date)) {
				auction.setStatus("경매 마감");
				Member seller = auction.getSeller();
				Notification notification = Notification.create(auction.getSeller().getId(), auction.getTitle(), "경매 마감되었습니다✅");
				notificationRepository.save(notification);
				messagingTemplate.convertAndSend("/sub/notice/list/"+auction.getSeller().getId(), notificationRepository.findByName(auction.getSeller().getId()));
				log.debug("notification={}", notification);
				try {
					BidDto byBuyer = bidService.getByBuyer(auction.getNum());
					if (auction.getType().equals(Auction.Type.BLIND)) {
						boolean flag = true;
						ArrayList<BidDto> buylist = bidService.getByParent2(auction.getNum());
						for (BidDto dto : buylist) {
							if (dto.getPrice() != auction.getMax() && flag) {
								Member loser = dto.getBuyer();
								loser.setPoint(loser.getPoint() + dto.getPrice());
							}
							byBuyer.setBuyer(dto.getBuyer());
							flag = false;
						}
					}
					if (auction.getType().equals(Auction.Type.EVENT)) {
						ArrayList<BidDto> buylist = bidService.getByParent(auction.getNum());
						Map<Member, Object> map = new HashMap();
						int total = 0;
						for (BidDto bdto : buylist) {
							map.put(bdto.getBuyer(), 0);
							total = total + bdto.getPrice();
						}
						for (BidDto bdto : buylist) {
							int price = (int) map.get(bdto.getBuyer());
							map.put(bdto.getBuyer(), price + bdto.getPrice());
						}
						int total2 = total;
						Map<Member, Double> map2 = new HashMap<>();
						map.forEach((key, value) -> {
							int price = (int) value;
							double chance = price / total2;
							map2.put(key, chance);
						});
						Random random = new Random();
						double randomNumber = random.nextDouble(); // 0부터 1 사이의 랜덤 숫자 생성
						// Stream API를 활용한 랜덤 선택
						Member winner = map2.entrySet().stream().filter(entry -> randomNumber < entry.getValue())
								.map(Map.Entry::getKey) // 당첨자 이름으로 매핑
								.findFirst() // 첫 번째 당첨자 찾기
								.orElse(null); // 찾지 못할 경우 null 반환
						byBuyer.setBuyer(winner);
					}
					auction.setMino(byBuyer.getBuyer());
					seller.setPoint(seller.getPoint() + auction.getMax());
					mservice.edit(MemberDto.create(seller));
					log.debug("byBuyer:{}", byBuyer);
					service.save(auction);
					Set<Object> byName = chatRoomService.findByName(auction.getMino().getId());
					if (byName.isEmpty()) {
						log.debug("byName:{}", byName);
						log.debug("id={}", byBuyer.getBuyer().getId());
						chatRoomService.createChatRoom(String.valueOf(auction.getNum()), byBuyer.getBuyer().getId(),
								seller.getId());
						return;
					}
					for (Object obj : byName) {
						if (obj instanceof ChatRoom) {
							ChatRoom chatRoom = (ChatRoom) obj;
							String chatRoomSeller = chatRoom.getSeller();
							if (!chatRoomSeller.equals(seller)) {
								chatRoomService.createChatRoom(String.valueOf(auction.getNum()),
										byBuyer.getBuyer().getId(), seller.getId());// Get seller from
								// ChatRoom
							}
							System.out.println("ChatRoom Seller: " + chatRoomSeller);
							// Additional processing if needed
						}
					}
				} catch (Exception e) {
					service.save(auction);
				}
			}
		}
	}
}