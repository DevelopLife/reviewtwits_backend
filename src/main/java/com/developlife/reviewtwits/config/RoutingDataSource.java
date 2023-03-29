package com.developlife.reviewtwits.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author ghdic
 * @since 2023/03/29
 */
public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

        System.out.println("Transaction의 Read Only가 " + isReadOnly + " 입니다.");

        if (isReadOnly) {
            System.out.println("Replica 서버로 요청합니다.");
            return "replica";
        }

        System.out.println("Source 서버로 요청합니다.");
        return "source";
    }
}
