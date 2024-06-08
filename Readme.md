# My Image to Text Project

This repository contains two Spring Boot applications (`web-app` and `api-app`) managed in a monorepository. These applications demonstrate integration with Google Cloud services to upload images to a Cloud Storage bucket, process the images using Google Vision API, and handle the results. All of it happens in 2 nodes inside cluster managed by Google Kubernetes Engine.

## Project Overview

***Information:***
- Project created by Oleksii Demydenko
- Student of group ACS201
- Email: oleksii.demydenko@student.kdg.be
- Student ID: 0159614-49

***Technologies:***
- [Spring](https://spring.io/) - main framework
- [AJAX](https://developer.mozilla.org/en-US/docs/Glossary/AJAX) - responsive ui
- [Bootstrap](https://getbootstrap.com/) - framewaork for modern ui
- [Spring&GCloud](https://googlecloudplatform.github.io/spring-cloud-gcp/4.1.3/reference/html/index.html#getting-started) - Spring integration with Google Cloud
- [Google Vision](https://cloud.google.com/vision/docs) - detection of text from image
- [Google Cloud Storage](https://cloud.google.com/storage/docs) - users image storage
- [Google Cloud Artifact Registry](https://cloud.google.com/artifact-registry/docs) - custom registry where builded application images are stored
- [Kubernetes](https://kubernetes.io/docs/home/) - automated container management
- [Google Kubernetes Engine](https://cloud.google.com/kubernetes-engine/docs) - implementation of the Kubernetes platform in cloud
- [Docker](https://docs.docker.com/) - just a cool guy who made it all possible


### web-app

The `web-app` is a Spring Boot application with a front-end interface that allows users to upload images. The uploaded images are saved to a Google Cloud Storage bucket, and a **signed** link to the uploaded image is displayed, link works only 15 minutes. The application then triggers a chain of requests that leads to the `api-app` to process the image. The access to the web application is managed by **LoadBalancer** service, that exposes it to the web. **Cluster contains 2 replicas of the app - 1 pod in each node**.

### api-app

The `api-app` is a backend Spring Boot application that handles API requests to process images. It retrieves the images from the Google Cloud Storage bucket by received name and bucket and sends them to the Google Vision API to extract text from the images. The extracted text is then returned to the `web-app`. Communication is managed by **ClusterIP** service, so this app could only be reached from inside the cluster. Leverging the features of kubernetes, requests are made with env variables, so application will perfectly run after even restart of the services. **Cluster contains 2 replicas of the app - 1 pod in each node**.

## Features

- **Image Upload**: Users can upload images through the `web-app`.
- **Google Cloud Storage**: Uploaded images are stored in a Google Cloud Storage bucket.
- **Google Vision API**: Images are processed to extract text using the Vision API.
- **Internal Communication**: The `web-app` and `api-app` communicate within the Kubernetes cluster.

## Prerequisites

- Java 17
- Docker
- Gradle
- Google Cloud SDK
- GitLab CI/CD configured with Google Cloud integration
- Google Cloud Project with enabled APIs:
  - Google Cloud Storage
  - Google Vision API
  - Google Kubernetes Engine

> ___Important:___
> I think that it's crucial to mention, that all steps below and information above do not include millions of tries and failures.
> Also, tonn of setups and intermediary commands that took multiple hours to figure out how to use.
> Bright example: Who knew that ***rules*** in CI does not work with ***only***, or that rules should be same for all jobs that connected with ***needs*** keyword?

## Setup

### Local Development

1. **Clone the repository:**

   ```bash
   git clone https://gitlab.com/kdg-ti/infrastructure-3/2023-24/acs-201/demydenko-oleksii
   cd demydenko-oleksii
   ```
2. **Build the applications:**

   ```bash
   cd web-app
   ./gradlew clean bootJar
   cd ../api-app
   ./gradlew clean bootJar
   ```
2. **Run the applications:**
   >You can run each application locally using:

   ```bash
   cd web-app
   ./gradlew bootRun
   cd ../api-app
   ./gradlew bootRun
   ```
   
### Docker

1. **Build Docker images:**

   ```bash
   cd web-app
   docker build -t web-app:latest .
   cd ../api-app
   docker build -t api-app:latest .
   ```
   
2. **Run Docker containers:**

   ```bash
   docker run -p 8080:8080 web-app:latest
   docker run -p 8081:8081 api-app:latest
   ```
   
## Google Cloud Integration

### Image Upload and Processing Flow
1. Image Upload: The web-app allows users to upload images.
2. Save to Cloud Storage: The uploaded image is saved to a Google Cloud Storage bucket.
3. Trigger Processing: The web-app triggers a request to the api-app to process the image.
4. Retrieve Image: The api-app retrieves the image from the Cloud Storage bucket.
5. Vision API: The api-app sends the image to the Google Vision API to extract text.
6. Return Results: The extracted text is returned to the web-app and displayed to the user.

>  __Google Cloud Credentials__
> The applications use a service account for authentication with Google Cloud services. Mind configuring service account.
> Also, separate service account with different roles for developers and guests used for CI pipline.

## Working with Kubernetes Cluster in cloud

### Creating a Cluster

1. **To create a new GKE Cluster:**

   ```bash
   gcloud container clusters create infra3-cluster --num-nodes=2 --zone=europe-west1-d
   ```
2. **Accessing the Cluster:**

   ```bash
   gcloud container clusters get-credentials infra3-cluster --zone=europe-west1-d
   ```

### Updating Deployments
After making changes to your code, follow these steps to update the deployments:

1. **Build new Docker images:**
   ```bash
   cd web-app
   docker build -t europe-west1-docker.pkg.dev/$PROJECT_ID/$REPO_NAME/app1:$IMAGE_TAG .
   docker push europe-west1-docker.pkg.dev/$PROJECT_ID/$REPO_NAME/app1:$IMAGE_TAG

   cd ../api-app
   docker build -t europe-west1-docker.pkg.dev/$PROJECT_ID/$REPO_NAME/app2:$IMAGE_TAG .
   docker push europe-west1-docker.pkg.dev/$PROJECT_ID/$REPO_NAME/app2:$IMAGE_TAG
   ```

### Deploying Applications Changes

1. **Deploy web-app:**

   ```bash
   kubectl apply -f web-app/app1-deployment.yaml
   kubectl apply -f web-app/app1-service.yaml
   
   kubectl apply -f api-app/app2-deployment.yaml
   kubectl apply -f api-app/app2-service.yaml
   ```
   
### Scaling the Deployments

1. **To scale the number of replicas for web-app:**

   ```bash
   kubectl scale deployment app1-deployment --replicas=3
   ```
   
2. **To scale the number of replicas for api-app:**

   ```bash
   kubectl scale deployment app1-deployment --replicas=3
   ```


# CI/CD Pipeline Explanation

This part provides a detailed explanation of the CI/CD pipeline defined in the `.gitlab-ci.yml` file. The pipeline consists of several stages to build, package, and deploy two Spring Boot applications (`web-app` and `api-app`) using GitLab CI/CD and Google Cloud services.

## Stages

The pipeline includes the following stages:
- `build`: Compiles and builds the JAR files.
- `package`: Builds Docker images and pushes them to Google Artifact Registry.
- `deploy`: Deploys the applications to Google Kubernetes Engine (GKE).

## Variables

The pipeline uses several variables to configure the build and deployment process:
- `GRADLE_OPTS`: Disables the Gradle daemon.
- `DOCKER_DRIVER`: Specifies the Docker storage driver.
- `DOCKER_TLS_CERTDIR`: Disables Docker TLS certification directory.

## Jobs

### build_app1

- **Stage**: `build`
- **Image**: `eclipse-temurin:17.0.3_7-jdk-jammy`
- **Script**: Navigates to the `web-app` directory and builds the JAR file using Gradle.
- **Artifacts**: Stores the built JAR file.
- **Rules**: Runs this job only on the `main` branch if there are changes in the `web-app` directory.

### build_app2

- **Stage**: `build`
- **Image**: `eclipse-temurin:17.0.3_7-jdk-jammy`
- **Script**: Navigates to the `api-app` directory and builds the JAR file using Gradle.
- **Artifacts**: Stores the built JAR file.
- **Rules**: Runs this job only on the `main` branch if there are changes in the `api-app` directory.

### package_and_push_app1

- **Stage**: `package`
- **Image**: `gcr.io/google.com/cloudsdktool/google-cloud-cli:466.0.0-alpine`
- **Needs**: Depends on `build_app1` job.
- **Services**: Uses Docker-in-Docker service.
- **Variables**: Configures Docker to communicate with the Docker service.
- **Identity**: Uses Google Cloud identity for authentication.
- **Before Script**:
  - Logs in to the Docker registry.
  - Configures Docker for Google Artifact Registry.
- **Script**:
  - Navigates to the `web-app` directory.
  - Builds and pushes the Docker image to Google Artifact Registry.
- **Rules**: Runs this job only on the `main` branch if there are changes in the `web-app` directory.

### package_and_push_app2

- **Stage**: `package`
- **Image**: `gcr.io/google.com/cloudsdktool/google-cloud-cli:466.0.0-alpine`
- **Needs**: Depends on `build_app2` job.
- **Services**: Uses Docker-in-Docker service.
- **Variables**: Configures Docker to communicate with the Docker service.
- **Identity**: Uses Google Cloud identity for authentication.
- **Before Script**:
  - Logs in to the Docker registry.
  - Configures Docker for Google Artifact Registry.
- **Script**:
  - Navigates to the `api-app` directory.
  - Builds and pushes the Docker image to Google Artifact Registry.
- **Rules**: Runs this job only on the `main` branch if there are changes in the `api-app` directory.

### deploy_app1

- **Stage**: `deploy`
- **Image**: `google/cloud-sdk`
- **Needs**: Depends on `package_and_push_app1` job.
- **Identity**: Uses Google Cloud identity for authentication.
- **Script**:
  - Retrieves GKE cluster credentials.
  - Deletes the existing `web-app` deployment.
  - Applies the new `web-app` deployment configuration.
- **Rules**: Runs this job only on the `main` branch if there are changes in the `web-app` directory.

### deploy_app2

- **Stage**: `deploy`
- **Image**: `google/cloud-sdk`
- **Needs**: Depends on `package_and_push_app2` job.
- **Identity**: Uses Google Cloud identity for authentication.
- **Script**:
  - Retrieves GKE cluster credentials.
  - Deletes the existing `api-app` deployment.
  - Applies the new `api-app` deployment configuration.
- **Rules**: Runs this job only on the `main` branch if there are changes in the `api-app` directory.

### Contributing
Feel free to contribute to this project by opening issues or submitting merge requests.
