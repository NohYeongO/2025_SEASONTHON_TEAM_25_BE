package com.freedom.saving.infra.fss;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
public class FssSavingApiClient {

    private final WebClient webClient;

    @Value("${fss.base-url:https://finlife.fss.or.kr}")
    private String baseUrl;

    @Value("${fss.api-key}")
    private String apiKey;

    public FssSavingApiClient(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public Mono<FssSavingResponseDto> fetchSavings(String topFinGrpNo, int pageNo) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/finlifeapi/savingProductsSearch.json")
                .queryParam("auth", apiKey)           // 인증키
                .queryParam("topFinGrpNo", topFinGrpNo) // 020000: 은행, 030300: 저축은행
                .queryParam("pageNo", pageNo)
                .toUriString();

        return webClient.get().uri(url)
                .retrieve()
                .bodyToMono(FssSavingResponseDto.class);
    }
}
