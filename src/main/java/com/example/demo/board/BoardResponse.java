package com.example.demo.board;

import lombok.Data;

public class BoardResponse {

    @Data
    public static class ListDTO {
        private Integer id;
        private String title;

        public ListDTO(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
        }
    }

    // RULE: Detail DTO는 상세 정보를 저장한다.
    @Data
    public static class Detail {

    }
}
