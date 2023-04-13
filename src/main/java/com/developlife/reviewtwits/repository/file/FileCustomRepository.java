package com.developlife.reviewtwits.repository.file;

import com.developlife.reviewtwits.type.ReferenceType;

import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-04-12
 */
public interface FileCustomRepository {
    List<String> getRealFilename(Long referenceId, ReferenceType referenceType);
}