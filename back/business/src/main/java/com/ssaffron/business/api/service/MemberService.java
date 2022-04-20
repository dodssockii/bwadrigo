package com.ssaffron.business.api.service;

import com.ssaffron.business.api.config.JwtUtil;
import com.ssaffron.business.api.config.UserRole;
import com.ssaffron.business.api.dto.MemberDto;
import com.ssaffron.business.api.entity.MemberEntity;
import com.ssaffron.business.api.entity.MemberStatus;

import com.ssaffron.business.api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    private final JwtUtil jwtUtil;

    private final CookieUtil cookieUtil;

    private final RedisUtil redisUtil;

    private void saveMember(MemberDto memberDto, MemberEntity memberEntity) {
        memberEntity.setMemberName(memberDto.getMemberName());
        memberEntity.setMemberEmail(memberDto.getMemberEmail());
        memberEntity.setMemberPassword(memberDto.getMemberPassword());
        memberEntity.setMemberAddress(memberDto.getMemberAddress());
        memberEntity.setMemberAge(memberDto.getMemberAge());
        memberEntity.setMemberPhone(memberDto.getMemberPhone());
        memberEntity.setMemberGender(memberDto.isMemberGender());
        memberEntity.setMemberStatus(MemberStatus.ACTIVATE);
        memberEntity.setRole(UserRole.ROLE_USER);
        memberRepository.save(memberEntity);
    }

    public void registerMember(MemberDto memberDto){
        MemberEntity memberEntity = new MemberEntity();
        saveMember(memberDto, memberEntity);
    }

    public MemberEntity getMember(String memberEmail){
        return memberRepository.findByMemberEmail(memberEmail);
    }

    public void updateMember(MemberDto memberDto){
        MemberEntity memberEntity = getMember(memberDto.getMemberEmail());
        saveMember(memberDto, memberEntity);
    }

    public void deleteMember(String memberEmail){
        MemberEntity memberEntity = getMember(memberEmail);
        memberEntity.setMemberStatus(MemberStatus.DEACTIVATE);
    }

    public Map<String, Object> login(String memberEmail, String memberPwd) {
        Map<String, Object> result = new HashMap<>();
        MemberEntity memberEntity = memberRepository.findByMemberEmail(memberEmail);

        if(memberEntity == null)
            return null;
        if(!memberEntity.getMemberPassword().equals(memberPwd))
            return null;

        String token = jwtUtil.generateToken(memberEntity);
        String refreshJwt = jwtUtil.generateRefreshToken(memberEntity);
        Cookie accessToken = cookieUtil.createCookie(JwtUtil.ACCESS_TOKEN_NAME, token);
        Cookie refreshToken = cookieUtil.createCookie(JwtUtil.REFRESH_TOKEN_NAME, refreshJwt);
        redisUtil.setDataExpire(refreshJwt, memberEntity.getMemberEmail(), JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);
//        res.addCookie(accessToken);
//        res.addCookie(refreshToken);

        result.put("memberName", memberEntity.getMemberName());
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);

        log.info(redisUtil.getData(refreshJwt));

        return result;
    }
}