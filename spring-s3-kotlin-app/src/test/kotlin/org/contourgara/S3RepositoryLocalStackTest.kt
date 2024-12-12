package org.contourgara

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.services.s3.model.S3Exception

@SpringBootTest
@ActiveProfiles("localstack")
class S3RepositoryLocalStackTest(val sut: S3Repository, val s3Client: S3Client) : WordSpec() {
    companion object {
        const val BUCKET_NAME: String = "test-bucket"

        val container = LocalStackContainer(DockerImageName.parse("localstack/localstack:s3-latest"))
            .apply { start() }

        @DynamicPropertySource
        @JvmStatic
        fun awsProperties(registry: DynamicPropertyRegistry) {
            registry.add("aws.access-key-id") { container.accessKey }
            registry.add("aws.secret-key") { container.secretKey }
            registry.add("aws.region") { container.region }
            registry.add("aws.s3.endpoint") { container.endpoint }
        }
    }

    init {
        beforeEach {
            for (bucket in s3Client.listBuckets().buckets().map { it.name() }) {
                for (key in s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucket).build()).contents().map { it.key() }) {
                    s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build())
                }

                s3Client.deleteBucket(DeleteBucketRequest.builder().bucket(bucket).build())
            }

            s3Client.createBucket(CreateBucketRequest.builder().bucket(BUCKET_NAME).build())
        }

        "S3 へのアップロードに成功した場合" should {
            "例外が発生しない" {
                println(s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(BUCKET_NAME).build()))
                shouldNotThrowAny {
                    sut.save(BUCKET_NAME, "test-key")
                }
                println(s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(BUCKET_NAME).build()))
            }
        }

        "S3 へのアップロードに失敗した場合" should {
            "例外が発生する" {
                println(s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(BUCKET_NAME).build()))
                shouldThrow<S3Exception> {
                    sut.save("incorrect-bucket", "test-key")
                }
                println(s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(BUCKET_NAME).build()))
            }
        }
    }
}
