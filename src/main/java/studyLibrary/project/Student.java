package studyLibrary.project;

import java.util.Objects;

public class Student extends User {
    











    @Override
    public boolean equals(Object other) { //Buna studymate kısmında ihtiyacım olduğu için 
    // senden önce yapmam gerekti, classlarımız bağlı olduğu için arada küçük dokunuşlar yapmak zorunlu oluyor. Okuyunca silersin
        if(this == other) return true;
        if(!(other instanceof Student)) return false;
        Student otherStudent = (Student) other;
        if(this.userId != otherStudent.userId || !(this.email.equals(otherStudent.email))){
            return false;
        }
        return true;
    }
    @Override
    public int hashCode() { // Equals metodunu UserID ve Email üzerinden karşılaştırır, her kullanıcının kendine has ID ve emaili olduğunu düşünürsek
        return Objects.hash(userId, email); 
    }
}
