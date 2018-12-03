package xin.wangning.domain;

public class User {
    private Long id;
    private String name;
    private String url;

    public User(String name,String url){
        this.name = name;
        this.url = url;
    }

    public User(){};

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
