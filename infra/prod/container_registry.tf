# manually create the registry yourself : 
data "aws_ecr_repository" "example" {
  name                 = var.example_image_registry

}

data "aws_ecr_image" "service_image" {
  repository_name = data.aws_ecr_repository.example.name
  image_tag       = var.example_image_tag
}
