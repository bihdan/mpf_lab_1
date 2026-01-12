package sumdu.edu.ua.core.domain;

public class PageRequest {
    private final int page;
    private final int size;
    private final String sortBy;

    public PageRequest(int page, int size, String sortBy) {
        if (page < 0) throw new IllegalArgumentException("page must be >= 0");
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
    }

    public int getPage() { return page; }
    public int getSize() { return size; }
    public String getSortBy() { return sortBy; }
}