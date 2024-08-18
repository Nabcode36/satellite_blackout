package unsw.objects;

public class File {
    private String name;
    private String content;
    private Integer size;

    public File(String name, String content) {
        this.name = name;
        this.content = content;
        this.size = content.length();
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public Integer getSize() {
        return size;
    }

}
