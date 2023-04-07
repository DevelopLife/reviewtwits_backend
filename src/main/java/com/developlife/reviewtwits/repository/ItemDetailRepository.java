package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.ItemDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemDetailRepository extends JpaRepository<ItemDetail, Long> {

    @Query("select i from ItemDetail i where i.relatedProduct.name like %:searchKey% or i.detailInfo like %:searchKey% ORDER BY i.itemId DESC")
    List<ItemDetail> findByRelatedProduct_NameLikeOrDetailInfoLike(String searchKey, Pageable pageable);

    List<ItemDetail> findAllByOrderByCreatedDateDesc(Pageable pageable);
}
