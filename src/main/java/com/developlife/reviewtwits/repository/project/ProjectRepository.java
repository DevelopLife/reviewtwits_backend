package com.developlife.reviewtwits.repository.project;

import com.developlife.reviewtwits.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author ghdic
 * @since 2023/03/10
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findProjectsByUser_AccountId(String accountId);

    Optional<Project> findByProjectId(Long projectId);

    Optional<Project> findFirstByUser_AccountId(String accountId);
    Optional<Project> findByProjectName(String projectName);



}
