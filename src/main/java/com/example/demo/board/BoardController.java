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
    public String list(@RequestParam(defaultValue = "1", name = "page") int page,
                       @RequestParam(defaultValue = "", name = "keyword") String keyword,
                       Model model) {
        if (page < 1) {
            return "redirect:/?page=1&keyword=" + keyword;
        }
        var responseDTO = boardService.게시글목록보기(page - 1, keyword);
        model.addAttribute("model", responseDTO);

        return "board/list";
    }
}
