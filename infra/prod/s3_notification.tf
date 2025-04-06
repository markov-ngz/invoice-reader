data "aws_s3_bucket" "invoice_reader_bucket" {
  bucket = "invoice-reader-bucket"
}


# data "aws_iam_policy_document" "queue" {
#   statement {
#     effect = "Allow"

#     principals {
#       type        = "*"
#       identifiers = ["*"]
#     }

#     actions   = ["sqs:SendMessage"]
#     resources = ["arn:aws:sqs:*:*:${"s3-new-invoice-queue"}"]

#     condition {
#       test     = "ArnEquals"
#       variable = "aws:SourceArn"
#       values   = [data.aws_s3_bucket.invoice_reader_bucket.arn]
#     }
#   }
# }

# resource "aws_sqs_queue" "s3_new_invoice" {
#   name   = "s3-new-invoice-queue"
#   policy = data.aws_iam_policy_document.queue.json
#   redrive_policy = jsonencode({
#     deadLetterTargetArn = aws_sqs_queue.s3_new_invoice_deadletter.arn
#     maxReceiveCount     = 2
#   })
# }

# resource "aws_sqs_queue" "s3_new_invoice_deadletter" {
#   name = "s3_new_invoice-deadletter-queue"
# }

# resource "aws_sqs_queue_redrive_allow_policy" "s3_new_invoice_redrive_allow_policy" {
#   queue_url = aws_sqs_queue.s3_new_invoice_deadletter.id

#   redrive_allow_policy = jsonencode({
#     redrivePermission = "byQueue",
#     sourceQueueArns   = [aws_sqs_queue.s3_new_invoice.arn]
#   })
# }




# resource "aws_s3_bucket_notification" "bucket_notification" {

#   bucket = data.aws_s3_bucket.invoice_reader_bucket.id

#   queue {
#     queue_arn     = aws_sqs_queue.s3_new_invoice.arn
#     events        = ["s3:ObjectCreated:*"]
#     filter_prefix = "invoices/"
#   }
# }


# resource "aws_lambda_event_source_mapping" "invoice_reader_call" {
#   event_source_arn = aws_sqs_queue.s3_new_invoice.arn
#   function_name    = aws_lambda_function.analyzeDocument.arn
# }