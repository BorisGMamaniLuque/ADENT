package com.adent.publicsite.service;

import com.adent.platform.domain.Tenant;
import com.adent.publicsite.domain.WebsiteRequest;
import com.adent.shared.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Locale;
import java.util.Set;

@Service
public class SiteProjectGeneratorService {
    private final Path templatePath;
    private final Path generatedRoot;
    private static final Set<String> TEXT_EXT = Set.of(".js",".jsx",".ts",".tsx",".json",".html",".css",".md",".txt",".env");

    public SiteProjectGeneratorService(
            @Value("${adent.public-sites.template-path:../public-web/template-base}") String templatePath,
            @Value("${adent.public-sites.generated-root:../public-web/generated-sites}") String generatedRoot) {
        this.templatePath = Paths.get(templatePath).toAbsolutePath().normalize();
        this.generatedRoot = Paths.get(generatedRoot).toAbsolutePath().normalize();
    }

    public String generate(Tenant tenant, WebsiteRequest request) {
        String businessName = request.getBusinessName() == null || request.getBusinessName().isBlank()
                ? tenant.getName()
                : request.getBusinessName();
        return generate(tenant, tenant.getSlug(), businessName);
    }

    public String generate(Tenant tenant, String siteSlug, String businessName) {
        String slug = normalizeSlug(siteSlug == null || siteSlug.isBlank() ? tenant.getSlug() : siteSlug);
        String safeBusinessName = businessName == null || businessName.isBlank() ? tenant.getName() : businessName.trim();
        Path target = generatedRoot.resolve(slug).normalize();

        if (!target.startsWith(generatedRoot)) {
            throw new BusinessException("Ruta de proyecto inválida");
        }

        try {
            if (!Files.isDirectory(templatePath)) {
                throw new BusinessException("No existe template-base en " + templatePath);
            }

            Files.createDirectories(generatedRoot);

            if (!Files.exists(target)) {
                copyTree(templatePath, target);
            }

            replaceTokens(target, slug, safeBusinessName);
            writeProjectMetadata(target, tenant, slug, safeBusinessName);

            return "public-web/generated-sites/" + slug;
        } catch (IOException ex) {
            throw new BusinessException("No se pudo generar el proyecto web: " + ex.getMessage());
        }
    }

    public boolean exists(String siteSlug) {
        String slug = normalizeSlug(siteSlug);
        if (slug.isBlank()) return false;
        Path target = generatedRoot.resolve(slug).normalize();
        return target.startsWith(generatedRoot) && Files.isDirectory(target);
    }

    private void copyTree(Path source, Path target) throws IOException {
        try (var stream = Files.walk(source)) {
            for (Path src : stream.toList()) {
                Path rel = source.relativize(src);
                Path dst = target.resolve(rel);
                if (Files.isDirectory(src)) {
                    Files.createDirectories(dst);
                } else {
                    Files.copy(src, dst, StandardCopyOption.COPY_ATTRIBUTES);
                }
            }
        }
    }

    private void replaceTokens(Path target, String slug, String businessName) throws IOException {
        try (var stream = Files.walk(target)) {
            for (Path file : stream.filter(Files::isRegularFile).toList()) {
                String name = file.getFileName().toString();
                if (!isText(name)) continue;
                String text = Files.readString(file, StandardCharsets.UTF_8);
                text = text.replace("{{SITE_SLUG}}", slug)
                           .replace("{{BUSINESS_NAME}}", businessName);
                Files.writeString(file, text, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            }
        }
    }

    private void writeProjectMetadata(Path target, Tenant tenant, String slug, String businessName) throws IOException {
        String json = """
                {\n  \"tenantId\": \"%s\",\n  \"organizationName\": \"%s\",\n  \"siteSlug\": \"%s\",\n  \"businessName\": \"%s\",\n  \"editable\": true,\n  \"sourceTemplate\": \"template-base\"\n}\n
                """.formatted(
                tenant.getId(),
                escapeJson(tenant.getName()),
                escapeJson(slug),
                escapeJson(businessName));
        Files.writeString(target.resolve("adent-site.json"), json, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        Path env = target.resolve(".env");
        if (!Files.exists(env)) {
            Files.writeString(env, "VITE_API_URL=http://localhost:8080/api\n", StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW);
        }
    }

    private static String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String normalizeSlug(String value) {
        if (value == null) return "";
        return value.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9-]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    private boolean isText(String name) {
        if (name.equals(".env") || name.equals(".env.example")) return true;
        int i = name.lastIndexOf('.');
        return i >= 0 && TEXT_EXT.contains(name.substring(i));
    }
}
