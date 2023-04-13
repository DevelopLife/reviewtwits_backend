package com.developlife.reviewtwits.repository.file;

import com.developlife.reviewtwits.type.ReferenceType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.developlife.reviewtwits.entity.QFileInfo.fileInfo;
import static com.developlife.reviewtwits.entity.QFileManager.fileManager;

/**
 * @author WhalesBob
 * @since 2023-04-12
 */

@RequiredArgsConstructor
public class FileCustomRepositoryImpl implements FileCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    //SELECT fs.real_filename FROM file_storage fs
    //    // INNER JOIN file_manager fm ON fs.file_storage_id = fm.file_storage_id WHERE fm.reference_id = ?1
    //    // AND fm.reference_type LIKE ?2 AND fs.exist = true

    @Override
    public List<String> getRealFilename(Long referenceId, ReferenceType referenceType) {
        return jpaQueryFactory.select(fileInfo.realFilename).from(fileInfo)
                .innerJoin(fileManager)
                .on(fileInfo.eq(fileManager.fileInfo))
                .where(
                        fileManager.referenceId.eq(referenceId)
                                .and(fileManager.referenceType.eq(referenceType))
                                .and(fileInfo.exist.isTrue())
                )
                .fetch();
    }
}