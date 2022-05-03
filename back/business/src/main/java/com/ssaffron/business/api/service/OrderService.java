package com.ssaffron.business.api.service;

import com.ssaffron.business.api.dto.*;
import com.ssaffron.business.api.entity.*;
import com.ssaffron.business.api.exception.NullAddressException;
import com.ssaffron.business.api.exception.NullApplyException;
import com.ssaffron.business.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;
    private final EmployeeRepository employeeRepository;
    private final CollectRepository collectRepository;
    private final PayRepository payRepository;
    private final LaundryPlanRepository laundryPlanRepository;
    private final ApplyRepository applyRepository;

    public boolean collectionRequest(String memberEmail, List<CollectDto> collectDtoList){
        //수거 신청 성공/실패
        MemberEntity memberEntity = memberRepository.findByMemberEmail(memberEmail);
        if(memberEntity.getMemberAddress() == null)
            throw new NullAddressException();
        ApplyEntity applyEntity = applyRepository.findByMemberEntityAndApplyDeliveryCountIsNotNull(memberEntity).orElse(null);
        if(applyEntity == null){
            throw new NullApplyException(memberEntity.getMemberName());
        }
        List<CollectEntity> collectEntityList = collectDtoList.
                stream().map(collectDto -> new CollectEntity(
                        collectDto.getCollecttype(), memberEntity)).collect(Collectors.toList());
        collectRepository.saveAll(collectEntityList);
        return true;
    }

    public List<CollectDto> collectionRequestInquiry(String memberEmail){
        //한 사용자의 수거 요청 목록을 조회 할 수 있다.
        //수거한 직원의 정보를 볼 수 없다. -> 사용자용
        MemberEntity memberEntity = memberRepository.findByMemberEmail(memberEmail);
        return collectRepository.findAllByMemberEntity(memberEntity).
                stream().map(collectEntity -> CollectDto.builder().
                    collectId(collectEntity.getCollectId()).
                    collectRequestDate(collectEntity.getCollectRequestDate()).
                    collectWithdrawDate(collectEntity.getCollectWithdrawDate()).
                    collecttype(collectEntity.getCollectType()).
                    build()).collect(Collectors.toList());
                    //collectId를 넣는 이유 -> 접수번호 확인
    }






    public boolean withdrawalOfCollection(long collectionId){
        //사용자 인증은 JWT를 통해 본인만 진행이 되고, collectionId자체는 PK이기 때문에 중복이 없음
        //직원은 접근이 가능함.
        CollectEntity collectEntity = collectRepository.findById(collectionId).get();
        collectEntity.setCollectWithdrawDate(LocalDateTime.now());
        collectRepository.save(collectEntity);
        return true;

    }



    public List<PayDto> getBill(String memberEmail){
        MemberEntity memberEntity = memberRepository.findByMemberEmail(memberEmail);
        return payRepository.findAllByMemberEntity(memberEntity).
                stream().map(payEntity -> PayDto.builder().
                    payRequestCount(payEntity.getPayRequestCount()).
                    payResponseDate(payEntity.getPayResponseDate()).
                    payPickDate(payEntity.getPayPickDate()).
                    laundryPlanDto(LaundryPlanDto.builder()
                            .laundryPlanDescription(payEntity.getLaundryPlanEntity().getLaundryPlanDescription())
                            .laundryPlanTypeKor(payEntity.getLaundryPlanEntity().getLaundryPlanTypeKor())
                            .laundryPlanTypeEng(payEntity.getLaundryPlanEntity().getLaundryPlanTypeEng())
                            .laundryPlanDetails(payEntity.getLaundryPlanEntity().getLaundryPlanDetails())
                            .laundryPlanPrice(payEntity.getLaundryPlanEntity().getLaundryPlanPrice())
                            .build()
                    ).build()
        ).collect(Collectors.toList());
    }


}
