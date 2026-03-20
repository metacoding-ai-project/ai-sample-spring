package com.example.demo.board;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardService boardService;
    private final HttpSession session;

    @GetMapping("/")
    public String list(@RequestParam(defaultValue = "0", name = "page") int page, Model model) {
        var boardList = boardService.게시글목록보기(page);
        model.addAttribute("boardList", boardList);
        model.addAttribute("page", page); // 현재 페이지 번호 전달
        return "board/list";
    }
}
