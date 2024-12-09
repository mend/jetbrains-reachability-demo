package io.mend.reachability.demo.service;

import io.mend.reachability.demo.dto.ResourceVulnerabilityDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.*;

@Slf4j
@Service
public class SourcesService {

  public void downloadSources(ResourceVulnerabilityDto resourceVulnerabilityDto, String outputPath) {
    if (resourceVulnerabilityDto.getComponentType().equals("MAVEN_ARTIFACT")) {
      this.downloadMavenSources(resourceVulnerabilityDto, outputPath);
    }
  }

  private void downloadMavenSources(ResourceVulnerabilityDto resourceVulnerabilityDto, String outputPath) {
    String groupId = resourceVulnerabilityDto.getGroupId();
    String version = resourceVulnerabilityDto.getVersionId();
    String artifactString = (groupId + "." + resourceVulnerabilityDto.getArtifactId()).replace(".", "/");
    String artifactName = String.format("%s-%s-sources.jar", resourceVulnerabilityDto.getArtifactId(), version);

    log.info("Downloading MAVEN sources for {}", artifactName);
    String downloadUrl = String.format("https://repo1.maven.org/maven2/%s/%s/%s", artifactString, version, artifactName);
    File outputFile = this.downloadFromUrlToFile(downloadUrl, outputPath);
    if (outputFile != null) {
      extractJar(outputFile.toString(), outputPath);
    }
  }

  private File downloadFromUrlToFile(String downloadUrl, String outputFolder) {
    log.info("Downloading sources from {} to {}", downloadUrl, outputFolder);
    File outputFile = new File(outputFolder, "sources.jar");
    if (!outputFile.getParentFile().mkdirs()) {
      log.warn("Failed to create output directory {}", outputFolder);
      return null;
    }
    try {
      InputStream inputStream = new URL(downloadUrl).openStream();
      readFromInputStream(inputStream, outputFile);
    } catch (IOException e) {
      log.error(e.getMessage());
      return null;
    }
    log.info("Downloaded successfully to: {}", outputFile);
    return outputFile;
  }

  public void extractJar(String jarFilePath, String outputDir) {
    log.info("Extracting sources from jar {}", jarFilePath);
    File jarFile = new File(jarFilePath);
    if (!jarFile.exists()) {
      log.error("Jar file {} does not exist", jarFilePath);
      return;
    }

    File destDir = new File(outputDir);
    if (!destDir.exists()) {
      destDir.mkdirs();
    }
    try (JarFile jar = new JarFile(jarFile)) {
      Enumeration<JarEntry> entries = jar.entries();
      while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        File entryFile = new File(outputDir, entry.getName());

        if (entry.isDirectory()) {
          // If the entry is a directory, create it
          entryFile.mkdirs();
        } else {
          // If the entry is a file, extract it
          File parent = entryFile.getParentFile();
          if (!parent.exists()) {
            parent.mkdirs();
          }
          readFromInputStream(jar.getInputStream(entry), entryFile);
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    log.info("Finished extracting sources from jar {}", jarFilePath);
  }

  private static void readFromInputStream(InputStream inputStream, File outputFile) {
    try (BufferedInputStream is = new BufferedInputStream(inputStream);
         FileOutputStream fos = new FileOutputStream(outputFile)) {
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = is.read(buffer, 0, 1024)) != -1) {
        fos.write(buffer, 0, bytesRead);
      }
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

}
