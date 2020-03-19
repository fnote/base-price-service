# API Blueprint
This project serves as an API Blueprint to initiate a spring boot based API development with all the commonly required boilerplate included.
We will use the API Blueprint to derive a template so that we can use a maven archetype like scaffolding tool available for gradle.
https://github.com/orctom/gradle-archetype-plugin

# What You Will Get

* Layered architecture with controller,service and repository layers
* JPA based ORM,JPA interceptor to capture cross cutting data requirements such as createdBy,createdAt
* Database connection pooling
* Spring security, authentication and authorization 
* High focus in enabling sustainable and scalable Test Driven Development in all layers 
* Standard validations and error handling
* Logging
* Automated Swagger documentation 
* Automated database management using liquibase
* Secure coding practices
 
# How to run
* Create a database named `blueprint` in mysql or postgre
* Set the below datasource properties as appropriate.(mysql)
```
#comment out if postgre
jdbcUrl=jdbc:mysql://localhost:3306/blueprint?characterEncoding=utf8&useSSL=false
#Uncomment if postgre
#jdbcUrl=jdbc:postgresql://localhost:5432/users?characterEncoding=utf8&useSSL=false
username=root
password=password
```
* Run unit tests `gradle test`
* Ensure all the tests run 
* The bluepriint comes with support for both mysql and postgre. Change the database connection parameters as appropriate.
* eg: postgre datasource.properties
```dtd

jdbcUrl=jdbc:postgresql://localhost:5432/users?characterEncoding=utf8&useSSL=false
username=root
password=password
```
# Main Features built into the API Blueprint

# Layered Architecture

![Image description](./api-achi.png)

## Controller

* Handles the http requests and responses.
* Uses DTO(data transfer objects) to represent the requests/responses.
* Handles the url mapping and API versioning
* Handles exceptions 
* Handles validations of requests
* Swagger documentation
* eg:
```dtd
@RestController
@RequestMapping("/api-blueprint")
@Slf4j
class BookController {
    Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;
    @GetMapping("/v1/books/{id}")
    public @ResponseBody
    Optional<Book> get(@PathVariable("id") Long id) {
        return bookService.findById(id);
    }

    @PostMapping("v1/book")
    @ApiOperation(value = "Creates a new book", notes = "Returns the newly created Book with its auto assigned Id", code = 201, response = Book.class)
  
```
### Testing the Controller Layer
* Uses mock based testing
* @WebMvcTest and @MockBean tag
* Required to mock the services layer the controller depends on.
* Uses @WithMockUser for authentication/authorization
* eg:
```dtd
@WebMvcTest(BookController.class)
@EnableAutoConfiguration
class BookControllerTest {

    private final String API_PATH = "/api-blueprint/v1";
    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;
    @Autowired
    private BookController bookController;
   @Test
    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    public void whenSavingUpdatedBook_returnsUpdatedBook()
            throws Exception {
        BookDTO bookDTO = new BookDTO("Life in Jail", "ISBN 978-0-596-52068-7", "Rohana Kumara");
        String bookJson = new ObjectMapper().writeValueAsString(bookDTO);
        Book bookToBeSSaved = new Book(bookDTO.getName(), bookDTO.getIsbn(), bookDTO.getAuthor());
        Book bookSaved = new Book(bookDTO.getName(), bookDTO.getIsbn(), bookDTO.getAuthor());
        bookSaved.setId(100L);
        //mock the expected response from the service
        when(bookService.save(bookToBeSSaved)).thenReturn(bookSaved);
        mvc.perform(MockMvcRequestBuilders.post(API_PATH + "/book")
                .content(bookJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
        bookSaved.setName("Life in a Jail");
        String savedBookJson = new ObjectMapper().writeValueAsString(bookSaved);
        //mock the expected response from the service
        when(bookService.save(bookSaved)).thenReturn(bookSaved);
        mvc.perform(MockMvcRequestBuilders.put(API_PATH + "/book")
                .content(savedBookJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(bookSaved.getName())))
                .andExpect(jsonPath("$.isbn", is(bookSaved.getIsbn())))
                .andExpect(jsonPath("$.id", is(bookSaved.getId().intValue())))
                .andDo(print());
    }
```
## Service

