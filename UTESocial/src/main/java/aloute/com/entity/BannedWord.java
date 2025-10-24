// package aloute.com.entity;

// import java.time.LocalDateTime;

// import org.hibernate.annotations.OnDelete;
// import org.hibernate.annotations.OnDeleteAction;

// import jakarta.persistence.*;

// @Entity
// @Table(name = "banned_words")
// public class BannedWord {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "WordID")
//     private Integer wordId;

//     @Column(name = "Word", length = 100, unique = true, nullable = false)
//     private String word;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "AddedBy")
//     @OnDelete(action = OnDeleteAction.SET_NULL) 
//     private User addedBy;

//     @Column(name = "CreatedAt")
//     private LocalDateTime createdAt = LocalDateTime.now();

//     //Constructors
//     public BannedWord() {
//     }

//     public BannedWord(String word, User addedBy) {
//         this.word = word;
//         this.addedBy = addedBy;
//     }

//     // Getters and Setters
//     public Integer getWordId() {
//         return wordId;
//     }

//     public void setWordId(Integer wordId) {
//         this.wordId = wordId;
//     }

//     public String getWord() {
//         return word;
//     }

//     public void setWord(String word) {
//         this.word = word;
//     }

//     public User getAddedBy() {
//         return addedBy;
//     }

//     public void setAddedBy(User addedBy) {
//         this.addedBy = addedBy;
//     }

//     public LocalDateTime getCreatedAt() {
//         return createdAt;
//     }

//     public void setCreatedAt(LocalDateTime createdAt) {
//         this.createdAt = createdAt;
//     }

//     // Getters and Setters
    
// }
