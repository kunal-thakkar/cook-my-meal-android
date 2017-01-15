package in.co.krishnaconsultancy.authentication;

public interface ServerAuthenticate {
    public User userSignUp(final String name, final String email, final String pass) throws Exception;
    public User userSignIn(final String user, final String pass) throws Exception;
}