* Main logic layer
* Transactions enabled
* Secured with method level annotations
* eg:
```dtd
@Service
public class BookServiceImpl implements BookService {
    @Autowired
    BookRepository bookRepository;

    @Override
    @Secured ({"ROLE_USER", "ROLE_ADMIN"})
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    @Transactional
    @Secured({"ROLE_ADMIN"})
    public Book save(Book book) {
          return bookRepository.save(book);
    }
}

```
### Testing the Service Layer
* Uses real service classes (Minimize the mocking)
* Tests are transactional and data used for testing rolled back
* Repository layer is tested as well
* Uses @WithMockUser for authentication/authorization
* eg:

```dtd
@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class BookServiceTest {

    @Autowired
    BookService bookService;
    @Test
    @WithMockUser(username="admin",roles={"ADMIN"})
    public void givenAuthenticated_whenCallServiceWithSecured_thenOk() {
        Book bookToCreate=new Book("Back door deals","ISBN 978-0-596-54068-7","Sisil Gamage");
        Book bookSaved=bookService.save(bookToCreate);
        assertNotNull(bookSaved.getId());
    }

```


## Repository

* Handles all the database or backing service calls 
* JPA(java persistence API)  based
* Optionally, jdbcTemplate based implementation to handle limitations with JPA
* Uses JPQL for fetching objects from the repository
* eg
```dtd
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = "SELECT b FROM Book b WHERE b.author = :author and b.name = :name", nativeQuery = false)
    List<Book> findBooksByAuthorAndName(
            @Param("author") String author, @Param("name") String name);

}
```

### Testing the Repository Layer
* No tests,assuming all repository layer functionality is covered throght service layer testing
* If required, one can easily follow the approach for service layer testing for repository layer as well
* Enable @Transactional in each repository test so that data won`t be persisted after each test run

## Entities
* Represents business entities of the system
* Uses JPA implementation
* Defines the constraints for the entity fields so validations can be automated
* eg

```
  @Entity
  @Table(name = "book")
  public class Book implements AuditableEntity{
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;
      @Column(name = "name", length = 45, nullable = false, unique = false)
      @NotBlank(message = "Name is mandatory")
      private String name;
      @Column(name = "isbn", length = 45, nullable = false, unique = true)
      @ISBNFormat(message = "Not a valid ISBN format")
```


## Security
* Based on spring security
* `/api-blueprint/sign-up` url to sign up
* Spring provided `/login` url to sign in
* JWT token based authentication
* URL and method level authorization
  

### Authentication

See `UserSignUpControllerTest.java and ``UserLoginControllerTest.java` <br>
Filter `JWTAuthenticationFilter.java`

### Authorization

See `BookControllerTest` for mocked user based testing<br>
Filter `JWTAuthorizationFilter.java`

### Role Based Access
* URL level authorization
* eg: See in 
```dtd
SpringSecurityConfig.java
```
```dtd
 // Secure the endpoints with authentication and authorization
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
               .antMatchers(HttpMethod.POST, "/*/*/sign-up").permitAll()
                //Actual role must be ROLE_USER. This is spring thing
               .antMatchers(HttpMethod.GET,"/*/*/book**").hasAnyRole("USER","ADMIN")
               .antMatchers(HttpMethod.POST,"/*/*/book**").hasRole("ADMIN")
               .anyRequest().authenticated()
               .and()
               .addFilter(new JWTAuthenticationFilter(authenticationManager()))
               .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                // this disables session creation on Spring Security
               .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
```




## Validation and Exception Handling



## Logging 

## Swagger API documentation

## Secure Coding Practices





