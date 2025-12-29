package app.dto;


public record Texte(int id, String title, String content, String creator) {
    @Override
    public String toString() {
        return title();
    }

}
