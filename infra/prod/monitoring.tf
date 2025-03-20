resource "aws_cloudwatch_log_group" "example" {
  name = "example_log_group"

  log_group_class = "STANDARD"

  retention_in_days = 0

  tags = {
    Environment = "production"
    Application = "example"
  }
}

# Metric to catch the errors
resource "aws_cloudwatch_log_metric_filter" "error_filter" {
  name           = "lambda-error-filter"
  log_group_name = aws_cloudwatch_log_group.example.name  # Ensure this matches your Lambda's log group

  pattern = "{ $.level = \"ERROR\" }"  # JSON log format, change if necessary

  metric_transformation {
    name      = "LambdaErrorCount"
    namespace = "LambdaErrors"
    value     = "1"
  }
}

# SNS Topic for notifications
resource "aws_sns_topic" "lambda_error_alert" {
  name = "lambda-error-alerts"
}

# Email notification Subscription
resource "aws_sns_topic_subscription" "email_subscription" {
  topic_arn = aws_sns_topic.lambda_error_alert.arn
  protocol  = "email"
  endpoint  = var.alerting_email # Replace with your email
}
# !!! don't forget to go to the designated mailbox and confirm subscription


# Create alert from the metric to write into the notification topic
resource "aws_cloudwatch_metric_alarm" "lambda_error_alarm" {
  alarm_name          = "lambda-error-alarm"
  comparison_operator = "GreaterThanOrEqualToThreshold"
  evaluation_periods  = 1
  metric_name         = aws_cloudwatch_log_metric_filter.error_filter.metric_transformation[0].name
  namespace           = aws_cloudwatch_log_metric_filter.error_filter.metric_transformation[0].namespace
  period              = 60 # interval watch period
  statistic           = "Sum"
  threshold           = 1
  alarm_description   = "This alarm triggers when an ERROR log appears in the Lambda log group."
  actions_enabled     = true
  alarm_actions       = [aws_sns_topic.lambda_error_alert.arn]
}
