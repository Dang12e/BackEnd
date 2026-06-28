package com.testBackendDatabase.demo.model;



import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Table(name="movies")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieInfo {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    
    private String title;
    @Column(columnDefinition = "LONGTEXT")
    private String description;
    private String genre;
    private String image;
    private String rating;
    private LocalDate releaseDate;
    
    private Integer duration; // Đổi thành Integer để dễ tính toán giờ kết thúc

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ShowTime> showtimes;
}
