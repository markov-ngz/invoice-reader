# resource "aws_sqs_queue" "analyzed_documents_queue" {
#   name = "analyzed-documents-queue"
#   redrive_policy = jsonencode({
#     deadLetterTargetArn = aws_sqs_queue.analyzed_documents_deadletter.arn
#     maxReceiveCount     = 5
#   })
# }

# resource "aws_sqs_queue" "analyzed_documents_deadletter" {
#   name = "analyzed_documents-deadletter-queue"
# }

# resource "aws_sqs_queue_redrive_allow_policy" "analyzed_documents_redrive_allow_policy" {
#   queue_url = aws_sqs_queue.analyzed_documents_deadletter.id

#   redrive_allow_policy = jsonencode({
#     redrivePermission = "byQueue",
#     sourceQueueArns   = [aws_sqs_queue.analyzed_documents_queue.arn]
#   })
# }