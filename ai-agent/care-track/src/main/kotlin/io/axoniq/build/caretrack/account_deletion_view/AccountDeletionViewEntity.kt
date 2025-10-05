package io.axoniq.build.caretrack.account_deletion_view

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

/**
 * JPA Entity for Account Deletion View component
 * Stores account deletion information for both patients and doctors
 */
@Entity
@Table(name = "account_deletion_view")
@EntityListeners(AuditingEntityListener::class)
data class AccountDeletionViewEntity(
    @Id
    val accountId: String,

    @Column(nullable = false)
    val accountType: String,

    @Column(nullable = false, length = 1000)
    val deletionRequirements: String
)

