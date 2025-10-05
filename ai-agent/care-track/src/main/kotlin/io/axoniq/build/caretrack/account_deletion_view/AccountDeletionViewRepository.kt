package io.axoniq.build.caretrack.account_deletion_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository interface for Account Deletion View component
 * Provides data access methods for account deletion information
 */
@Repository
interface AccountDeletionViewRepository : JpaRepository<AccountDeletionViewEntity, String> {

    /**
     * Find account deletion information by account ID
     * @param accountId the account identifier
     * @return AccountDeletionViewEntity if found, null otherwise
     */
    fun findByAccountId(accountId: String): AccountDeletionViewEntity?
}

