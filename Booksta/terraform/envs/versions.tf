terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 4.50.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6.3"
    }
  }

  backend "azurerm" {

  }
}

provider "azurerm" {
  features {}
  subscription_id = "ca08a2ca-7c17-4c1e-8d80-d2dc7381aefd"
}
