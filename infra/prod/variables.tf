variable "analyzeDocument_image_registry" {
  type        = string
  description = "Name of the container registry name"
}

variable "analyzeDocument_image_tag" {
  type        = string
  description = "Name of the image tag "
}

variable "alerting_email" {
  type        = string
  description = "email to receive alerts"
}