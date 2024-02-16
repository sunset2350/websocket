package com.project.notice.auth;

import com.project.notice.auth.entity.Login;
import com.project.notice.auth.entity.LoginRepository;
import com.project.notice.profile.Profile;
import com.project.notice.profile.ProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.notice.auth.util.HashUtil;

@Service
public class AuthService {

    private LoginRepository loginRepository;

    private ProfileRepository profileRepository;

    @Autowired
    private HashUtil hashUtil;

    @Autowired
    public AuthService(LoginRepository loginRepository, ProfileRepository profileRepository){
        this.loginRepository = loginRepository;
        this.profileRepository = profileRepository;
    }


    @Transactional
    public long createIdentity(SignupRequest signupRequest) {
        Login toSaveLogin =
                Login.builder()
                        .userId(signupRequest.getUserId())
                        .userPw(hashUtil.createHash(signupRequest.getUserPw()))
                        .build();

        Login savedLogin = loginRepository.save(toSaveLogin);

        Profile toProfileSave =
                Profile.builder()
                        .userName(signupRequest.getUserName())
                        .build();

        long profileNo = profileRepository.save(toProfileSave).getNo();
        loginRepository.save(savedLogin);

        return profileNo;
    }

}
