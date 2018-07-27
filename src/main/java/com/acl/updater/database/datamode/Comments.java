package com.acl.updater.database.datamode;

import java.util.Objects;

/**
 * Represent tables comments
 */
public class Comments {
    private int id; //primaryKey
    private int book;
    private String text; //commentaire

    public Comments(int id, int book, String text) {
        this.id = id;
        this.book = book;
        this.text = text;
    }

    @Override
    public String toString() {
        return "Comments{" +
                "id=" + id +
                ", book=" + book +
                ", text='" + text + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comments)) return false;
        Comments comments = (Comments) o;
        return id == comments.id &&
                book == comments.book &&
                text.equals(comments.text);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, book, text);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBook() {
        return book;
    }

    public void setBook(int book) {
        this.book = book;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
