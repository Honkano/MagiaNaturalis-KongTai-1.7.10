plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

version = "1.1.0-GTNH"

repositories {
    maven {
        name = "GTNH Maven"
        url = uri("https://nexus.gtnewhorizons.com/repository/public/")
    }
    mavenCentral()
    mavenLocal()
}

tasks.jar {
    archiveClassifier.set("")
}

// ============================================================
// 配置阶段就解析好路径，避免 doLast 闭包捕获 Gradle 对象
// ============================================================
val libsDir: File = layout.buildDirectory.dir("libs").get().asFile
val apiJarOriginalPrefix = "magianaturalis-"
val apiJarNewPrefix = "Magia-Naturalis-"

afterEvaluate {
    tasks.withType<Jar>().configureEach {
        archiveBaseName.set("Magia-Naturalis")
        archiveVersion.set(version.toString())
    }

    val apiJarTask = tasks.findByName("apiJar")
    if (apiJarTask is Jar) {
        apiJarTask.archiveBaseName.set("Magia-Naturalis")
        apiJarTask.archiveVersion.set(version.toString())
    }

    if (tasks.findByName("devJar") == null) {
        val devJar = tasks.register<Jar>("devJar") {
            archiveClassifier.set("dev")
            from(sourceSets.main.get().output)
        }
        tasks.named("build") { dependsOn(devJar) }
    }
}

// ============================================================
// 兜底重命名（闭包只捕获 File 和 String，配置缓存能序列化）
// ============================================================
tasks.named("build") {
    doLast {
        libsDir.listFiles()?.forEach { file ->
            if (file.name.startsWith(apiJarOriginalPrefix) && file.name.endsWith("-api.jar")) {
                val newName = file.name.replaceFirst(apiJarOriginalPrefix, apiJarNewPrefix)
                file.renameTo(File(libsDir, newName))
            }
        }
    }
}