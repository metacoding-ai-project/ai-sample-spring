package com.example.demo.board;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo._core.handler.ex.Exception404;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * DTO는 Service에서 만든다. Entity를 Controller에 전달하지 않는다.
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardResponse.ListDTO 게시글목록보기(int page, String keyword) {
        int limit = 3; // 한 페이지에 보여줄 개수
        int offset = page * limit; // 시작 인덱스 [0, 3]

        List<Board> boardList;
        Long totalCount;

        if (keyword == null || keyword.isBlank()) {
            boardList = boardRepository.findAll(limit, offset);
            totalCount = boardRepository.countAll();
        } else {
            boardList = boardRepository.findAllByKeyword(keyword, limit, offset);
            totalCount = boardRepository.countByKeyword(keyword);
        }

        if (boardList.isEmpty() && page > 0) {
            throw new Exception404("더 이상 게시글이 없습니다.");
        }

        return new BoardResponse.ListDTO(boardList, page, totalCount, limit, keyword);
    }

}
