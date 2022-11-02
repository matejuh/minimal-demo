import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.0-RC1"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.7.20"
	kotlin("plugin.spring") version "1.7.20"
	id("nu.studer.jooq") version "7.1.1"
	id("org.liquibase.gradle") version "2.1.1"
}

group = "com.matejuh"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17
val jooqVersion = "3.17.4"

extra["testcontainersVersion"] = "1.17.5"

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
	implementation("org.springframework:spring-webflux")
	implementation("org.springframework:spring-context")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("com.zaxxer:HikariCP")
	implementation("io.projectreactor.netty:reactor-netty")
	implementation("ch.qos.logback:logback-classic")

	implementation("io.micrometer:micrometer-core")
	implementation("io.micrometer:micrometer-registry-jmx:1.9.5")

	/* DB */
	implementation("org.liquibase:liquibase-core")
	implementation("org.postgresql:postgresql")
	implementation("org.jooq:jooq:$jooqVersion")
	implementation("org.jooq:jooq-kotlin:$jooqVersion")
	implementation("org.testcontainers:postgresql")

	/* JOOQ generator */
	jooqGenerator("org.postgresql:postgresql")
	liquibaseRuntime("org.liquibase:liquibase-core")
	liquibaseRuntime("org.postgresql:postgresql")
	liquibaseRuntime("ch.qos.logback:logback-classic")
	/* picocli needed for liquibase which doesn't include it as a transitive dependency :| */
	liquibaseRuntime("info.picocli:picocli:4.6.3")

	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
	testImplementation("org.junit.platform:junit-platform-launcher:1.9.1")
	testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.2")
	testImplementation("io.rest-assured:kotlin-extensions:5.2.0")
	testImplementation("net.javacrumbs.json-unit:json-unit:2.36.0")
	testImplementation("org.awaitility:awaitility-kotlin:4.2.0")
}

dependencyManagement {
	imports {
		mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
		mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val dbUrl = "jdbc:postgresql://localhost/postgres"
val dbUsername = "postgres"
val dbPassword = "secret"

liquibase {
	activities.register("main") {
		this.arguments = mapOf(
			"logLevel" to "info",
			"changeLogFile" to "src/main/resources/db.changelog/db.changelog-master.sql",
			"url" to dbUrl,
			"username" to dbUsername,
			"password" to dbPassword
		)
	}
	runList = "main"
}

jooq {
	version.set(jooqVersion)
	edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)

	configurations.create("main") {
		generateSchemaSourceOnCompilation.set(false)
		jooqConfiguration.apply {
			logging = org.jooq.meta.jaxb.Logging.WARN
			jdbc.apply {
				driver = "org.postgresql.Driver"
				url = dbUrl
				user = dbUsername
				password = dbPassword
			}
			generator.apply {
				name = "org.jooq.codegen.KotlinGenerator"
				database.apply {
					name = "org.jooq.meta.postgres.PostgresDatabase"
					inputSchema = "public"
					// We do not want to generate classes for Liquibase tables
					excludes = "databasechangelog|databasechangeloglock"
				}
				generate.apply {
					isRecords = false
				}
				target.apply {
					packageName = "com.matejuh.demo.generated"
					directory = "./src/generated/jooq"
				}
				strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
			}
		}
	}
}

val postgresImage = "postgres:12.4-alpine"

val startJooqDb by tasks.registering {
	description = "Starts a Postgres instance in docker for the JOOQ generator"
	group = "jooq"

	doFirst {
		// run fresh Postgres for jooq generator
		exec {
			runPostgres(containerName = "jooq-postgres")
		}
		// give it some time to start accepting connections
		Thread.sleep(2_000)
	}
}

val stopJooqDb by tasks.registering {
	description = "Tries to stop the Postgres docker instance for the JOOQ generator"
	group = "jooq"

	doLast {
		exec {
			commandLine("docker", "stop", "jooq-postgres")
		}
	}
}

tasks.update {
	dependsOn(startJooqDb)
}

tasks.named("generateJooq") {
	dependsOn(tasks.update)
	finalizedBy(stopJooqDb)
}

fun ExecSpec.runPostgres(port: Int = 5432, containerName: String) {
	commandLine(
		"docker",
		"run",
		"--rm",
		"-d",
		"-e",
		"POSTGRES_PASSWORD=$dbPassword",
		"-p",
		"$port:5432",
		"--name",
		containerName,
		postgresImage
	)
}
