package io.axoniq.challenge.axoniq_meta_challenge_jg.admin_dashboard

import org.axonframework.queryhandling.annotations.Query

/**
 * Query for retrieving all running challenges - used internally by the admin dashboard
 */
@Query(
    name = "AllRunningChallenges",
    namespace = "axoniq-meta-challenge-jg",
)
class AllRunningChallenges()