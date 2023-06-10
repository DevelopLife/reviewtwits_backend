package com.developlife.reviewtwits.repository.statistics;

import com.developlife.reviewtwits.entity.StatInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatInfoRepository extends JpaRepository<StatInfo, Long>, PeriodCheckingRepository {
}
