package com.example.demo.board;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

public class BoardResponse {

    @Data
    public static class ListDTO {
        private List<BoardDTO> boards;
        private Integer page;
        private Integer prevPage;
        private Integer nextPage;
        private List<PageNumberDTO> pageNumbers;
        private boolean first;
        private boolean last;
        private Integer totalPage;

        public ListDTO(List<Board> boards, Integer page, Long totalCount, int limit) {
            this.boards = boards.stream().map(BoardDTO::new).toList();
            this.page = page + 1; // 1-기반으로 변환하여 저장
            this.prevPage = page; // page - 1 + 1
            this.nextPage = page + 2; // page + 1 + 1

            // 전체 페이지 수 계산
            this.totalPage = (int) Math.ceil((double) totalCount / limit);
            
            // 첫 페이지, 마지막 페이지 여부
            this.first = (page == 0);
            this.last = (this.page >= totalPage);

            // 5개씩 끊어서 페이지 번호 리스트 생성
            this.pageNumbers = new ArrayList<>();
            int startPage = (page / 5) * 5 + 1;
            int endPage = Math.min(startPage + 4, totalPage);
            for (int i = startPage; i <= endPage; i++) {
                this.pageNumbers.add(new PageNumberDTO(i, this.page == i));
            }
        }

        @Data
        public static class BoardDTO {
            private Integer id;
            private String title;

            public BoardDTO(Board board) {
                this.id = board.getId();
                this.title = board.getTitle();
            }
        }

        @Data
        public static class PageNumberDTO {
            private Integer number;
            private boolean active;

            public PageNumberDTO(Integer number, boolean active) {
                this.number = number;
                this.active = active;
            }
        }
    }

    // RULE: Detail DTO는 상세 정보를 저장한다.
    @Data
    public static class Detail {

    }
}
