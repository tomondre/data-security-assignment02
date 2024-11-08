package server.model;

public interface Model {
    public int getNumberOfUsers();
    public User getUser(int index) throws IndexOutOfBoundsException;
    public server.model.User getUserByName(String name);
    public void addUser(server.model.User user) throws IllegalStateException, IllegalArgumentException;
    public void addUser(server.model.UserName userName, Password password) throws IllegalStateException, IllegalArgumentException;
    public void addUser(String userName, String password) throws IllegalStateException, IllegalArgumentException;
    public boolean contains(server.model.User user);
}
