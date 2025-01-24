package com.example.server.service;

import com.example.server.model.Inquiry;
import com.example.server.repository.InquiryRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InquiryService {

    @Autowired
    private InquiryRepository inquiryRepository;

    // 게시글 삭제 및 순서 재조정
    @Transactional
    public void deleteInquiryAndReorder(Long inquiryId) {
        // 해당 게시글 삭제
        inquiryRepository.deleteById(inquiryId);

        // 모든 게시글을 게시판 번호 기준으로 정렬한 후 번호 재설정
        List<Inquiry> inquiries = inquiryRepository.findAllByOrderByBoardOrder();
        for (int i = 0; i < inquiries.size(); i++) {
            inquiries.get(i).setBoardOrder(i + 1); // 게시판 번호 재설정
        }
        // 순서가 변경된 데이터를 다시 저장
        inquiryRepository.saveAll(inquiries);
    }
}