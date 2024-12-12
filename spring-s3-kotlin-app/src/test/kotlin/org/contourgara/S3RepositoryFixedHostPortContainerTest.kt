package org.contourgara

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.FixedHostPortGenericContainer
import org.testcontainers.utility.MountableFile
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.S3Exception

@SpringBootTest
@ActiveProfiles("localstack")
class S3RepositoryFixedHostPortContainerTest(val sut: S3Repository, val s3Client: S3Client) : WordSpec() {
    companion object {
        const val BUCKET_NAME: String = "test-bucket"

        val container = FixedHostPortGenericContainer("localstack/localstack:s3-latest")
            .withCopyFileToContainer(MountableFile.forClasspathResource("init.py"), "/etc/localstack/init/ready.d/init.py")
            .withExposedPorts(4566)
            .withFixedExposedPort(14566, 4566)
            .apply { start() }

        @DynamicPropertySource
        @JvmStatic
        fun awsProperties(registry: DynamicPropertyRegistry) {
            registry.add("aws.access-key-id") { "dummy" }
            registry.add("aws.secret-key") { "dummy" }
            registry.add("aws.region") { "ap-northeast-1" }
            registry.add("aws.s3.endpoint") { "http://localhost:14566" }
        }
    }

    init {
        beforeEach {
            container.stop()
            container.start()
        }

        "S3 へのアップロードに成功した場合" should {
            "例外が発生しない" {
                shouldNotThrowAny {
                    sut.save(BUCKET_NAME, "test-key")
                }
            }
        }

        "S3 へのアップロードに失敗した場合" should {
            "例外が発生する" {
                shouldThrow<S3Exception> {
                    sut.save("incorrect-bucket", "test-key")
                }
            }
        }
    }
}
