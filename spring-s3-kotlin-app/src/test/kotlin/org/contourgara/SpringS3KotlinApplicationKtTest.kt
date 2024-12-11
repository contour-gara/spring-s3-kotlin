package org.contourgara

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SpringS3KotlinApplicationKtTest(val s3Repository: S3Repository) : StringSpec({
    "contextLoads" {
        s3Repository shouldNotBeNull {}
    }
})
