package org.example.blog.service.user.userInterface;

import org.example.blog.domain.user.History;

import java.util.List;

public interface HistoryInterface {
    List<History> getHistoryByUserId(Long userId);
}
