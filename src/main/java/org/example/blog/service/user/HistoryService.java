package org.example.blog.service.user;

import lombok.RequiredArgsConstructor;
import org.example.blog.domain.user.History;
import org.example.blog.repository.user.HistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService{
    private final HistoryRepository historyRepository;

    public List<History> getHistoriesByUserIdByViewDayAsc(Long userId) {
        return historyRepository.findByUserIdOrderByViewDayAsc(userId);
    }

    @Transactional
    public void saveHistory(History history, Long userId, Long postId) {
        if (userId == 0L) return;
        History historyByUserIdAndPostPostId = historyRepository.findHistoryByUserIdAndPostPostId(userId, postId);
        if (historyByUserIdAndPostPostId != null) {
            deleteHistory(historyByUserIdAndPostPostId);
            historyRepository.save(history);
        } else historyRepository.save(history);
    }

    @Transactional
    public void deleteHistory(History history) {
        historyRepository.delete(history);
    }

    @Transactional
    public List<History> getHistoryByUserId(Long userId) {
        return historyRepository.findHistoriesByUserId(userId);
    }
}
