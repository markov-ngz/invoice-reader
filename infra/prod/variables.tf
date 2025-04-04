
variable "alerting_email" {
  type        = string
  description = "email to receive alerts"
}

variable "analyzeDocument_image_registry" {
  type        = string
  description = "Name of the container registry name"
}

variable "analyzeDocument_image_tag" {
  type        = string
  description = "Name of the image tag "
}

variable "loadAnalyzedDocument_image_registry" {
  type        = string
  description = "Name of the container registry name"
}

variable "loadAnalyzedDocument_image_tag" {
  type        = string
  description = "Name of the image tag "
}