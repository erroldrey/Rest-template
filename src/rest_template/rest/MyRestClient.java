package rest_template.rest;

import rest_template.model.User;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MyRestClient {
    private final RestTemplate template = new RestTemplate();
    private final String URL = "http://94.198.50.185:7081/api/users";
    private HttpHeaders headers;

    public MyRestClient() {
        getHeaders();
    }

    private HttpEntity<User> getHttpEntity(User user) {
        return new HttpEntity<>(user, headers);
    }

    private void getHeaders() {
        String sessionId = Objects.requireNonNull(template.headForHeaders(URL)
                .getFirst("Set-Cookie")).split(";")[0];
        HttpHeaders head =  new HttpHeaders();
        head.setContentType(MediaType.APPLICATION_JSON);
        head.add("Cookie", sessionId);
        this.headers = head;
    }

    public List<User> getAllUsers() {
        return Arrays.stream(Objects.requireNonNull(template.getForObject(URL, User[].class, headers)))
                .toList();
    }

    public String saveUser(User user) {
        return template.exchange(URL, HttpMethod.POST, getHttpEntity(user), String.class).getBody();
    }

    public String updateUser(User user) {
        return template.exchange(URL, HttpMethod.PUT, getHttpEntity(user), String.class).getBody();
    }

    public String deleteUser(Long id) {
        return template.exchange(URL + "/" + id, HttpMethod.DELETE, new HttpEntity<>(headers), String.class).getBody();
    }


    public static void main(String[] args) {
        MyRestClient client = new MyRestClient();

        List<User> list = client.getAllUsers();
        list.forEach(System.out::println);

        User user = new User(3L, "James", "Brown", (byte) 26);
        String saveUserResponse = client.saveUser(user);

        User updateUser = new User(3L, "Thomas", "Shelby", (byte) 26);
        String updateUserResponse = client.updateUser(updateUser);

        String deleteUserResponse = client.deleteUser(3L);

        String finishStr = saveUserResponse + updateUserResponse + deleteUserResponse;

        System.out.println("Конечная строка = " + finishStr);
        System.out.println("Длинна конечной строки = " + finishStr.length());
    }
}
