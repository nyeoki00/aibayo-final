package com.aico.aibayo.repository.kid;

import com.aico.aibayo.common.AcceptStatusEnum;
import com.aico.aibayo.common.AcceptTypeEnum;
import com.aico.aibayo.common.BooleanEnum;
import com.aico.aibayo.common.MemberStatusEnum;
import com.aico.aibayo.dto.kid.KidDto;
import com.aico.aibayo.dto.kid.KidSearchCondition;
import com.aico.aibayo.entity.QAcceptLogEntity;
import com.aico.aibayo.entity.QClassEntity;
import com.aico.aibayo.entity.QClassKidEntity;
import com.aico.aibayo.entity.QClassTeacherEntity;
import com.aico.aibayo.entity.QInviteCodeEntity;
import com.aico.aibayo.entity.QKidEntity;
import com.aico.aibayo.entity.QMemberEntity;
import com.aico.aibayo.entity.QParentKidEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KidRepositoryCustomImpl implements KidRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    private final QKidEntity kid = QKidEntity.kidEntity;
    private final QClassKidEntity classKid = QClassKidEntity.classKidEntity;
    private final QAcceptLogEntity acceptLog1 = QAcceptLogEntity.acceptLogEntity;
    private final QClassEntity clazz = QClassEntity.classEntity;
    private final QClassTeacherEntity classTeacher = QClassTeacherEntity.classTeacherEntity;
    private final QMemberEntity member = QMemberEntity.memberEntity;
    private final QAcceptLogEntity acceptLog2 = new QAcceptLogEntity("acceptLog2");
    private final QParentKidEntity parentKid = QParentKidEntity.parentKidEntity;
    private final QInviteCodeEntity inviteCode = QInviteCodeEntity.inviteCodeEntity;
    private final QAcceptLogEntity acceptLog3 = new QAcceptLogEntity("acceptLog3");

    @Override
    public List<KidDto> findAllByMemberId(Long id) {
        return jpaQueryFactory
                .select(Projections.constructor(KidDto.class,
                        kid.kidNo,
                        kid.kinderNo,
                        kid.kidName,
                        kid.kidBirth,
                        kid.kidGender,
                        kid.admissionDate,
                        kid.modifyDate,
                        kid.dischargeDate,
                        kid.dischargeFlag
                        ))
                .from(kid)
                .join(classKid).on(kid.kidNo.eq(classKid.kidNo))
                .join(acceptLog1).on(acceptLog1.acceptNo.eq(classKid.acceptNo))
                .join(clazz).on(
                        clazz.classNo.eq(classKid.classNo)
                        .and(clazz.classDeleteFlag.eq(BooleanEnum.FALSE.getBool()))
                )
                .join(classTeacher).on(clazz.classNo.eq(classTeacher.classNo))
                .join(member).on(
                        member.id.eq(classTeacher.classTeacherId),
                        member.kinderNo.eq(kid.kinderNo)
                ).join(acceptLog2).on(acceptLog2.acceptNo.eq(classTeacher.acceptNo))
                .where(
                        kid.dischargeFlag.eq(BooleanEnum.FALSE.getBool()),
                        acceptLog1.acceptStatus.eq(AcceptStatusEnum.ACCEPT.getStatus()),
                        acceptLog2.acceptStatus.eq(AcceptStatusEnum.ACCEPT.getStatus()),
                        member.id.eq(id)
                ).fetch();
    }

    @Override
    public List<KidDto> findAllByClassNoAndAcceptStatus(KidSearchCondition condition) {
        return jpaQueryFactory
                .selectDistinct(Projections.constructor(KidDto.class,
                        kid.kidNo,
                        kid.kinderNo,
                        kid.kidName,
                        kid.kidBirth,
                        kid.kidGender,
                        kid.admissionDate,
                        kid.modifyDate,
                        kid.dischargeDate,
                        kid.dischargeFlag
                        ))
                .from(kid)
                .leftJoin(parentKid).on(kid.kidNo.eq(parentKid.kidNo))
                .leftJoin(acceptLog1).on(
                        acceptLog1.acceptNo.eq(parentKid.acceptNo)
                        .and(acceptLog1.acceptStatus.eq(AcceptStatusEnum.ACCEPT.getStatus()))
                )

                .join(classKid).on(kid.kidNo.eq(classKid.kidNo))
                .join(clazz).on(
                        clazz.classNo.eq(classKid.classNo)
                        .and(clazz.classDeleteFlag.eq(BooleanEnum.FALSE.getBool()))
                )
                .join(acceptLog2).on(
                        acceptLog2.acceptNo.eq(classKid.acceptNo)
                        .and(acceptLog2.acceptStatus.eq(AcceptStatusEnum.ACCEPT.getStatus()))
                )

                .where(
                        kid.dischargeFlag.eq(BooleanEnum.FALSE.getBool()),
                        kid.kinderNo.eq(condition.getKinderNo()),
                        getClassEq(condition.getClassNo())
                )
                .orderBy(kid.kidName.asc())
                .fetch();
    }

    @Override
    public List<KidDto> findAllWithParentByClassNoAndAcceptStatus(KidSearchCondition condition) {
        return jpaQueryFactory
                .selectDistinct(Projections.constructor(KidDto.class,
                        kid.kidNo,
                        kid.kinderNo,
                        kid.kidName,
                        kid.kidBirth,
                        kid.kidGender,
                        kid.admissionDate,
                        kid.modifyDate,
                        kid.dischargeDate,
                        kid.dischargeFlag,
                        member.username,
//                        inviteCode.inviteEmail
                        acceptLog1.acceptNo
                ))
                .from(kid)
                .leftJoin(parentKid).on(kid.kidNo.eq(parentKid.kidNo))
                .leftJoin(member).on(
                        member.id.eq(parentKid.id)
                        .and(member.status.eq(MemberStatusEnum.ACTIVE.getStatus()))
                )
                .leftJoin(acceptLog1).on(acceptLog1.acceptNo.eq(parentKid.acceptNo))

                .join(classKid).on(kid.kidNo.eq(classKid.kidNo))
                .join(clazz).on(
                        clazz.classNo.eq(classKid.classNo)
                        .and(clazz.classDeleteFlag.eq(BooleanEnum.FALSE.getBool()))
                )
                .join(acceptLog2).on(
                        acceptLog2.acceptNo.eq(classKid.acceptNo)
                        .and(acceptLog2.acceptStatus.eq(AcceptStatusEnum.ACCEPT.getStatus()))
                )

//                .leftJoin(inviteCode).on(kid.kidNo.eq(inviteCode.kidNo))
//                .leftJoin(acceptLog3).on(
//                        inviteCode.acceptNo.eq(acceptLog3.acceptNo)
//                        .and(acceptLog3.acceptStatus.eq(AcceptStatusEnum.ACCEPT.getStatus()))
//                )

                .where(
                        kid.dischargeFlag.eq(BooleanEnum.FALSE.getBool()),
                        kid.kinderNo.eq(condition.getKinderNo()),
                        getClassEq(condition.getClassNo()),
                        getAcceptStatusEq(condition.getAcceptStatus())
//                        getInviteAcceptTypeEq(condition.getInviteAcceptStatus())
                )
                .orderBy(kid.kidName.asc())
                .fetch();
    }

    @Override
    public List<KidDto> findAllWithInviteByClassNoAndAcceptStatus(KidSearchCondition condition) {
        return jpaQueryFactory
                .selectDistinct(Projections.constructor(KidDto.class,
                        kid.kidNo,
                        kid.kinderNo,
                        kid.kidName,
                        kid.kidBirth,
                        kid.kidGender,
                        kid.admissionDate,
                        kid.modifyDate,
                        kid.dischargeDate,
                        kid.dischargeFlag,
                        inviteCode.inviteId,
                        inviteCode.inviteEmail,
                        acceptLog1.acceptNo,
                        acceptLog3.acceptNo
                ))
                .from(kid)
                .leftJoin(parentKid).on(kid.kidNo.eq(parentKid.kidNo))
                .leftJoin(member).on(
                        member.id.eq(parentKid.id)
                                .and(member.status.eq(MemberStatusEnum.ACTIVE.getStatus()))
                )
                .leftJoin(acceptLog1).on(acceptLog1.acceptNo.eq(parentKid.acceptNo))

                .join(classKid).on(kid.kidNo.eq(classKid.kidNo))
                .join(clazz).on(
                        clazz.classNo.eq(classKid.classNo)
                                .and(clazz.classDeleteFlag.eq(BooleanEnum.FALSE.getBool()))
                )
                .join(acceptLog2).on(
                        acceptLog2.acceptNo.eq(classKid.acceptNo)
                                .and(acceptLog2.acceptStatus.eq(AcceptStatusEnum.ACCEPT.getStatus()))
                )

                .leftJoin(inviteCode).on(kid.kidNo.eq(inviteCode.kidNo))
                .leftJoin(acceptLog3).on(
                        inviteCode.acceptNo.eq(acceptLog3.acceptNo)
                                .and(acceptLog3.acceptStatus.eq(AcceptStatusEnum.ACCEPT.getStatus()))
                )

                .where(
                        kid.dischargeFlag.eq(BooleanEnum.FALSE.getBool()),
                        kid.kinderNo.eq(condition.getKinderNo()),
                        getClassEq(condition.getClassNo()),
                        getAcceptStatusEq(condition.getAcceptStatus()),
                        getInviteAcceptTypeEq(condition.getInviteAcceptStatus())
                )
                .orderBy(kid.kidName.asc())
                .fetch();
    }

    @Override
    public List<KidDto> findAllByParent(Long id) {
        return jpaQueryFactory
                .select(Projections.constructor(KidDto.class,
                        kid.kidNo,
                        kid.kinderNo,
                        kid.kidName,
                        kid.kidBirth,
                        kid.kidGender,
                        kid.admissionDate,
                        kid.modifyDate,
                        kid.dischargeDate,
                        kid.dischargeFlag
                ))
                .from(kid)
                .join(parentKid).on(kid.kidNo.eq(parentKid.kidNo))
                .join(member).on(
                        member.id.eq(parentKid.id)
                                .and(member.status.eq(MemberStatusEnum.ACTIVE.getStatus()))
                )
                .join(acceptLog1).on(
                        acceptLog1.acceptNo.eq(parentKid.acceptNo)
                                .and(acceptLog1.acceptStatus.eq(AcceptStatusEnum.ACCEPT.getStatus()))
                )
                .where(
                        kid.dischargeFlag.eq(BooleanEnum.FALSE.getBool()),
                        member.id.eq(id)
                )
                .fetch();
    }

    private BooleanExpression getInviteAcceptTypeEq(Integer inviteAcceptStatus) {
        return inviteAcceptStatus == null ?
                null : acceptLog3.acceptType.eq(AcceptTypeEnum.INVITE_CODE.getType());
    }

    private BooleanExpression getAcceptStatusEq(Integer acceptStatus) {
        return acceptStatus == null ? null : acceptLog1.acceptStatus.eq(acceptStatus);
    }

    private BooleanExpression getClassEq(Long classNo) {
        return classNo == null ? null : clazz.classNo.eq(classNo);
    }
}
