package Model;

import java.util.List;

public class Request {
    private String action;       // Ví dụ: "update", "lookup", "drop", "export"
    private String keyword;      // Ví dụ: "HS001"
    private List<String> params; // Ví dụ: ["--grade", "Toán", "9.5"]

    public Request(String action, String keyword, List<String> params) {
        this.action = action;
        this.keyword = keyword;
        this.params = params;
    }

    public String getAction() {
        return action;
    }

    public String getKeyword() {
        return keyword;
    }

    public List<String> getParams() {
        return params;
    }

    // Override toString để debug dễ dàng hơn
    @Override
    public String toString() {
        return "Request{action='" + action + "', keyword='" + keyword + "', params=" + params + "}";
    }
}