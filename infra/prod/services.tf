
resource "aws_lambda_function_url" "analyzeDocument_url" {
  function_name = aws_lambda_function.analyzeDocument.function_name

  authorization_type = "NONE"
}

resource "aws_lambda_function" "analyzeDocument" {
  function_name = "analyzeDocument_lambda"
  role          = aws_iam_role.iam_for_lambda.arn
  package_type  = "Image"
  image_uri     = data.aws_ecr_image.analyzeDocument_image.image_uri
  timeout       = 30
  memory_size = 512

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

data "aws_iam_policy_document" "assume_role" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }

    actions = [
      "sts:AssumeRole",
    ]
  }
}

resource "aws_iam_role" "iam_for_lambda" {
  name               = "iam_for_lambda"
  assume_role_policy = data.aws_iam_policy_document.assume_role.json
}

# necessary to add the loging stream permissions or the log can't be retrieved
resource "aws_iam_role_policy_attachment" "lambda_basic_execution" {
  role       = aws_iam_role.iam_for_lambda.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_iam_role_policy_attachment" "sqs_basic_execution" {
  role       = aws_iam_role.iam_for_lambda.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaSQSQueueExecutionRole"
}

resource "aws_iam_role_policy_attachment" "s3_basic_execution" {
  role       = aws_iam_role.iam_for_lambda.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess"
}

resource "aws_iam_policy" "secretsmanager_get_secret_value" {
  name        = "SecretsManagerGetSecretValuePolicy"
  description = "Policy to allow GetSecretValue from Secrets Manager"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "secretsmanager:GetSecretValue",
          "secretsmanager:GetSecretValue"
        ],
        Resource = "*",
      },
    ]
  })
}

resource "aws_iam_role_policy_attachment" "secretsmanager_get_secret_value_attachment" {
  role       = aws_iam_role.iam_for_lambda.name
  policy_arn = aws_iam_policy.secretsmanager_get_secret_value.arn
}


resource "aws_iam_policy" "textract_analyze_document" {
  name        = "TextractAnalyzeDocumentPolicy"
  description = "Policy to allow Textract AnalyzeDocument actions"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = [
          "textract:AnalyzeDocument",
        ],
        Effect   = "Allow",
        Resource = "*",
      },
    ]
  })
}

resource "aws_iam_role_policy_attachment" "textract_analyze_document_attachment" {
  role       = aws_iam_role.iam_for_lambda.name
  policy_arn = aws_iam_policy.textract_analyze_document.arn
}


# Add SQS SendMessage permissions
resource "aws_iam_policy" "sqs_send_message" {
  name        = "SQSSendMessagePolicy"
  description = "Policy to allow SQS SendMessage actions"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = [
          "sqs:SendMessage",
        ],
        Effect   = "Allow",
        Resource = aws_sqs_queue.analyzed_documents_queue.arn, # Replace with the actual ARN of your SQS queue
      },
    ]
  })
}

resource "aws_iam_role_policy_attachment" "sqs_send_message_attachment" {
  role       = aws_iam_role.iam_for_lambda.name
  policy_arn = aws_iam_policy.sqs_send_message.arn
}