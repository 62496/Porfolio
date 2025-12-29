variable "application_name" {
    type = string
}

variable "environment_name" {
    type = string
}

variable "primary_location" {
    type = string
}

variable "docker_username" {
    type = string
    sensitive = true
}

variable "docker_password" {
    type = string
    sensitive = true
}