package studyLibrary.project;

import java.util.ArrayList;

public class Librarian extends User{

    public Librarian(int userID, String name, String email, String password) {
        super(userID, name, email, password);
    }
    public void addBook(LibrarySystem system, Book b){
        system.addBookDB(b);
    }
    public void removeBook(LibrarySystem system, Book b){
        system.deleteBookDB(b);
    }
    public void setbookID(int newBookID, Book b){
        b.setbookID(newBookID);
    }
    public void setTittle(String newTittle, Book b){
        b.setTittle(newTittle);
    }
    public void setAuthor(String newAuthor, Book b){
        b.setAuthor(newAuthor);
    }
    public void setCategory(String newCategory, Book b){
        b.setCategory(newCategory);
    }
    public ArrayList<Book> searchBook(ArrayList<Book> booklist, String keyword){
        ArrayList<Book> results = new ArrayList<>();
        for (Book book : booklist) {
        if (book.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
            results.add(book);
        }
    }

    return results;
    }
    public boolean approveRes(Book b){
        if(b.isAvailable() == true){
            return true;
        }
        return false;
    }     
}