variable "project_id" {
  description = "GCP Project ID"
}

variable "region" {
  description = "GCP Region"
  default     = "europe-west1"
}

variable "app_name" {
  description = "Application Name"
  default     = "addressbook"
}

variable "db_user" {
  description = "Database User"
  default     = "postgres"
}

variable "db_password" {
  description = "Database Password"
  sensitive   = true
}
