data "aws_ecr_repository" "analyzeDocument_registry" {
  name = var.analyzeDocument_image_registry

}

data "aws_ecr_image" "analyzeDocument_image" {
  repository_name = data.aws_ecr_repository.analyzeDocument_registry.name
  image_tag       = var.analyzeDocument_image_tag
}


resource "aws_lambda_function" "analyzeDocument" {
  function_name = "analyzeDocument_lambda"
  role          = aws_iam_role.iam_for_lambda.arn
  package_type  = "Image"
  image_uri     = data.aws_ecr_image.analyzeDocument_image.image_uri
  timeout       = 30
  memory_size   = 512

  logging_config {
    log_format            = "JSON"
    application_log_level = "INFO"
    system_log_level      = "INFO"
    log_group             = aws_cloudwatch_log_group.analyzeDocument.name
  }

  environment {
    variables = {
      "ANALYZED_DOCUMENT_QUEUE_URL" = aws_sqs_queue.analyzed_documents_queue.url
    }
  }
}