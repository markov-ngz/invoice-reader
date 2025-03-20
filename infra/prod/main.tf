terraform {
  required_providers {

    aws = {
      source  = "hashicorp/aws"
      version = "5.91.0"
    }

  }

  backend "s3" {
    bucket = "template-github"
    key    = "terraform/prod/default" # don't forget the "/" at the end or the state will be saved in a file named prod
    region = "eu-west-3"
  }
}

provider "aws" {
  region = "eu-west-3"
}