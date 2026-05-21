import java.util.Map;

class StudentRequest {
    private String action; // lookup, define, drop
    private String keyword; // Dùng cho lookup hoặc drop (thường là studentId)
    private Map<String, String> params; // Dùng cho define (name, class...)

    public StudentRequest(String action, String keyword, Map<String, String> params) {
        this.action = action;
        this.keyword = keyword;
        this.params = params;
    }

    public String getAction() { return action; }
    public String getKeyword() { return keyword; }
    public Map<String, String> getParams() { return params; }
}