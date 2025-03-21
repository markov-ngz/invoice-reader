# manually create the registry yourself : 
data "aws_ecr_repository" "invoiceReader_registry" {
  name                 = var.invoiceReader_image_registry

}

data "aws_ecr_image" "invoiceReader_image" {
  repository_name = data.aws_ecr_repository.invoiceReader_registry.name
  image_tag       = var.invoiceReader_image_tag
}
