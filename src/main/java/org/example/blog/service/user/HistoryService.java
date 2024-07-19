package org.example.blog.service.user;

import lombok.RequiredArgsConstructor;
import org.example.blog.domain.post.Post;
import org.example.blog.domain.user.History;
import org.example.blog.domain.user.User;
import org.example.blog.repository.user.HistoryRepository;
import org.example.blog.service.user.userInterface.HistoryInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService implements HistoryInterface {
    private final HistoryRepository historyRepository;

    public List<History> getHistoriesByUserIdByViewDayAsc(Long userId) {
        return historyRepository.findByUserIdOrderByViewDayAsc(userId);
    }

    @Transactional
    public void saveHistory(History history, User user, Post post) {
        if (user != null) {
            if (user.getId() == post.getBlog().getUser().getId() || user.getId() == 0L) {
                return;
            }
            history.setPost(post);
            history.setUser(user);
            History historyByUserIdAndPostPostId = historyRepository.findHistoryByUserIdAndPostPostId(user.getId(), post.getPostId());
            if (historyByUserIdAndPostPostId != null) {
                deleteHistory(historyByUserIdAndPostPostId);
                historyRepository.save(history);
            } else historyRepository.save(history);
        }

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
