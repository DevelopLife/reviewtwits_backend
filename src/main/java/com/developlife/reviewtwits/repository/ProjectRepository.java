package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ghdic
 * @since 2023/03/10
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}
