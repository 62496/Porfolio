resource "azurerm_resource_group" "main" {
  name     = "rg-${var.application_name}-${var.environment_name}"
  location = var.primary_location
}

resource "azurerm_static_web_app" "aswa-booksta" {
  name                = "sw-${var.application_name}-${var.environment_name}"
  resource_group_name = azurerm_resource_group.main.name
  location            = azurerm_resource_group.main.location
}

resource "azurerm_service_plan" "main" {
  name                = "wa-${var.application_name}"
  resource_group_name = azurerm_resource_group.main.name
  location            = azurerm_resource_group.main.location
  os_type             = "Linux"
  sku_name            = "P1v2"
}

resource "azurerm_linux_web_app" "awa-booksta" {
  name                = "wa-${var.application_name}-${var.environment_name}"
  resource_group_name = azurerm_resource_group.main.name
  location            = azurerm_service_plan.main.location
  service_plan_id     = azurerm_service_plan.main.id

  site_config {
    application_stack {
      docker_image_name = ""
      docker_registry_url = ""
      docker_registry_username = var.docker_username
      docker_registry_password = var.docker_password
    }
    cors {
      allowed_origins = ["https://${azurerm_static_web_app.aswa-booksta.default_host_name}"]
    }
    websockets_enabled = true
    # ip_restriction {
      
    # }
    # ip_restriction_default_action = "Deny"
    minimum_tls_version = "1.3"
  }
}