package com.example.chatbot;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "hakgwa")
public class Hakgwa {
    private Integer hakgwaId;
    private Gyeyeol gyeyeol;
    private String hakgwaNameKr;
    private List<Professor> professorList;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getHakgwaId() {
        return hakgwaId;
    }
    public void setHakgwaId(Integer hakgwaId) {
        this.hakgwaId = hakgwaId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gyeyeol_id", nullable = false)
    public Gyeyeol getGyeyeol() {
        return gyeyeol;
    }
    public void setGyeyeol(Gyeyeol gyeyeol) {
        this.gyeyeol = gyeyeol;
    }

    @Column(name = "hakgwa_name_kr", nullable = false, length = 60)
    public String getHakgwaNameKr() {
        return hakgwaNameKr;
    }
    public void setHakgwaNameKr(String hakgwaNameKr) {
        this.hakgwaNameKr = hakgwaNameKr;
    }

    @OneToMany(mappedBy = "hakgwa", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Professor> getProfessorList() {
        return professorList;
    }
    public void setProfessorList(List<Professor> professorList) {
        this.professorList = professorList;
    }
}