package com.example.chatbot;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "gyeyeol")
public class Gyeyeol {
    private Integer gyeyeolId;
    private String gyeyeolNameKr;
    private List<Hakgwa> hakgwaList;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getGyeyeolId() {
        return gyeyeolId;
    }
    public void setGyeyeolId(Integer gyeyeolId) {
        this.gyeyeolId = gyeyeolId;
    }

    @Column(name = "gyeyeol_name_kr", nullable = false, length = 50)
    public String getGyeyeolNameKr() {
        return gyeyeolNameKr;
    }
    public void setGyeyeolNameKr(String gyeyeolNameKr) {
        this.gyeyeolNameKr = gyeyeolNameKr;
    }

    @OneToMany(mappedBy = "gyeyeol", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Hakgwa> getHakgwaList() {
        return hakgwaList;
    }
    public void setHakgwaList(List<Hakgwa> hakgwaList) {
        this.hakgwaList = hakgwaList;
    }
}