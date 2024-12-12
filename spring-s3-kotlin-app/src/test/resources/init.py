import boto3

s3_client = boto3.client(
    "s3",
    endpoint_url=f"http://localhost:4566",
)

s3_client.create_bucket(Bucket="test-bucket")
