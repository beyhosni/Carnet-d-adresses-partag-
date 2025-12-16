terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "5.10.0"
    }
  }
}

provider "google" {
  project = var.project_id
  region  = var.region
}

# VPC
resource "google_compute_network" "vpc" {
  name                    = "${var.app_name}-vpc"
  auto_create_subnetworks = false
}

resource "google_compute_subnetwork" "subnet" {
  name          = "${var.app_name}-subnet"
  region        = var.region
  network       = google_compute_network.vpc.name
  ip_cidr_range = "10.0.0.0/24"
  secondary_ip_range {
    range_name    = "gke-pods"
    ip_cidr_range = "10.4.0.0/14"
  }
  secondary_ip_range {
    range_name    = "gke-services"
    ip_cidr_range = "10.8.0.0/20"
  }
}

# Artifact Registry
resource "google_artifact_registry_repository" "repo" {
  location      = var.region
  repository_id = "${var.app_name}-repo"
  description   = "Docker repository for Address Book App"
  format        = "DOCKER"
}

# GKE Cluster
resource "google_container_cluster" "primary" {
  name     = "${var.app_name}-gke"
  location = var.region
  
  # We can't create a cluster with no node pool defined, but we want to only use
  # separately managed node pools. So we create the smallest possible default
  # node pool and immediately delete it.
  remove_default_node_pool = true
  initial_node_count       = 1

  network    = google_compute_network.vpc.name
  subnetwork = google_compute_subnetwork.subnet.name

  ip_allocation_policy {
    cluster_secondary_range_name  = "gke-pods"
    services_secondary_range_name = "gke-services"
  }
}

resource "google_container_node_pool" "primary_nodes" {
  name       = "${var.app_name}-node-pool"
  location   = var.region
  cluster    = google_container_cluster.primary.name
  node_count = 1

  node_config {
    preemptible  = true
    machine_type = "e2-medium"

    oauth_scopes = [
      "https://www.googleapis.com/auth/cloud-platform"
    ]
  }
}

# Cloud SQL
resource "google_sql_database_instance" "master" {
  name             = "${var.app_name}-db-instance"
  database_version = "POSTGRES_15"
  region           = var.region
  deletion_protection = false

  settings {
    tier = "db-f1-micro"
    ip_configuration {
      ipv4_enabled = true # Ideally private IP, but for simplicity public with auth
    }
  }
}

resource "google_sql_database" "database" {
  name     = "addressbook"
  instance = google_sql_database_instance.master.name
}

resource "google_sql_user" "users" {
  name     = var.db_user
  instance = google_sql_database_instance.master.name
  password = var.db_password
}
