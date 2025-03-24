resource "aws_sqs_queue" "analyzed_documents_queue" {
  name = "analyzed-documents-queue"
}

# resource "aws_lambda_event_source_mapping" "analyzed_documents_queue_mapping" {
#   event_source_arn = aws_sqs_queue.analyzed_documents_queue.arn
#   function_name    = aws_lambda_function.invoiceReader.arn
# }