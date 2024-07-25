package com.aico.aibayo.service.notepad;

import com.aico.aibayo.common.BooleanEnum;
import com.aico.aibayo.dto.notepad.NotepadDto;
//import com.aico.aibayo.entity.QNotepadEntity;
import com.aico.aibayo.dto.notepad.NotepadSearchCondition;
import com.aico.aibayo.entity.BoardEntity;
import com.aico.aibayo.entity.LifeRecordEntity;
import com.aico.aibayo.entity.NotepadEntity;
import com.aico.aibayo.entity.NotepadReceiverEntity;
import com.aico.aibayo.repository.BoardRepository;
import com.aico.aibayo.repository.LifeRecordRepository;
import com.aico.aibayo.repository.NotepadReceiverRepository;
import com.aico.aibayo.repository.notepad.NotepadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotepadServiceImpl implements NotepadService {
    private final NotepadRepository notepadRepository;
    private final BoardRepository boardRepository;
    private final LifeRecordRepository lifeRecordRepository;
    private final NotepadReceiverRepository notepadReceiverRepository;

    private static final int PAGE_SIZE = 6;

    @Override
    public Page<NotepadDto> getAllByKinderNo(NotepadSearchCondition condition, int page) {
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);

        return notepadRepository.findAllByKinderNo(condition, pageable);
    }

    @Override
    public Page<NotepadDto> getAllByKidNo(NotepadSearchCondition condition, int page) {
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);

        return notepadRepository.findAllByKidNo(condition, pageable);
    }

    @Override
    public NotepadDto getByNotepadNo(Long notepadNo) {
        return notepadRepository.findByNotepadNo(notepadNo);
    }

    @Override
    @Transactional
    public NotepadDto insertNotepad(NotepadDto notepadDto) {
        BoardEntity boardEntity = BoardEntity.builder()
                .invisibleFlag(BooleanEnum.FALSE.getBool())
                .boardType(notepadDto.getBoardType())
                .writer(notepadDto.getWriter())
                .boardTitle(notepadDto.getBoardTitle())
                .boardContents(notepadDto.getBoardContents())
                .boardRegDate(LocalDateTime.now())
                .build();

        BoardEntity savedBoard = boardRepository.save(boardEntity);

        NotepadEntity notepadEntity = NotepadEntity.builder()
                .boardNo(savedBoard.getBoardNo())
                .hasLifeRecord(notepadDto.getHasLifeRecord())
                .notepadDate(notepadDto.getNotepadDate())
                .build();

        NotepadEntity savedNotepad = notepadRepository.save(notepadEntity);

        if (savedNotepad.getHasLifeRecord().equals(BooleanEnum.TRUE.getBool())) {

            LifeRecordEntity lifeRecordEntity = LifeRecordEntity.builder()
                    .notepadNo(savedNotepad.getNotepadNo())
                    .mood(notepadDto.getMood())
                    .health(notepadDto.getHealth())
                    .temperature(notepadDto.getTemperature())
                    .meal(notepadDto.getMeal())
                    .sleepTime(notepadDto.getSleepTime())
                    .defecationStatus(notepadDto.getDefecationStatus())
                    .build();

            lifeRecordRepository.save(lifeRecordEntity);

            NotepadReceiverEntity notepadReceiverEntity =
                    NotepadReceiverEntity.builder()
                    .kidNo(notepadDto.getKidNo())
                    .build();

            notepadReceiverRepository.save(notepadReceiverEntity);
        } else if (savedNotepad.getHasLifeRecord().equals(BooleanEnum.FALSE.getBool())) {
            NotepadReceiverEntity notepadReceiverEntity =
                    NotepadReceiverEntity.builder()
                            .classNo(notepadDto.getClassNo())
                            .build();

            notepadReceiverRepository.save(notepadReceiverEntity);
        }

        return null;
    }

}
