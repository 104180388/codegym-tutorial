import java.util.*;
import java.util.function.Consumer;


class Request {
    String action;
    String keyword;
    Map<String, String> params;

    public Request(String action, String keyword, Map<String, String> params) {
        this.action = action;
        this.keyword = keyword;
        this.params = params;
    }
}

public class StudentController {
    Scanner scanner = new Scanner(System.in);
    private final StudentService service = StudentService.getInstance();
    private final Map<String, Consumer<Request>> commands = new HashMap<>();

    public StudentController() {
        commands.put("define", req -> {
            service.define(req.params.get("name"), req.params.get("dateOfBirth"), req.params.get("gender"), req.params.get("phoneNumber"), req.params.get("className"));
        });

        commands.put("lookupName", req -> printResults(service.lookup("name", req.keyword)));

        commands.put("drop", req -> {
            if (!service.drop(req.keyword)) {
                System.out.println("no info");
            } else {
                System.out.print("Bạn có chắc muốn xóa sinh viên này không? (Yes/No): ");
                String confirm = scanner.nextLine().trim();

                if (confirm.equalsIgnoreCase("Yes") || confirm.equalsIgnoreCase("Y")) {
                    System.out.println("Đã xóa học sinh " + req.keyword);
                } else {
                    System.out.println("Đã hủy thao tác xóa sinh viên.");
                }
            }
        });

        commands.put("displayAll", req -> {
            service.displayAll();
        });

        commands.put("export", req -> {
            String filename = req.params.get("filename");
            service.exportToFile(filename);
        });
    }

    private void printResults(List<Entity.Student> results) {
        if (results.isEmpty()) System.out.println("no info");
        else results.forEach(System.out::println);
    }

    public void execute(Request req) {
        commands.getOrDefault(req.action, r -> System.out.println("Action invalid")).accept(req);
    }


}