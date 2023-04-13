package com.developlife.reviewtwits.repository.file;

import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-04-12
 */
public interface FileCustomRepository {
    List<String> getRealFilename(Long referenceId, String referenceType);
}