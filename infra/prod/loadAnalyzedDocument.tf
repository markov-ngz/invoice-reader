# # manually create the registry yourself : 
# data "aws_ecr_repository" "loadAnalyzedDocument_registry" {
#   name = var.loadAnalyzedDocument_image_registry

# }

# data "aws_ecr_image" "loadAnalyzedDocument_image" {
#   repository_name = data.aws_ecr_repository.loadAnalyzedDocument_registry.name
#   image_tag       = var.loadAnalyzedDocument_image_tag
# }

# resource "aws_lambda_function" "loadAnalyzedDocument" {
#   function_name = "loadAnalyzedDocument_lambda"
#   role          = aws_iam_role.iam_for_lambda.arn
#   package_type  = "Image"
#   image_uri     = data.aws_ecr_image.loadAnalyzedDocument_image.image_uri
#   timeout       = 30
#   memory_size   = 512

#   logging_config {
#     log_format            = "JSON"
#     application_log_level = "INFO"
#     system_log_level      = "INFO"
#     log_group             = aws_cloudwatch_log_group.loadAnalyzedDocument.name
#   }

# }

# resource "aws_lambda_function_url" "analyzeDocument_url" {
#   function_name = aws_lambda_function.loadAnalyzedDocument.function_name

#   authorization_type = "NONE"
# }

# resource "aws_cloudwatch_log_group" "loadAnalyzedDocument" {
#   name = "loadAnalyzedDocument_log_group"

#   log_group_class = "STANDARD"

#   retention_in_days = 0

#   tags = {
#     Environment = "production"
#     Application = "loadAnalyzedDocument"
#   }
# }