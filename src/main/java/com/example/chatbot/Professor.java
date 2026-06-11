package com.example.chatbot;

import jakarta.persistence.*;

@Entity
@Table(name = "professor")
public class Professor {
    private Integer profId;
    private Hakgwa hakgwa;
    private String nameKr;
    private String positionTitle;
    private String officeRoom;
    private String phone;
    private String email;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getProfId() {
        return profId;
    }
    public void setProfId(Integer profId) {
        this.profId = profId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hakgwa_id", nullable = false)
    public Hakgwa getHakgwa() {
        return hakgwa;
    }
    public void setHakgwa(Hakgwa hakgwa) {
        this.hakgwa = hakgwa;
    }

    @Column(name = "name_kr", nullable = false, length = 30)
    public String getNameKr() {
        return nameKr;
    }
    public void setNameKr(String nameKr) {
        this.nameKr = nameKr;
    }

    @Column(name = "position_title", length = 40)
    public String getPositionTitle() {
        return positionTitle;
    }
    public void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }

    @Column(name = "office_room", length = 80)
    public String getOfficeRoom() {
        return officeRoom;
    }
    public void setOfficeRoom(String officeRoom) {
        this.officeRoom = officeRoom;
    }

    @Column(name = "phone", length = 20)
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "email", length = 100)
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}