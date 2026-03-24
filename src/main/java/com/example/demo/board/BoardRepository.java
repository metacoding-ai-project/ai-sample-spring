package com.example.demo.board;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Integer> {
    @Modifying
    @Query("delete from Board b where b.user.id = :userId")
    void deleteByUserId(@Param("userId") Integer userId);

    @Query("SELECT b FROM Board b ORDER BY b.id DESC LIMIT :limit OFFSET :offset")
    List<Board> findAll(@Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT b FROM Board b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword% ORDER BY b.id DESC LIMIT :limit OFFSET :offset")
    List<Board> findAllByKeyword(@Param("keyword") String keyword, @Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT COUNT(b) FROM Board b")
    Long countAll();

    @Query("SELECT COUNT(b) FROM Board b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    Long countByKeyword(@Param("keyword") String keyword);
}
