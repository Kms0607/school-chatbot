package com.example.chatbot;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfessorService {
    private final ProfessorRepository professorRepository;
    private final HakgwaRepository hakgwaRepository;

    public ProfessorService(ProfessorRepository professorRepository, HakgwaRepository hakgwaRepository) {
        this.professorRepository = professorRepository;
        this.hakgwaRepository = hakgwaRepository;
    }

    public Optional<ProfessorVO> getByName(String nameKr) {
        Optional<Professor> opt = professorRepository.findByNameKr(nameKr);
        return opt.map(this::convertVO);
    }

    public List<ProfessorVO> listByHakgwaId(Integer hakgwaId) {
        List<Professor> list = professorRepository.findByHakgwa_HakgwaId(hakgwaId);
        return list.stream().map(this::convertVO).collect(Collectors.toList());
    }

    public ProfessorVO convertVO(Professor p) {
        ProfessorVO vo = new ProfessorVO();
        vo.setProfId(p.getProfId());
        vo.setHakgwaId(p.getHakgwa().getHakgwaId());
        vo.setHakgwaNameKr(p.getHakgwa().getHakgwaNameKr());
        vo.setGyeyeolId(p.getHakgwa().getGyeyeol().getGyeyeolId());
        vo.setGyeyeolNameKr(p.getHakgwa().getGyeyeol().getGyeyeolNameKr());
        vo.setNameKr(p.getNameKr());
        vo.setPositionTitle(p.getPositionTitle());
        vo.setOfficeRoom(p.getOfficeRoom());
        vo.setPhone(p.getPhone());
        vo.setEmail(p.getEmail());
        return vo;
    }

    public List<Professor> batchSave(List<Professor> list) {
        return professorRepository.saveAll(list);
    }

    // 新增根据ID查询VO
    public Optional<ProfessorVO> getById(Integer profId) {
        Optional<Professor> optProf = professorRepository.findById(profId);
        return optProf.map(this::convertVO);
    }
}