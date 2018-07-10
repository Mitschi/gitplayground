import java.util.Objects;

public class MyObject {
    private int length;

    public MyObject(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyObject myObject = (MyObject) o;
        return length == myObject.length;
    }

    @Override
    public int hashCode() {
        return Objects.hash(length);
    }
}
