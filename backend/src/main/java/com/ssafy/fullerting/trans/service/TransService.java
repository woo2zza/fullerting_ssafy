package com.ssafy.fullerting.trans.service;

import com.ssafy.fullerting.deal.model.dto.request.DealProposeRequest;
import com.ssafy.fullerting.deal.model.entity.Deal;
import com.ssafy.fullerting.deal.repository.DealRepository;
import com.ssafy.fullerting.exArticle.model.dto.request.ExArticleRegisterRequest;
import com.ssafy.fullerting.exArticle.model.dto.response.ExArticleResponse;
import com.ssafy.fullerting.exArticle.model.entity.ExArticle;
import com.ssafy.fullerting.exArticle.model.entity.enums.ExArticleType;
import com.ssafy.fullerting.exArticle.repository.ExArticleRepository;
import com.ssafy.fullerting.global.utils.MessageUtils;
import com.ssafy.fullerting.trans.exception.TransErrorCode;
import com.ssafy.fullerting.trans.exception.TransException;
import com.ssafy.fullerting.trans.model.dto.response.TransResponse;
import com.ssafy.fullerting.trans.model.entity.Trans;
import com.ssafy.fullerting.trans.repository.TransRepository;
import com.ssafy.fullerting.user.model.dto.response.UserResponse;
import com.ssafy.fullerting.user.model.entity.CustomUser;
import com.ssafy.fullerting.user.service.UserService;
import jakarta.transaction.TransactionalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransService {
    private final TransRepository transRepository;
    private final ExArticleRepository exArticleRepository;
    private final UserService userService;

    public List<TransResponse> selectAllshare() {

        UserResponse userResponse = userService.getUserInfo();
        CustomUser customUser = userResponse.toEntity(userResponse);

        List<TransResponse> transResponse = new ArrayList<>();

        List<ExArticleResponse> exArticleResponses = exArticleRepository
                .findAllByType(ExArticleType.SHARING)
                .stream()
                .map(exArticles -> exArticles.toResponse(exArticles, customUser))
                .filter(exArticleResponse -> exArticleResponse.getExArticleType().equals(ExArticleType.SHARING))
                .collect(Collectors.toList());

        log.info("exArticleResponsesexArticleResponses"+exArticleResponses.toString());
// sharing 만 가져와야한다.

        transResponse = exArticleResponses.stream().map(exArticleResponse -> {
                    TransResponse transResponse1 = new TransResponse();
                    transResponse1.setExArticleResponse(exArticleResponse);

                    Trans trans = transRepository.findByExArticleId(exArticleResponse.getExArticleId())
                            .orElseThrow(() -> new TransException(TransErrorCode.NOT_EXISTS));

                    transResponse1.setPrice(trans.getTrans_sell_price());
                    transResponse1.setId(trans.getId());
                    return transResponse1;
                })
                .collect(Collectors.toList())
        ;


        return transResponse;
    }

}
