# manually create the registry yourself : 
data "aws_ecr_repository" "invoiceAPI_registry" {
  name = var.invoiceAPI_image_registry

}

data "aws_ecr_image" "invoiceAPI_image" {
  repository_name = data.aws_ecr_repository.invoiceAPI_registry.name
  image_tag       = var.invoiceAPI_image_tag
}


resource "aws_apprunner_service" "invoiceAPI" {
  service_name = "invoiceAPI"

  source_configuration {
    image_repository {
      image_configuration {
        port = "8000"
      }
      image_identifier      = data.aws_ecr_image.invoiceAPI_image.image_uri
      image_repository_type = "ECR"
    }
    auto_deployments_enabled = false
  }

  tags = {
    Name = "invoiceAPI"
  }
}