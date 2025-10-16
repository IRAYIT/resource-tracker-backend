package com.ikonicit.resource.tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;




@Entity
@Data
@Table(name = "OfferLetter")
public class Internship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    private String name;
    private String offerLetterReleasingDate;
    private String town;
    private String district;
    private String internPosition;
    private String commencmentDate;
    private String submissionDate;
    private String email;
    private String phone;




    // Other methods if needed
}
