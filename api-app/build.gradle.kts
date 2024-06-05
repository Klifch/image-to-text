plugins {
	java
	id("org.springframework.boot") version "3.2.6"
	id("io.spring.dependency-management") version "1.1.5"
}

group = "com.imgtotext"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

extra["springCloudGcpVersion"] = "5.3.0"
extra["springCloudVersion"] = "2023.0.1"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")


	implementation("com.google.cloud:spring-cloud-gcp-starter-storage")  // working
//	implementation("org.springframework.cloud:spring-cloud-gcp-dependencies") // not working???
//	implementation("org.springframework.cloud:spring-cloud-gcp-starter-vision") // not working?
	implementation("com.google.cloud:spring-cloud-gcp-dependencies:4.1.3")
	implementation("com.google.cloud:spring-cloud-gcp-starter")
	implementation("com.google.cloud:spring-cloud-gcp-starter-vision")
}

dependencyManagement {
	imports {
		mavenBom("com.google.cloud:spring-cloud-gcp-dependencies:${property("springCloudGcpVersion")}")
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
