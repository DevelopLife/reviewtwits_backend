package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.ItemDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemDetailRepository extends JpaRepository<ItemDetail, Long> {
    @Query("SELECT COUNT(i.itemId) > 0 FROM ItemDetail i")
    boolean checkTableIsNotEmpty();
}
