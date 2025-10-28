package io.github.stu2301681017.MyooosicAPI.integration.deezer;

public record Track(
        String id,
        boolean readable,
        String title,
        String title_short,
        String title_version,
        String link,
        String duration,
        String rank,
        boolean explicit_lyrics,
        int explicit_content_lyrics,
        int explicit_content_cover,
        String preview,
        String md5_image,
        Artist artist,
        Album album,
        String type
) {
}