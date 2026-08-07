

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    public DepartmentController(
            DepartmentRepository departmentRepository) {

        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getDepartments() {

        List<DepartmentResponse> departments =
                departmentRepository.findAll()
                        .stream()
                        .map(department ->
                                new DepartmentResponse(
                                        department.getId(),
                                        department.getName()
                                )
                        )
                        .toList();

        return ResponseEntity.ok(departments);
    }
}