plugins {
    id("java")
    id("application")
}

application {
    mainClass.set("com.xtaing.core.Main")
}

group = "com.xtiang"
version = "1.0-SNAPSHOT"

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.google.code.gson:gson:2.13.1")

    // 视频背景解码：javacv 提供 Java API，ffmpeg-platform 携带各平台 native
    // JavaCPP 的 Loader 按运行时 OS 与架构自动加载匹配的 native，无需固定 classifier
    implementation("org.bytedeco:javacv:1.5.14")
    implementation("org.bytedeco:ffmpeg-platform:8.1.2-1.5.14")

    // 纯 Java 解码兜底：native 不可用的平台自动降级至此，无任何平台依赖
    implementation("org.jcodec:jcodec:0.2.5")
    implementation("org.jcodec:jcodec-javase:0.2.5")
}

tasks.test {
    useJUnitPlatform()
}