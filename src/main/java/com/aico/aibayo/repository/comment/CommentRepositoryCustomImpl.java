package com.aico.aibayo.repository.comment;

import com.aico.aibayo.common.BooleanEnum;
import com.aico.aibayo.dto.comment.CommentDto;
import com.aico.aibayo.dto.comment.CommentSearchCondition;
import com.aico.aibayo.entity.QBoardEntity;
import com.aico.aibayo.entity.QCommentEntity;
import com.aico.aibayo.entity.QMemberEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QCommentEntity comment = QCommentEntity.commentEntity;
    private final QBoardEntity board = QBoardEntity.boardEntity;
    private final QMemberEntity member = QMemberEntity.memberEntity;

    @Override
    public Page<CommentDto> findAllByBoardNo(CommentSearchCondition condition, Pageable pageable) {
        List<CommentDto> comments = jpaQueryFactory
                .select(Projections.constructor(CommentDto.class,
                        member.roleNo,
                        member.name,
                        member.kinderNo,
                        comment.boardNo,
                        comment.commentGroupNo,
                        comment.commentRegDate,
                        comment.commentWriter,
                        comment.commentClass,
                        comment.commentContent,
                        comment.commentDeleteFlag
                ))
                .from(comment)
                .join(board).on(board.boardNo.eq(comment.boardNo))
                .leftJoin(member).on(comment.commentWriter.eq(member.id)) // 이 부분 수정
                .where(
                        getInvisibleFlagEq(board),
                        comment.boardNo.eq(condition.getBoardNo())
                )
                .orderBy(
                        comment.commentGroupNo.desc(),
                        comment.commentRegDate.asc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .join(board).on(board.boardNo.eq(comment.boardNo))
                .join(member).on(comment.commentWriter.eq(member.id)) // 이 부분 수정
                .where(
                        getInvisibleFlagEq(board),
                        comment.boardNo.eq(condition.getBoardNo())
                );

        return PageableExecutionUtils.getPage(comments, pageable, countQuery::fetchOne);
    }

    private BooleanExpression getInvisibleFlagEq(QBoardEntity board) {
        if (board.invisibleFlag == null) {
            return board.invisibleFlag.isNull(); // 또는 board.invisibleFlag.isNotNull();
        }
        return board.invisibleFlag.eq(BooleanEnum.FALSE.getBool());
    }
}
