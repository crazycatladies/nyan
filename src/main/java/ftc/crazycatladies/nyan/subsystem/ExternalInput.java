package ftc.crazycatladies.nyan.subsystem;

public class ExternalInput {
    private String name;

    public ExternalInput(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ExternalInput{" +
                "name='" + name + '\'' +
                '}';
    }
}
