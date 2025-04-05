# manually create the registry yourself : 
data "aws_ecr_repository" "invoiceAPI_registry" {
  name = var.invoiceAPI_image_registry

}

data "aws_ecr_image" "invoiceAPI_image" {
  repository_name = data.aws_ecr_repository.invoiceAPI_registry.name
  image_tag       = var.invoiceAPI_image_tag
}

data "aws_instance" "db" {
  instance_id = "i-03469552767fc05d3"
}

data "aws_secretsmanager_secret" "db_password" {
  name = "invoiceAPI/db_password"
}

locals {
  backend_db_conn = {
    db_name     = "template1"
    db_username = "postgres"
    db_password = data.aws_secretsmanager_secret.db_password.arn
    jdbc_url    = "jdbc:postgresql://${data.aws_instance.db.public_ip}:5432/template1"
    // =>  public ip pending the private ip association with vpc & firewall rules 
  }
}
data "aws_iam_role" "apprunner" {
  name = "AWSServiceRoleForAppRunner"
}

resource "aws_apprunner_service" "invoiceAPI" {
  service_name = "invoiceAPI"

  source_configuration {
    image_repository {
      image_configuration {
        port = "8000"
        runtime_environment_variables = {
          DB_URL            = local.backend_db_conn.jdbc_url
          DB_USERNAME       = local.backend_db_conn.db_username
          S3_INVOICE_BUCKET = data.aws_s3_bucket.invoice_reader_bucket.id
          S3_INVOICE_FOLDER = "invoices"
        }
        runtime_environment_secrets = {
          DB_PASSWORD = local.backend_db_conn.db_password
        }
      }
      image_identifier      = data.aws_ecr_image.invoiceAPI_image.image_uri
      image_repository_type = "ECR"
    }
    auto_deployments_enabled = false
  }

  instance_configuration {
    instance_role_arn = data.aws_iam_role.apprunner.arn
  }


  tags = {
    Name = "invoiceAPI"
  }
}