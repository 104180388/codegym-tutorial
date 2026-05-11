import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlSong {
    public static void main(String[] args) {
        try {
// Đọc nội dung HTML từ file
            String content = new String(Files.readAllBytes(Paths.get("src/song.html")));

// Xóa các ký tự xuống dòng
            content = content.replaceAll("\\n+", "");

// Regex lọc tên bài hát
            Pattern pattern = Pattern.compile("class=\"name_song\"[^>]*>(.*?)</a>");
            Matcher matcher = pattern.matcher(content);

// In danh sách bài hát
            while (matcher.find()) {
                System.out.println(matcher.group(1));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}