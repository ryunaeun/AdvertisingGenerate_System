package com.example.server.service;

import com.example.server.model.Board;
import com.example.server.model.Notice;
import com.example.server.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 게시글 생성
     * 새로운 게시글에는 연속된 noticeOrder를 자동으로 할당
     */
    @Transactional
    public Notice createNotice(Notice notice) {
        // 가장 최신 noticeOrder 찾기
        List<Notice> notices = noticeRepository.findAllByOrderByNoticeOrderAsc();
        int latestOrder = notices.isEmpty() ? 0 : notices.get(notices.size() - 1).getNoticeOrder();

        // 새로운 게시글의 noticeOrder 설정
        notice.setNoticeOrder(latestOrder + 1);

        // 객체 필드 값 디버깅
        System.out.println("Notice 객체 상태: " + notice);

        // 저장
        return noticeRepository.save(notice);
    }

    /**
     * ID (Primary Key)로 게시글 조회
     */
    public Notice getNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));
    }

    /**
     * 특정 noticeOrder로 게시글 조회
     */
    public Notice getNoticeByNoticeOrder(int noticeOrder) {
        return noticeRepository.findAllByOrderByNoticeOrderAsc()
                .stream()
                .filter(notice -> notice.getNoticeOrder() == noticeOrder)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));
    }

    /**
     * 게시글 삭제 및 noticeOrder 재정렬
     */
    @Transactional
    public void deleteNoticeByNoticeOrder(int noticeOrder) {
        // 게시글 삭제
        noticeRepository.deleteByNoticeOrder(noticeOrder);

        // 삭제 이후, noticeOrder 재정렬
        reorderNoticeOrders();
    }

    /**
     * 전체 게시판의 noticeOrder 재정렬
     */
    @Transactional
    public void reorderNoticeOrders() {
        List<Notice> notices = noticeRepository.findAllByOrderByNoticeOrderAsc();

        // noticeOrder를 1부터 다시 재정렬
        for (int i = 0; i < notices.size(); i++) {
            notices.get(i).setNoticeOrder(i + 1);
        }

        // 변경된 데이터 저장
        noticeRepository.saveAll(notices);
    }

    /**
     * 게시글 페이지네이션 조회
     */
    public Page<Notice> getNotices(Pageable pageable, boolean isDescending) {
        // 정렬 조건 생성
        Sort sort = isDescending ? Sort.by("createdAt").descending() : Sort.by("createdAt").ascending();
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        // 모든 공지사항 조회
        return noticeRepository.findAll(pageable);
    }


}
