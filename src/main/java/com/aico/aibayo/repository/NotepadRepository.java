package com.aico.aibayo.repository;

import com.aico.aibayo.entity.NotepadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotepadRepository extends JpaRepository<NotepadEntity, Long> {
}
