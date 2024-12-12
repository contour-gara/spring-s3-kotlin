package org.contourgara

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import software.amazon.awssdk.services.s3.model.S3Exception

@SpringBootTest
class S3RepositoryS3Test(val sut: S3Repository) : WordSpec() {
    companion object {
        // type your bucket name
        const val BUCKET_NAME: String = ""

        @DynamicPropertySource
        @JvmStatic
        fun awsProperties(registry: DynamicPropertyRegistry) {
            // type your credentials
            registry.add("aws.access-key-id") { "" }
            registry.add("aws.secret-key") { "" }
            registry.add("aws.region") { "ap-northeast-1" }
        }
    }

    init {
        "S3 へのアップロードに成功した場合" should {
            "例外が発生しない".config(enabled = false) {
                shouldNotThrowAny {
                    sut.save(BUCKET_NAME, "test-key")
                }
            }
        }

        "S3 へのアップロードに失敗した場合" should {
            "例外が発生する".config(enabled = false) {
                shouldThrow<S3Exception> {
                    sut.save("incorrect-bucket", "test-key")
                }
            }
        }
    }
}
