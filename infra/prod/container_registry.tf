# manually create the registry yourself : 
data "aws_ecr_repository" "analyzeDocument_registry" {
  name = var.analyzeDocument_image_registry

}

data "aws_ecr_image" "analyzeDocument_image" {
  repository_name = data.aws_ecr_repository.analyzeDocument_registry.name
  image_tag       = var.analyzeDocument_image_tag
}
